package com.anusvg.preazy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadPageActivity extends AppCompatActivity {

    private static final String TAG = "LoadPage";
    private FirebaseAuth deviceUser;
    private FirebaseAuth.AuthStateListener deviceUserListener;
    private FirebaseFirestore db;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_page);
        deviceUser = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        getSupportActionBar().hide();

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
                    Intent intent = new Intent(getApplicationContext(), StartPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    //redirects to next page by checking mode
    private void goToNextPage(final String uid) {
        db.collection("UserTypeCategorize").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String id = document.getString("id");
                    mode = document.getString("Category");
                    Intent intent;
                    switch (mode) {
                        case "Do Not Login":
                            deviceUser.getCurrentUser().delete();
                            deviceUser.signOut();
                            Toast.makeText(getApplicationContext(), "Invalid User", Toast.LENGTH_SHORT).show();
                            return;

                        case "DoctorDb":
                            intent = new Intent(getApplicationContext(), DoctorCreatePrescriptionActivity.class);
                            break;

                        case "UserDb":
                            intent = new Intent(getApplicationContext(), PatientPrescriptionListActivity.class);
                            break;

                        case "PharmacyDb":
                            intent = new Intent(getApplicationContext(), PharmacyQRScanActivity.class);
                            break;

                        default:
                            Toast.makeText(getApplicationContext(), "Invalid Login", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "The user id: " + id + " is not registered properly");
                            return;
                    }
                    intent.putExtra("id", id);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(TAG, ("get failed with " + task.getException().getMessage()));
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