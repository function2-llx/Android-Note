package com.se.npe.androidnote.events;

import com.se.npe.androidnote.models.Note;

public class NoteModifyEvent extends NoteEvent{

    public NoteModifyEvent(Note note)
    {
        super(note);
    }
}
