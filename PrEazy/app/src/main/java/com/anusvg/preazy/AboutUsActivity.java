package com.anusvg.preazy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    private String Context;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Intent intent = getIntent();
        Context = intent.getStringExtra("context");
        id = intent.getStringExtra("UserId");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Context.equals("PharmacyQRScan")) {
            Intent intent = new Intent(getApplicationContext(), PharmacyQRScanActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        finish();
    }
}