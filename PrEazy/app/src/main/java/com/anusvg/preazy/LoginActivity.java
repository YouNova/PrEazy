package com.anusvg.preazy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailId;
    private EditText password;
    private FirebaseAuth deviceUser;
    private FirebaseFirestore db;
    private TextView DisplayPassword;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        //setting values by id
        Button loginButton = findViewById(R.id.buttonLogin_LoginPage);
        Button signUpButton = findViewById(R.id.buttonSignUp_LoginPage);
        emailId = findViewById(R.id.editTextEmail_LoginPage);
        password = findViewById(R.id.editTextPassword_LoginPage);
        DisplayPassword = findViewById(R.id.ToggleTextView_password);
        deviceUser = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        DisplayPassword.setVisibility(View.GONE);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        DisplayPassword.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (DisplayPassword.getText() == "SHOW") {
                    DisplayPassword.setText("HIDE");
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setSelection(password.length());
                } else {
                    DisplayPassword.setText("SHOW");
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setSelection(password.length());
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.getText().length() > 0) {
                    DisplayPassword.setVisibility(View.VISIBLE);
                } else {
                    DisplayPassword.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //when login button pressed redirect to respective mode's home page
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = emailId.getText().toString().trim();
                String passwordString = password.getText().toString().trim();

                if (emailString.equals("")) {
                    emailId.setError("Enter email id");
                } else if (passwordString.equals("")) {
                    password.setError("Enter password");
                } else {
                    deviceUser.signInWithEmailAndPassword(emailString, passwordString)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String uid = deviceUser.getUid();
                                        goToNextPage(uid);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Invalid Email or Password, Please Try again or Create an Account", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        //if user not registered go to sign up page
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    //redirects to next page by checking mode
    private void goToNextPage(final String uid) {
        db.collection("UserTypeCategorize").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String id = document.getString("id");
                    String mode = document.getString("Category");

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

                    Toast.makeText(LoginActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                    intent.putExtra("id", id);
                    startActivity(intent);
                    finish();

                } else {
                    Log.d(TAG, ("get failed with " + task.getException().getMessage()));
                }
            }
        });
    }
}