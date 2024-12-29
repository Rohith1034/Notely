package com.notes.notely;

import static android.app.ProgressDialog.show;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    ImageView back_btn, star_btn, delete_btn, save_btn;
    TextView note_head, note_content;
    int isStarred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view);
        Intent i = getIntent();
        back_btn = findViewById(R.id.back_btn);
        star_btn = findViewById(R.id.star_btn);
        delete_btn = findViewById(R.id.delete_btn);
        save_btn = findViewById(R.id.save_btn);
        isStarred = 0;
        note_head = findViewById(R.id.note_heading);
        note_content = findViewById(R.id.note_content);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("Users").child(currentUser.getUid().toString()).child("allNotes");

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot notes : snapshot.getChildren()) {
                    // Check if noteId and intentNoteId are null
                    String noteId = notes.child("noteId").getValue(String.class);
                    String intentNoteId = i.getStringExtra("note_id");

                    if (noteId != null && intentNoteId != null && noteId.equals(intentNoteId)) {
                        note_head.setText(notes.child("note_heading").getValue(String.class));
                        note_content.setText(notes.child("note_content").getValue(String.class));
                        boolean starred = notes.child("isStarred").getValue(Boolean.class);
                        if (starred) {
                            star_btn.setImageResource(R.drawable.baseline_star_24);
                            delete_btn.setImageResource(R.drawable.baseline_delete_24);
                            save_btn.setImageResource(R.drawable.baseline_save_24);
                        } else {
                            star_btn.setImageResource(R.drawable.baseline_star_outline_24);
                            delete_btn.setImageResource(R.drawable.baseline_delete_outline_24);
                            save_btn.setImageResource(R.drawable.outline_save_24);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle cancellation if needed
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!note_head.getText().toString().isEmpty() && !note_content.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
                    builder.setTitle("Save")
                            .setMessage("Do you want to save note?")
                            .setCancelable(false)
                            .setPositiveButton("save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot notes : snapshot.getChildren()) {
                                                if (notes.child("noteId").getValue(String.class).equals(i.getStringExtra("note_id"))) {
                                                    Notes currentNote = new Notes(notes.child("noteId").toString(), note_head.getText().toString(), note_content.getText().toString(), notes.child("isStarred").getValue(Boolean.class), notes.child("isDeleted").getValue(Boolean.class));
                                                    String key = notes.getKey().toString();
                                                    if (key != null) {
                                                        databaseReference.child(key).setValue(currentNote);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Intent i1 = new Intent(ViewActivity.this, ViewActivity.class);
                                            startActivity(i1);
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                            positiveButton.setTextColor(ContextCompat.getColor(ViewActivity.this, R.color.custom_positive_color));

                            Button negativeButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(ContextCompat.getColor(ViewActivity.this, R.color.custom_positive_color));
                        }
                    });
                }
            }
        });

        star_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
                builder.setTitle("Star")
                        .setMessage("Do tou want to star note?")
                        .setCancelable(false)
                        .setPositiveButton("Star", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot notes : snapshot.getChildren()) {
                                            String noteId = notes.child("noteId").getValue(String.class);
                                            String intentNoteId = i.getStringExtra("note_id");

                                            // Ensure null safety for noteId and intentNoteId
                                            if (noteId != null && intentNoteId != null && noteId.equals(intentNoteId)) {
                                                boolean currentStarred = notes.child("isStarred").getValue(Boolean.class);
                                                boolean newStarred = !currentStarred; // Toggle the value

                                                // Create the updated note object
                                                Notes updatedNote = new Notes(
                                                        noteId,
                                                        notes.child("note_heading").getValue(String.class),
                                                        notes.child("note_content").getValue(String.class),
                                                        newStarred, // Updated isStarred value
                                                        notes.child("isDeleted").getValue(Boolean.class)
                                                );

                                                // Update the note in the database
                                                String key = notes.getKey();
                                                if (key != null) {
                                                    databaseReference.child(key).setValue(updatedNote);
                                                }

                                                // Update the star button image based on the new state
                                                updateStarButton(newStarred);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ViewActivity.this, "Failed to update star status", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Style the buttons inside the alert dialog
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setTextColor(ContextCompat.getColor(ViewActivity.this, R.color.custom_positive_color));

                        Button negativeButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(ContextCompat.getColor(ViewActivity.this, R.color.custom_positive_color));
                    }
                });
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Do you want to delete note?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot notes : snapshot.getChildren()) {
                                            String noteId = notes.child("noteId").getValue(String.class);
                                            String intentNoteId = i.getStringExtra("note_id");
                                            if (noteId != null && intentNoteId != null && noteId.equals(intentNoteId)) {
                                                String key = notes.getKey();
                                                if (key != null) {
                                                    Notes updatedNote = new Notes(
                                                            noteId,
                                                            notes.child("note_heading").getValue(String.class),
                                                            notes.child("note_content").getValue(String.class),
                                                            notes.child("isStarred").getValue(Boolean.class),
                                                            true
                                                    );
                                                    databaseReference.child(key).setValue(updatedNote);
                                                    Toast.makeText(ViewActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();

                                                    Intent i1 = new Intent(ViewActivity.this, Home.class);
                                                    startActivity(i1);
                                                    finish();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ViewActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();  // Dismiss the dialog if cancel is clicked
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Style the buttons inside the alert dialog
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setTextColor(ContextCompat.getColor(ViewActivity.this, R.color.custom_positive_color));

                        Button negativeButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(ContextCompat.getColor(ViewActivity.this, R.color.custom_positive_color));
                    }
                });
            }
        });


    }

    private void updateStarButton(boolean isStarred) {
        if (isStarred) {
            star_btn.setImageResource(R.drawable.baseline_star_24);
            delete_btn.setImageResource(R.drawable.baseline_delete_24);
            save_btn.setImageResource(R.drawable.baseline_save_24);
        } else {
            star_btn.setImageResource(R.drawable.baseline_star_outline_24);
            delete_btn.setImageResource(R.drawable.baseline_delete_outline_24);
            save_btn.setImageResource(R.drawable.outline_save_24);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ViewActivity.this, Home.class);
        startActivity(i);
        super.onBackPressed();
    }
}
