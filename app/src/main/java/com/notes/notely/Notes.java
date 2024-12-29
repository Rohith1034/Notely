package com.notes.notely;

import com.google.firebase.database.PropertyName;

public class Notes {
    public String noteId;
    public String note_heading;
    @PropertyName("note_content")
    public String note_content;
    public boolean isStarred;
    public boolean isDeleted;

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNote_heading() {
        return note_heading;
    }

    public void setNote_heading(String note_heading) {
        this.note_heading = note_heading;
    }

    public String getNote_content() {
        return note_content;
    }

    public void setNote_content(String note_content) {
        this.note_content = note_content;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Notes() {
    }

    public Notes(String noteId, String note_heading, String note_content, boolean isStarred, boolean isDeleted) {
        this.noteId = noteId;
        this.note_heading = note_heading;
        this.note_content = note_content;
        this.isStarred = isStarred;
        this.isDeleted = isDeleted;
    }
}
