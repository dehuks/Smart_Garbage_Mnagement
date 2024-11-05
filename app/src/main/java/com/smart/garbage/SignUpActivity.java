package com.smart.garbage;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class SignUpActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button signupButton;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        // Initialize views
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signupButton = findViewById(R.id.signupButton);
        loginLink = findViewById(R.id.loginLink);

        // Set click listeners
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();

                if (validateInput(name, email, password, confirmPassword)) {
                    performSignup(name, email, password);
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will return to the LoginActivity
            }
        });
    }

    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            nameInput.setError("Name is required");
            return false;
        }

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

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void performSignup(String name, String email, String password) {
        // TODO: Implement your signup logic here
        // This could involve API calls, database creation, etc.
        Toast.makeText(this, "Creating account...", Toast.LENGTH_SHORT).show();
    }
}
