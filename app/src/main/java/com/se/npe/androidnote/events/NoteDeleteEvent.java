package com.se.npe.androidnote.events;

import com.se.npe.androidnote.models.Note;

public class NoteDeleteEvent extends NoteEvent {
    public NoteDeleteEvent(Note note)
    {
        super(note);
    }
}
