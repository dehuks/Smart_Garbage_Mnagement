package com.smart.garbage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);  // Ensure this matches your XML layout name

        // Find the "Request Pickup Now" button
        Button requestPickupButton = findViewById(R.id.requestPickupButton);
        requestPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the RequestPickupActivity when the button is clicked
                Intent intent = new Intent(DashboardActivity.this, RequestPickupActivity.class);
                startActivity(intent);
            }
        });

        // Find the "Go to Map Screen" button
        Button goToMapButton = findViewById(R.id.goToMapButton);
        goToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the MapScreenActivity when the button is clicked
                Intent intent = new Intent(DashboardActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
