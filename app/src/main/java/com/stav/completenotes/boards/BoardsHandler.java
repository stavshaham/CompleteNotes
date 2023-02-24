package com.stav.completenotes.boards;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stav.completenotes.Listener;
import com.stav.completenotes.firebase.ReadWriteUserDetails;

public class BoardsHandler {

    private FirebaseAuth auth;

    public BoardsHandler(FirebaseAuth auth) {
        this.auth = auth;
    }

    public void getUserProjects(FirebaseUser user, Listener<Boolean> hasProjects, Listener<Board[]> boards) {
        // Extract the details from user
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("UsersProjects");
        referenceProfile.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails  = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    hasProjects.onListen(true);
                } else {
                    hasProjects.onListen(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
