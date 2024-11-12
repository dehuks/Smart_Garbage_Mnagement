package com.smart.garbage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;



import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView signupLink;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Hardcoded credentials
    private static final String HARD_CODED_EMAIL = "user@example.com";
    private static final String HARD_CODED_PASSWORD = "password123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signupLink = findViewById(R.id.signupLink);

        // Set click listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (validateInput(email, password)) {
                    performLogin(email, password);
                }
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            return false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return false;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private void performLogin(String email, String password) {
        // Disable login button to prevent multiple attempts
        loginButton.setEnabled(false);

        // Show loading state
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();

        db.collection("Clients")
                .document(email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String storedPassword = document.getString("password");

                            if (password.equals(storedPassword)) {
                                // Show success message
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                // Create and start dashboard activity
                                Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                                dashboardIntent.putExtra("email", email);
                                dashboardIntent.putExtra("client_name", document.getString("client_name"));
                                startActivity(dashboardIntent);
                                finish();
                            } else {
                                // Handle invalid password
                                Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                                passwordInput.setError("Invalid password");
                                loginButton.setEnabled(true);
                            }
                        } else {
                            // Handle non-existent user
                            Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            emailInput.setError("User not found");
                            loginButton.setEnabled(true);
                        }
                    } else {
                        // Handle database error
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() :
                                "Login failed";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        loginButton.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle network or other errors
                    Toast.makeText(LoginActivity.this, "Network error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    loginButton.setEnabled(true);
                });
    }
}
