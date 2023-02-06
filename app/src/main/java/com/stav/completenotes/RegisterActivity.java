package com.stav.completenotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseHandler firebaseHandler;
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupUsername, signupMobile, signupDOB, signupName;
    private RadioGroup genderGroup;
    private RadioButton buttonGenderSelected;
    private Button signupBtn;
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initializing Firebase Values
        firebaseHandler = new FirebaseHandler();
        auth = firebaseHandler.getAuth();

        // Initializing Design Values
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupUsername = findViewById(R.id.signupUsername);
        genderGroup = findViewById(R.id.signupGender);
        signupMobile = findViewById(R.id.signupMobile);
        signupDOB = findViewById(R.id.signupDOB);
        signupName = findViewById(R.id.signupName);

        signupBtn = findViewById(R.id.signupBtn);

        // Setting up DatePicker on EditText
        signupDOB.setOnClickListener(v -> {
            final Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);

            picker = new DatePickerDialog(RegisterActivity.this, R.style.AppTheme_DialogTheme, (view, year1, month1, dayOfMonth) ->
                    signupDOB.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);

            picker.show();
        });
    }

    // registerOnClick function, set onClick on activity_register.xml in button code.
    public void registerOnClick(View view) {
        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();
        String username = signupUsername.getText().toString();
        String gender = "";
        String mobile = signupMobile.getText().toString();
        String dob = signupDOB.getText().toString();
        String name = signupName.getText().toString();

        int buttonGender = genderGroup.getCheckedRadioButtonId();
        buttonGenderSelected = findViewById(buttonGender);


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            signupEmail.setError("Email is required");
            signupEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            signupEmail.setError("Email is not valid");
            signupEmail.requestFocus();
        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            signupUsername.setError("Username is required");
            signupUsername.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            signupPassword.setError("Password is required");
            signupPassword.requestFocus();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must contain at least 6 digits ", Toast.LENGTH_SHORT).show();
            signupPassword.setError("Password is required");
            signupPassword.requestFocus();
        } else if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, "Please enter a mobile phone number", Toast.LENGTH_SHORT).show();
            signupMobile.setError("Phone number is required");
            signupMobile.requestFocus();
        } else if (mobile.length() != 10) {
            Toast.makeText(this, "Please enter a valid mobile phone number, 10 digits", Toast.LENGTH_SHORT).show();
            signupMobile.setError("Phone number is not valid");
            signupMobile.requestFocus();
        } else if (TextUtils.isEmpty(dob)) {
            Toast.makeText(this, "Please enter a dob", Toast.LENGTH_SHORT).show();
            signupDOB.setError("DOB is required");
            signupDOB.requestFocus();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            signupDOB.setError("Name is required");
            signupDOB.requestFocus();
        } else if (genderGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
        } else {
            gender = buttonGenderSelected.getText().toString();
            CreateUser(email, password, username, gender, mobile, dob, name);
        }
    }

    public void redirectLoginOnClick(View view) {
        Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    private void CreateUser(String email, String password, String username, String gender, String mobile, String dob, String name) {
        firebaseHandler.createAccount(email, password, success -> {
            if (success) {
                runOnUiThread(() -> { // Instead of new runnable, using () because no values needed in runnable
                    FirebaseUser user = auth.getCurrentUser();

                    // Update Display Name of User
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    user.updateProfile(profileChangeRequest);

                    // Enter data into realtime firebase
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(username, gender, mobile, dob);

                    // Extracting user reference from database for "RegisteredUsers"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("RegisteredUsers");
                    referenceProfile.child(user.getUid()).setValue(writeUserDetails).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Register completed, Please verify your email", Toast.LENGTH_SHORT).show();
                            // Going back to login activity
                            Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                            // Prevents from user to go back.
                            loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(loginActivity);

                            finish(); // to close this activity
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });

                });
            }
            // Instead of new runnable, using () because no values needed in runnable
        }, error -> runOnUiThread(() -> Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show()));
    }
}