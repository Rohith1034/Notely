package com.notes.notely;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Notes> notesArrayList;

    FirebaseDatabase db;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    public NotesAdapter(Context context, ArrayList<Notes> notesArrayList) {
        this.context = context;
        this.notesArrayList = notesArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.note_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notes currentNote = notesArrayList.get(position);

        if (currentNote != null) {
            // Safely handle null content
            String noteHeading = currentNote.getNote_heading();
            String noteContent = currentNote.getNote_content();

            // Set the note heading
            if (noteHeading != null) {
                holder.noteHeading.setText(noteHeading);
            } else {
                holder.noteHeading.setText("Untitled Note"); // Fallback text for null heading
            }

            // Safely handle note content
            if (noteContent != null) {
                String trimedContent = noteContent.trim();
                holder.layout.setTag(currentNote.getNoteId());

                // Limit content length to 100 characters
                if (trimedContent.length() > 100) {
                    trimedContent = trimedContent.substring(0, 100);
                }
                holder.noteContent.setText(trimedContent);
            } else {
                holder.noteContent.setText("No content available"); // Fallback text for null content
            }
        }
    }


    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView noteHeading,noteContent;
        public ConstraintLayout layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.constraintLayout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ViewActivity.class);
                    i.putExtra("note_id",v.getTag().toString());
                    v.getContext().startActivity(i);
                }
            });
            noteHeading = itemView.findViewById(R.id.noteHeading);
            noteContent = itemView.findViewById(R.id.noteContent);
        }
    }

}
