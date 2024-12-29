package com.notes.notely;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.notes.notely.Home;
import com.notes.notely.Notes;
import com.notes.notely.R;

import java.util.ArrayList;

public class CreateNotes extends AppCompatActivity {
    ImageView back_btn, star_btn, delete_btn, save_btn;
    TextView note_head, note_content;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private int isStarred;
    private FirebaseDatabase database;
    private DatabaseReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_notes);
        back_btn = findViewById(R.id.back_btn);
        save_btn = findViewById(R.id.save_btn);
        note_head = findViewById(R.id.note_heading);
        note_content = findViewById(R.id.note_content);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        collectionReference = database.getReference("Users").child(currentUser.getUid().toString()).child("allNotes");

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!note_head.getText().toString().isEmpty() && !note_content.getText().toString().isEmpty()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateNotes.this);
                    builder.setTitle("Save")
                            .setMessage("Do you want to save note?")
                            .setCancelable(false)
                            .setPositiveButton("save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Save the note
                                    String newId = collectionReference.push().getKey();
                                    boolean isStar = (isStarred == 1);

                                    Notes newNote = new Notes(newId, note_head.getText().toString(), note_content.getText().toString(), isStar, false);
                                    collectionReference.push().setValue(newNote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(CreateNotes.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(CreateNotes.this, Home.class);
                                    startActivity(i);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                            positiveButton.setTextColor(ContextCompat.getColor(CreateNotes.this, R.color.custom_positive_color));

                            Button negativeButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(ContextCompat.getColor(CreateNotes.this, R.color.custom_positive_color));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(CreateNotes.this, Home.class);
        startActivity(i);
        super.onBackPressed();
    }

}