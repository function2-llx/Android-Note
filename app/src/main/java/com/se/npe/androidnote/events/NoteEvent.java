package com.se.npe.androidnote.events;

import com.se.npe.androidnote.models.Note;

public abstract class NoteEvent {
    protected Note note;

    public  NoteEvent(Note note)
    {
        this.note = note;
    }

    public Note getNote()
    {
        return this.note;
    }
}
