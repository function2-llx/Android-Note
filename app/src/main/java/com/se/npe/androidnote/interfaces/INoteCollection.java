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
    List<Note> getAllNotes(String groupName,List<String> tagList);

    List<Note> fuzzySearch(String parameter,String groupName,List<String> tagList);

    void addGroup(String groupName);

    void removeGroup(String groupName);

    List<String> getAllGroup();

    void addNote(Note note);

    void setNote(Note note);

    void removeNote(Note note);

    void removeAllNotes();
}