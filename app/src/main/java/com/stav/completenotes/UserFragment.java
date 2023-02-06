package com.stav.completenotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserFragment extends Fragment {

    private TextView textViewWelcome, textViewName, textViewEmail, textViewDoB, textViewGender, textViewPhone;
    private String name, email, dob, gender, phone, username;
    private ImageView imageView, genderImageView;

    private FirebaseHandler firebaseHandler;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initializing Firebase Values
        firebaseHandler = new FirebaseHandler();
        auth = firebaseHandler.getAuth();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initializing values
        textViewWelcome = view.findViewById(R.id.text_show_welcome);
        textViewEmail = view.findViewById(R.id.text_show_email);
        textViewName = view.findViewById(R.id.text_show_name);
        textViewDoB = view.findViewById(R.id.text_show_dob);
        textViewGender = view.findViewById(R.id.text_show_gender);
        textViewPhone = view.findViewById(R.id.text_show_phone);
        genderImageView = view.findViewById(R.id.ic_gender);

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getActivity(), "Something went wrong! User's details are not available at this moment", Toast.LENGTH_SHORT).show();
        } else {
            showUserProfile(user);
        }
    }

    private void showUserProfile(FirebaseUser user) {
        String uid = user.getUid();

        // Extract the details from user
//        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("RegisteredUsers");
//        referenceProfile.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
//                if (readUserDetails != null) {
//                    name = user.getDisplayName();
//                    email = user.getEmail();
//                    dob = readUserDetails.getDob();
//                    username = readUserDetails.getUsername();
//                    phone = readUserDetails.getPhone();
//                    gender = readUserDetails.getGender();
//
//                    textViewWelcome.setText("Welcome " + name);
//                    textViewName.setText(username);
//                    textViewEmail.setText(email);
//                    textViewDoB.setText(dob);
//                    textViewGender.setText(gender);
//                    textViewPhone.setText(phone);
//
//                    if (gender.equalsIgnoreCase("male")) {
//                        genderImageView.setImageResource(R.drawable.ic_baseline_male_24);
//                    } else {
//                        genderImageView.setImageResource(R.drawable.ic_baseline_female_24);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
//            }
//        });

        firebaseHandler.extractUserDetails(uid, userDetails -> {
            name = user.getDisplayName();
            email = user.getEmail();
            dob = userDetails.getDob();
            username = userDetails.getUsername();
            phone = userDetails.getPhone();
            gender = userDetails.getGender();

            textViewWelcome.setText("Welcome " + name);
            textViewName.setText(username);
            textViewEmail.setText(email);
            textViewDoB.setText(dob);
            textViewGender.setText(gender);
            textViewPhone.setText(phone);

            if (gender.equalsIgnoreCase("male")) {
                genderImageView.setImageResource(R.drawable.ic_baseline_male_24);
            } else {
                genderImageView.setImageResource(R.drawable.ic_baseline_female_24);
            }
        });
    }
}