package com.anuvg.preazyadmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class AdminHomeActivity extends AppCompatActivity {

    private FirebaseAuth deviceUser;
    private TextView DoctorCount;
    private TextView PatientCount;
    private TextView PharmacyCount;
    private Integer docCount;
    private Integer patCount;
    private Integer pharCount;
    private PieChartView pieChartView;
    private PieChartData pieChartData;
    private List<SliceValue> pieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        deviceUser = FirebaseAuth.getInstance();
        DoctorCount = findViewById(R.id.textViewDoctorCount);
        PatientCount = findViewById(R.id.textViewPatientCount);
        PharmacyCount = findViewById(R.id.textViewPharmacyCount);
        CardView doctorCardView = findViewById(R.id.cardViewDoctor);
        CardView patientCardView = findViewById(R.id.cardViewPatient);
        CardView pharmacyCardView = findViewById(R.id.cardViewPharmacy);
        pieChartView = findViewById(R.id.countPieChart);
        pieData = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserTypeCategorize").document("Count").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        docCount = Integer.parseInt(Objects.requireNonNull(document.getString("DoctorDb")));
                        patCount = Integer.parseInt(Objects.requireNonNull(document.getString("UserDb")));
                        pharCount = Integer.parseInt(Objects.requireNonNull(document.getString("PharmacyDb")));
                        DoctorCount.setText(docCount.toString());
                        PatientCount.setText(patCount.toString());
                        PharmacyCount.setText(pharCount.toString());
                        pieData.add(new SliceValue(docCount, Color.BLACK).setLabel("Doctors : " + docCount));
                        pieData.add(new SliceValue(patCount, R.color.colorGrey).setLabel("Patients : " + patCount));
                        pieData.add(new SliceValue(pharCount, Color.WHITE).setLabel("Pharmacies : " + pharCount));
                        pieData.add(new SliceValue(0, android.R.color.black).setLabel(""));
                        pieChartData = new PieChartData(pieData);
                        pieChartData.setHasLabels(true);
                        pieChartView.setPieChartData(pieChartData);
                    }
                });

        doctorCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DoctorListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        patientCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        pharmacyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PharmacyListActivity.class);
                startActivity(intent);
                finish();
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

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}