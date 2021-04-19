package com.anuvg.preazyadmin;

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

public class PharmacyDetailsActivity extends AppCompatActivity {

    private TextView pharmacyName;
    private TextView pharmacyEmail;
    private TextView pharmacyContactNumber;
    private TextView pharmacyLicenseNumber;
    private TextView pharmacyAddress;
    private FirebaseAuth deviceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_details);
        pharmacyName = findViewById(R.id.textViewPharmacyName);
        pharmacyEmail = findViewById(R.id.textViewPharmacyEmail);
        pharmacyContactNumber = findViewById(R.id.textViewPharmacyContactNumber);
        pharmacyLicenseNumber = findViewById(R.id.textViewPharmacyLicenseNumber);
        pharmacyAddress = findViewById(R.id.textViewPharmacyAddress);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        deviceUser = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String id = extras.getString("Pharmacy Id");

        assert id != null;
        db.collection("PharmacyDb").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            assert doc != null;
                            pharmacyName.setText(doc.getString("Name"));
                            pharmacyEmail.setText(doc.getString("Email"));
                            pharmacyContactNumber.setText(doc.getString("Contact"));
                            pharmacyLicenseNumber.setText(doc.getString("License No"));
                            pharmacyAddress.setText(doc.getString("Address"));
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