package com.notes.notely;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private TextView emailTextView, passwordTextView, confirmPasswordTextView;
    private Button sendButton, checkButton;
    TextInputLayout etPasswordLayout;
    private DatabaseReference db;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize views by ID
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.pass);
        confirmPasswordEditText = findViewById(R.id.confirmpassword);
        emailTextView = findViewById(R.id.textView7);
        passwordTextView = findViewById(R.id.textView15);
        confirmPasswordTextView = findViewById(R.id.textView8);
        sendButton = findViewById(R.id.send_btn);
        checkButton = findViewById(R.id.check);
        etPasswordLayout = findViewById(R.id.etPasswordLayout);
        id = "";

        // Initially hide password fields and check button
        passwordTextView.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.GONE);
        confirmPasswordTextView.setVisibility(View.GONE);
        confirmPasswordEditText.setVisibility(View.GONE);
        checkButton.setVisibility(View.GONE);

        // Set OnClickListener for the Send button to send password reset email
        sendButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(ChangePassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            sendPasswordResetEmail(email);
        });

        // Set OnClickListener for the Check button to show password fields
        checkButton.setOnClickListener(v -> {
            if (confirmPasswordEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()) {
                Toast.makeText(ChangePassword.this,"Enter all fields",Toast.LENGTH_SHORT).show();
            }
            else if (!confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {
                Toast.makeText(ChangePassword.this,"Passwords mismatch",Toast.LENGTH_SHORT).show();
            }
            else {
                // Now update the password
                updatePassword();
            }
        });
    }

    // Method to send password reset email using Firebase Authentication
    private void sendPasswordResetEmail(String email) {
        db = FirebaseDatabase.getInstance().getReference("Users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userExist = false;
                for (DataSnapshot notes : snapshot.getChildren()) {
                    if (notes.child("email").getValue(String.class).equals(email)) {
                        id = notes.getKey().toString();
                        userExist = true;
                        break;
                    }
                }
                if (userExist) {
                    emailTextView.setVisibility(View.GONE);
                    emailEditText.setVisibility(View.GONE);
                    passwordTextView.setVisibility(View.VISIBLE);
                    passwordEditText.setVisibility(View.VISIBLE);
                    confirmPasswordTextView.setVisibility(View.VISIBLE);
                    confirmPasswordEditText.setVisibility(View.VISIBLE);
                    etPasswordLayout.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                    checkButton.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ChangePassword.this, "Email not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    // Method to update the user's password
    private void updatePassword() {
        String newPassword = confirmPasswordEditText.getText().toString().trim();

        // Using Firebase Authentication to update the password
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Use the existing email to update the password
        auth.sendPasswordResetEmail(emailEditText.getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Password reset email sent successfully
                        Toast.makeText(ChangePassword.this, "Password reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
                        // You can navigate the user back to the login screen or do further steps as needed.
                    } else {
                        // Error occurred
                        Toast.makeText(ChangePassword.this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
