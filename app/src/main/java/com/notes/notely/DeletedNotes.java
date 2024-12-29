package com.notes.notely;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeletedNotes extends AppCompatActivity {
    FirebaseUser currentUser;
    DatabaseReference db;
    private ArrayList<Notes> notesArrayList;
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    ImageView menu_btn, noNotes;
    TextView notext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deleted_notes);
        notesArrayList = new ArrayList<>();
        noNotes = findViewById(R.id.imageView9);
        notext = findViewById(R.id.notext);
        recyclerView = findViewById(R.id.recyclerView);
        menu_btn = findViewById(R.id.menu_btn);

        // Initialize adapter and RecyclerView
        adapter = new NotesAdapter(this, notesArrayList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Add item decoration for spacing
        int spanCount = 2; // Number of columns
        int spacingDp = 15; // Spacing in dp
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(this, spanCount, spacingDp));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("allNotes");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notesArrayList.clear(); // Clear the existing list before adding new data
                for (DataSnapshot notes : snapshot.getChildren()) {
                    if (notes.child("isDeleted").getValue(Boolean.class) != null && notes.child("isDeleted").getValue(Boolean.class)) {
                        Notes newNotes = notes.getValue(Notes.class);
                        notesArrayList.add(newNotes);
                    }
                }

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();

                // Check if there are no starred notes
                if (notesArrayList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    noNotes.setVisibility(View.VISIBLE);
                    notext.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noNotes.setVisibility(View.GONE);
                    notext.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Menu button click listener
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DeletedNotes.this, Menu.class);
                startActivity(i);
            }
        });
    }
}