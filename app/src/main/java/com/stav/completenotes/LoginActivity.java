package com.stav.completenotes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseHandler firebaseHandler;
    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initializing Firebase Values
        firebaseHandler = new FirebaseHandler();
        auth = firebaseHandler.getAuth();

        // Initializing Design Values
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);

        // Show/Hide password
        ImageView showHidePwd = findViewById(R.id.imageview_show_hide_pwd);
        showHidePwd.setImageResource(R.drawable.ic_show_pwd);
        showHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    // If password is visible then hide
                    loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Change Icon
                    showHidePwd.setImageResource(R.drawable.ic_show_pwd);

                } else {
                    loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    // Change Icon
                    showHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }
            }
        });
    }

    // registerOnClick function, set onClick on activity_login.xml in button code.
    // The function login with the entered details and if they are correct opening new intent
    public void loginOnClick(View view) {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            loginEmail.setError("Email is required");
            loginEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            loginEmail.setError("Password is required");
            loginEmail.requestFocus();
        } else {
            firebaseHandler.signIn(email, password, success -> {
                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseUser user = auth.getCurrentUser();

                            // Check if email is verified
                            if (user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "You are now logged in.", Toast.LENGTH_SHORT).show();
                                updateUI();
                            } else {
                                user.sendEmailVerification();
                                auth.signOut(); // Sign out user
                                showAlertDialog();
                            }
                        }
                    });
                }
            }, error -> runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }));
        }
    }

    private void showAlertDialog() {
        // Setup alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Verification");
        builder.setMessage("Please verify your email now. You can not login without email verification.");

        // Open email apps if user clicks continue button.
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent emailActivity = new Intent(Intent.ACTION_MAIN);
                emailActivity.addCategory(Intent.CATEGORY_APP_EMAIL);
                emailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);      // To email app in new window and not within the app
                startActivity(emailActivity);
            }
        });

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Show the AlertDialog
        alertDialog.show();
    }

    // The function transfer to the next activity after logged in
    private void updateUI() {
        Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeActivity);
        finish();
    }

    // redirectOnClick function, set onClick on activity_login.xml in button code.
    // If user does not have an account redirect to register activity
    public void redirectRegisterOnClick(View view) {
        Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerActivity);
        finish();
    }

    // Checking if user is already logged in. In such case, loading the home page.
    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            Toast.makeText(LoginActivity.this, "Already logged in!", Toast.LENGTH_SHORT).show();
            updateUI();
        }
    }
}