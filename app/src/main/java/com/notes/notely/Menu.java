package com.notes.notely;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Menu extends AppCompatActivity {

    ConstraintLayout home,editProfile, starredNotes, deletedNotes, changePassword, logout;
    TextView userName, email;
    ImageView userImg;
    FirebaseUser currentUser;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        // Initialize UI components
        userImg = findViewById(R.id.userimg);
        userName = findViewById(R.id.username);
        email = findViewById(R.id.email);

        // Firebase listener to update user data
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get gender value safely
                    String gender = snapshot.child("gender").getValue(String.class);
                    if (gender == null || gender.isEmpty() || gender.equals("Male")) {
                        userImg.setImageResource(R.drawable.boyimg1);
                    } else {
                        userImg.setImageResource(R.drawable.girlimg1);
                    }

                    // Get user name and email safely
                    String username = snapshot.child("userName").getValue(String.class);
                    String userEmail = snapshot.child("email").getValue(String.class);

                    if (username != null) {
                        userName.setText(username);
                    }

                    if (userEmail != null) {
                        email.setText(userEmail);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here, if necessary
            }
        });

        home = findViewById(R.id.home);
        editProfile = findViewById(R.id.editprofile);
        starredNotes = findViewById(R.id.starrednotes);
        deletedNotes = findViewById(R.id.deletednotes);
        changePassword = findViewById(R.id.changepassword);
        logout = findViewById(R.id.logout);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, Home.class);
                startActivity(i);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, EditProfile.class);
                startActivity(i);
            }
        });

        starredNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, StarredNotes.class);
                startActivity(i);
            }
        });

        deletedNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, DeletedNotes.class);
                startActivity(i);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this,ChangePassword.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Menu.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
