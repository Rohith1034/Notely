package com.notes.notely;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    EditText username, gender, phone, street, city, zipcode, state, country;
    Button save_changes;
    FirebaseUser currentUser;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI elements
        username = findViewById(R.id.username);
        gender = findViewById(R.id.gender);
        phone = findViewById(R.id.phone);
        street = findViewById(R.id.street);
        city = findViewById(R.id.city);
        zipcode = findViewById(R.id.zipcode);
        state = findViewById(R.id.state);
        country = findViewById(R.id.country);
        save_changes = findViewById(R.id.save_changes_btn);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        // Load user data
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Populate fields with existing data
                    if (snapshot.child("userName").exists()) {
                        username.setText(snapshot.child("userName").getValue(String.class));
                    }
                    if (snapshot.child("gender").exists()) {
                        gender.setText(snapshot.child("gender").getValue(String.class));
                    }
                    if (snapshot.child("phone").exists()) {
                        phone.setText(snapshot.child("phone").getValue(String.class));
                    }
                    if (snapshot.child("address").exists()) {
                        street.setText(snapshot.child("address").getValue(String.class));
                    }
                    if (snapshot.child("city").exists()) {
                        city.setText(snapshot.child("city").getValue(String.class));
                    }
                    if (snapshot.child("zipcode").exists()) {
                        zipcode.setText(snapshot.child("zipcode").getValue(String.class));
                    }
                    if (snapshot.child("state").exists()) {
                        state.setText(snapshot.child("state").getValue(String.class));
                    }
                    if (snapshot.child("country").exists()) {
                        country.setText(snapshot.child("country").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Save changes
        save_changes.setOnClickListener(v -> {
            String updatedUsername = username.getText().toString().trim();
            String updatedGender = gender.getText().toString().trim();
            String updatedPhone = phone.getText().toString().trim();
            String updatedStreet = street.getText().toString().trim();
            String updatedCity = city.getText().toString().trim();
            String updatedZipcode = zipcode.getText().toString().trim();
            String updatedState = state.getText().toString().trim();
            String updatedCountry = country.getText().toString().trim();

            if (updatedGender == "male" || updatedGender == "Male" || updatedGender == "M" || updatedGender == "m") {
                updatedGender = "Male";
            }
            else if (updatedGender == "female" || updatedGender == "Female" || updatedGender == "F" || updatedGender == "f") {
                updatedGender = "Female";
            }
            else {
                updatedGender = "Male";
            }

            db.child("userName").setValue(updatedUsername);
            db.child("gender").setValue(updatedGender);
            db.child("phone").setValue(updatedPhone);
            db.child("address").setValue(updatedStreet);
            db.child("city").setValue(updatedCity);
            db.child("zipcode").setValue(updatedZipcode);
            db.child("state").setValue(updatedState);
            db.child("country").setValue(updatedCountry);

            Toast.makeText(EditProfile.this,"Saved successfully",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(EditProfile.this, Menu.class);
            startActivity(i);
        });
    }
}
