package com.se.npe.androidnote.events;

import com.se.npe.androidnote.models.Note;

public class NoteSelectEvent extends NoteEvent{

    public NoteSelectEvent(Note note)
    {
        super(note);
    }

}
