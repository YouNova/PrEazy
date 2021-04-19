package com.anuvg.preazyadmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private ProgressBar ProgressView;
    private FirebaseAuth DeviceUser;
    private TextView DisplayPassword;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();
        Email = findViewById(R.id.editTextEmail_LoginPage);
        Password = findViewById(R.id.editTextPassword_LoginPage);
        DisplayPassword = findViewById(R.id.ToggleTextView_password);
        Button loginButton = findViewById(R.id.buttonLogin_LoginPage);
        ProgressView = findViewById(R.id.progressBar_LoginPage);
        DeviceUser = FirebaseAuth.getInstance();

        DisplayPassword.setVisibility(View.GONE);
        Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        DisplayPassword.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (DisplayPassword.getText() == "SHOW") {
                    DisplayPassword.setText("HIDE");
                    Password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Password.setSelection(Password.length());
                } else {
                    DisplayPassword.setText("SHOW");
                    Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Password.setSelection(Password.length());
                }
            }
        });

        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Password.getText().length() > 0) {
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

                String emailString = Email.getText().toString().trim();
                String passwordString = Password.getText().toString().trim();

                if (emailString.equals("")) {
                    Email.setError("Enter email id");
                } else if (passwordString.equals("")) {
                    Password.setError("Enter password");
                } else {
                    ProgressView.setVisibility(View.VISIBLE);
                    DeviceUser.signInWithEmailAndPassword(emailString, passwordString)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String uid = DeviceUser.getUid();
                                        goToNextPage(uid);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Invalid Email or Password, Please Try again or Create an Account", Toast.LENGTH_SHORT).show();
                                        ProgressView.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }

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
}