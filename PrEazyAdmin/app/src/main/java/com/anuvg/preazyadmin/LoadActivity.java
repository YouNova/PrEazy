package com.anuvg.preazyadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoadActivity extends AppCompatActivity {

    private FirebaseAuth deviceUser;
    private FirebaseAuth.AuthStateListener deviceUserListener;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        deviceUser = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //check if user already logged in
        deviceUserListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //User already signed in
                    String uid = deviceUser.getUid();
                    goToNextPage(uid);
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    //redirects to next page by checking mode
    private void goToNextPage(String uid) {
        db.collection("UserTypeCategorize").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (Objects.equals(document.getString("Category"), "Admin")) {
                        Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    //it is to add the state of the user in device,i.e. ,signed in or not
    @Override
    protected void onStart() {
        super.onStart();
        deviceUser.addAuthStateListener(deviceUserListener);
    }

    //remove the state of user in device added in onStart()
    @Override
    protected void onStop() {
        super.onStop();
        if (deviceUserListener != null) {
            deviceUser.removeAuthStateListener(deviceUserListener);
        }
    }
}