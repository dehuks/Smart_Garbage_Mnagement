package com.smart.garbage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find all buttons
        Button requestPickupButton = findViewById(R.id.requestPickupButton);
        Button goToMapButton = findViewById(R.id.goToMapButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Request Pickup button click listener
        requestPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, RequestPickupActivity.class);
                startActivity(intent);
            }
        });

        // Go to Map button click listener
        goToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        // Logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });

        // Set user email
        TextView userEmailText = findViewById(R.id.userEmailText);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmailText.setText(currentUser.getEmail());
        }
    }

    private void performLogout() {
        try {
            mAuth.signOut();
            // Show success message
            Toast.makeText(DashboardActivity.this,
                    "Logged out successfully",
                    Toast.LENGTH_SHORT).show();

            // Navigate back to login screen
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            // Clear the back stack so user can't go back to dashboard after logout
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            // Show error message if logout fails
            Toast.makeText(DashboardActivity.this,
                    "Logout failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        if (mAuth.getCurrentUser() == null) {
            // User is not signed in, return to login screen
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}