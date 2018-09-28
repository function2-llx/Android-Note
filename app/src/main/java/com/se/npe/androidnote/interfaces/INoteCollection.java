package com.se.npe.androidnote.interfaces;

import com.se.npe.androidnote.models.Note;

import java.util.List;

/**
 * A wrapper of a list of Note
 * Provides the ability to save and retrieve from file.
 *
 * @author MashPlant
 */

public interface INoteCollection {
    List<Note> getAllNotes();

    List<Note> getSearchResult(String parameter);

    void addNote(Note note);

    Note getNoteAt(int index);

    void setNoteAt(int index, Note Note);

    void removeNoteAt(int index);

    void loadFromFile(String fileName);

    void saveToFile(String fileName);
}