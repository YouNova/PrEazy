package com.anuvg.preazyadmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class DoctorDetailsActivity extends AppCompatActivity {

    private TextView doctorName;
    private TextView doctorCertificationNumber;
    private TextView doctorContactNumber;
    private TextView doctorEmail;
    private TextView doctorPrescriptionCount;
    private FirebaseAuth deviceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);
        doctorName = findViewById(R.id.textViewDoctorName);
        doctorEmail = findViewById(R.id.textViewDoctorEmail);
        doctorContactNumber = findViewById(R.id.textViewDoctorContactNumber);
        doctorCertificationNumber = findViewById(R.id.textViewDoctorCertificationNumber);
        doctorPrescriptionCount = findViewById(R.id.textViewDoctorPrescriptionCount);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        deviceUser = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String id = extras.getString("Doctor Id");

        assert id != null;
        db.collection("DoctorDb").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            assert doc != null;
                            doctorName.setText(doc.getString("Name"));
                            doctorEmail.setText(doc.getString("Email"));
                            doctorContactNumber.setText(doc.getString("Contact"));
                            doctorCertificationNumber.setText(doc.getString("Certified Number"));
                            long prescriptionCount = Long.parseLong(Objects.requireNonNull(doc.getString("Prescriptions made")));
                            doctorPrescriptionCount.setText(prescriptionCount + "");
                        }
                    }
                });
    }

    //for showing settings menu
    @Override
    public boolean onCreateOptionsMenu(Menu Menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, Menu);
        return true;
    }

    //for redirecting to menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.menuItemLogOut_SettingsPage) {
            deviceUser.signOut();
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}