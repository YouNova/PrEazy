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

import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientDetailsActivity extends AppCompatActivity {

    private TextView patientName;
    private TextView patientEmail;
    private TextView patientContactNumber;
    private TextView patientAge;
    private TextView patientHeight;
    private TextView patientGender;
    private TextView patientWeight;
    private TextView patientBloodGroup;
    private TextView patientAllergies;
    private Integer age;
    private String dateOfBirth;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String currentYear;
    private String Year;
    private String currentMonth;
    private String Month;
    private String currentDay;
    private String Day;
    private FirebaseAuth deviceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        patientName = findViewById(R.id.textViewPatientName);
        patientEmail = findViewById(R.id.textViewPatientEmail);
        patientContactNumber = findViewById(R.id.textViewPatientContactNumber);
        patientAge = findViewById(R.id.textViewPatientAge);
        patientHeight = findViewById(R.id.textViewPatientHeight);
        patientGender = findViewById(R.id.textViewPatientGender);
        patientWeight = findViewById(R.id.textViewPatientWeight);
        patientBloodGroup = findViewById(R.id.textViewPatientBloodGroup);
        patientAllergies = findViewById(R.id.textViewPatientAllergies);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        deviceUser = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String id = extras.getString("Patient Id");

        assert id != null;
        db.collection("UserDb").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            assert doc != null;
                            dateOfBirth = doc.getString("DoB");
                            if (dateOfBirth != null) {
                                dateImplication(dateOfBirth.substring(0, 10));
                                final String currentTimeStampVal = dateFormat.format(new Date());
                                currentDay = currentTimeStampVal.substring(8, 10);
                                currentMonth = currentTimeStampVal.substring(5, 7);
                                currentYear = currentTimeStampVal.substring(0, 4);
                                deriveAge();
                                patientName.setText(doc.getString("Name"));
                                patientEmail.setText(doc.getString("Email"));
                                patientContactNumber.setText(doc.getString("Contact"));
                                patientAge.setText(age + "");
                                patientHeight.setText(doc.getString("Height"));
                                patientGender.setText(doc.getString("Gender"));
                                patientWeight.setText(doc.getString("Weight"));
                                patientBloodGroup.setText(doc.getString("Blood Group"));
                                patientAllergies.setText(doc.getString("Allergies"));
                            }
                        }
                    }
                });
    }

    private void dateImplication(String dateGiven) {
        Year = dateGiven.substring(0, 4);
        Month = dateGiven.substring(5, 7);
        Day = dateGiven.substring(8);
    }

    private void deriveAge() {
        // we have Day, Month and Year of Date Of Birth and today
        //here we need to get current date and compare it with dOB in Day,Month,Year and display age
        age = Integer.parseInt(currentYear) - Integer.parseInt(Year);
        if (Integer.parseInt(currentMonth) < Integer.parseInt(Month)) {
            age--;
        }
        if ((currentMonth.equals(Month)) && (Integer.parseInt(currentDay) < Integer.parseInt(Day))) {
            age--;
        }
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