package com.stav.completenotes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stav.completenotes.firebase.FirebaseHandler;

import java.util.Calendar;

public class UserSettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FirebaseHandler firebaseHandler;
    private FirebaseAuth auth;
    private Button saveBtn;
    private EditText editTextName, editTextEmail, editTextDate, editTextGender, editTextPhone;
    private TextView textViewWelcome;
    private String name, email, dob, username, phone, gender;
    private ImageView genderImageView;
    private DatePickerDialog picker;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initializing Firebase Values
        firebaseHandler = new FirebaseHandler();
        auth = firebaseHandler.getAuth();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usersettings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Initializing xml values
        saveBtn = view.findViewById(R.id.saveBtn);
        editTextName = view.findViewById(R.id.edit_text_name);
        editTextEmail = view.findViewById(R.id.edit_text_email);
        editTextDate = view.findViewById(R.id.edit_text_dob);
        editTextGender = view.findViewById(R.id.edit_text_gender);
        editTextPhone = view.findViewById(R.id.edit_text_phone);

        textViewWelcome = view.findViewById(R.id.text_show_welcome);
        genderImageView = view.findViewById(R.id.ic_gender);

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getActivity(), "Something went wrong! User's details are not available at this moment", Toast.LENGTH_SHORT).show();
        } else {
            showUserProfile(user);
        }
        editTextName.setOnClickListener(v -> {
            alertDialogBuilder("Change username", "Enter your username", editTextName, "username");
        });

        editTextPhone.setOnClickListener(v -> {
            alertDialogBuilder("Change phone number", "Enter your phone number", editTextPhone, "phone");
        });

        editTextEmail.setOnClickListener(v -> {
            alertDialogBuilder("Change your email", "Enter your new email", editTextEmail, "email");
        });

        editTextDate.setOnClickListener(v -> {
            final Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);

            picker = new DatePickerDialog(getContext(), R.style.AppTheme_DialogTheme, (vw, year1, month1, dayOfMonth) ->
                    editTextDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);

            picker.show();
        });

        editTextGender.setOnClickListener(v -> {
            final String[] genders = {"Male", "Female"};
            Spinner dropdown = new Spinner(getContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, genders);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown.setAdapter(adapter);
            dropdown.setOnItemSelectedListener(this);
        });

        saveBtn.setOnClickListener(v -> firebaseHandler.saveUserDetails(editTextName.getText().toString(),
                editTextPhone.getText().toString(), editTextDate.getText().toString(),
                editTextGender.getText().toString(), user));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                editTextGender.setText("Male");
                genderImageView.setImageResource(R.drawable.ic_baseline_male_24);
                break;
            case 1:
                editTextGender.setText("Female");
                genderImageView.setImageResource(R.drawable.ic_baseline_female_24);
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void showUserProfile(FirebaseUser user) {
        String uid = user.getUid();

        firebaseHandler.extractUserDetails(uid, userDetails -> {
            name = user.getDisplayName();
            email = user.getEmail();
            dob = userDetails.getDob();
            username = userDetails.getUsername();
            phone = userDetails.getPhone();
            gender = userDetails.getGender();

            textViewWelcome.setHint("Welcome " + name);
            editTextName.setHint(username);
            editTextEmail.setHint(email);
            editTextDate.setHint(dob);
            editTextGender.setHint(gender);
            editTextPhone.setHint(phone);

            if (gender.equalsIgnoreCase("male")) {
                genderImageView.setImageResource(R.drawable.ic_baseline_male_24);
            } else {
                genderImageView.setImageResource(R.drawable.ic_baseline_female_24);
            }
        });
    }

    // alertDialogBuilder function
    // The function builds and alertDialog and checks the necessary things to change the text
    public void alertDialogBuilder(String title, String description, EditText editText, String type) {
        // Setup alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(description);

        final EditText editTextDialog = new EditText(getContext());
        editTextDialog.setTextColor(getResources().getColor(R.color.lavender));
        editTextDialog.setTextSize(18);
        editTextDialog.setHint(description);

        builder.setView(editTextDialog);

        String text = editTextDialog.getText().toString();

        // Open email apps if user clicks continue button.
        builder.setPositiveButton("Confirm", (dialog, which) -> { // Instead of new ... using dialog, which, means onClick because these are the values needed
            if (type == "email") {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(getContext(), "The text should not be empty", Toast.LENGTH_SHORT).show();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                    Toast.makeText(getContext(), "Email is not valid", Toast.LENGTH_SHORT).show();
                } else {
                    editText.setHint(editTextDialog.getText());
                    auth.getCurrentUser().verifyBeforeUpdateEmail(text);
                    Toast.makeText(getContext(), "Please verify your email.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(getContext(), "The text should not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    editText.setHint(editTextDialog.getText());
                }
            }
        });

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Show the AlertDialog
        alertDialog.show();
    }
}