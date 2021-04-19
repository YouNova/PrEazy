package com.anuvg.preazyadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class DoctorListActivity extends AppCompatActivity {

    private RecyclerView doctorRecycler;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<UserDetailCardView> doctorList;
    private FirebaseAuth deviceUser;
    private BarChart barChart;
    private ArrayList<BarEntry> prescriptionCounts;
    private ArrayList<String> doctorIds;
    private BarData barData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        doctorRecycler = findViewById(R.id.recyclerView_doctorList);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        doctorList = new ArrayList<>();
        deviceUser = FirebaseAuth.getInstance();
        barChart = findViewById(R.id.barChartCountDoctorPrescriptions);
        prescriptionCounts = new ArrayList<>();
        doctorIds = new ArrayList<>();

        db.collection("DoctorDb").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot eachDoctor : Objects.requireNonNull(task.getResult())) {
                                String doctorId = eachDoctor.getId();
                                String doctorName = eachDoctor.getString("Name");
                                float prescriptionCount = Float.parseFloat(Objects.requireNonNull(eachDoctor.getString("Prescriptions made")));
                                doctorList.add(new UserDetailCardView(doctorId, doctorName));
                                prescriptionCounts.add(new BarEntry(prescriptionCount, Integer.parseInt(doctorId)));
                                doctorIds.add(doctorId);
                            }
                        }
                        doctorRecycler.hasFixedSize();
                        doctorRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), doctorList);
                        doctorRecycler.setAdapter(recyclerAdapter);
                        BarDataSet prescriptionCountSet = new BarDataSet(prescriptionCounts, "Count");
                        barData = new BarData(doctorIds, prescriptionCountSet);
                        barChart.setData(barData);
                        barChart.setTouchEnabled(true);
                        barChart.setDragEnabled(true);
                    }
                });

        doctorRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(), DoctorDetailsActivity.class);
                        intent.putExtra("Doctor Id", doctorList.get(position).getUserEndId());
                        startActivity(intent);
                    }
                })
        );
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
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
        startActivity(intent);
        finish();
    }
}