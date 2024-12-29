package com.notes.notely;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.notes.notely.Notes;
import com.notes.notely.NotesAdapter;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference collectionReference;
    private ArrayList<Notes> notesArrayList;
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    FloatingActionButton fab;
    ImageView menu_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Initialize the data structures
        notesArrayList = new ArrayList<>();
        adapter = new NotesAdapter(this, notesArrayList);

        // Initialize Firebase Auth and check for the current user
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Redirect to MainActivity if the user is not logged in
        if (currentUser == null) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        menu_btn = findViewById(R.id.menu_btn);
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Menu.class);
                startActivity(i);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        // Example usage in an Activity or Fragment
        int spanCount = 2; // Number of columns (change this as needed)
        int spacingDp = 15; // Set spacing to 15dp

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(this, spanCount, spacingDp));


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, CreateNotes.class);
                startActivity(i);
            }
        });

        // Initialize Firebase Database reference
        db = FirebaseDatabase.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = db.getReference("Users").child(currentUserId).child("allNotes");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot notes:snapshot.getChildren()) {
                    Notes newNote = notes.getValue(Notes.class);
                    if (!notes.child("isDeleted").getValue(Boolean.class))
                        notesArrayList.add(newNote);
                }
                if (notesArrayList.size() <= 1) {
                    Intent i = new Intent(Home.this,NoNote.class);
                    startActivity(i);
                }
                else {
                    notesArrayList.remove(0);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
