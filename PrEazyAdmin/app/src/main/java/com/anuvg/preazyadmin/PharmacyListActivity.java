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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class PharmacyListActivity extends AppCompatActivity {

    private RecyclerView pharmacyRecycler;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<UserDetailCardView> pharmacyList;
    private FirebaseAuth deviceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_list);
        pharmacyRecycler = findViewById(R.id.recyclerView_pharmacyList);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        pharmacyList = new ArrayList<>();
        deviceUser = FirebaseAuth.getInstance();

        db.collection("PharmacyDb").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot eachDoctor : Objects.requireNonNull(task.getResult())) {
                                String doctorId = eachDoctor.getId();
                                String doctorName = eachDoctor.getString("Name");
                                pharmacyList.add(new UserDetailCardView(doctorId, doctorName));
                            }
                        }
                        pharmacyRecycler.hasFixedSize();
                        pharmacyRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), pharmacyList);
                        pharmacyRecycler.setAdapter(recyclerAdapter);
                    }
                });

        pharmacyRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(), PharmacyDetailsActivity.class);
                        intent.putExtra("Pharmacy Id", pharmacyList.get(position).getUserEndId());
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