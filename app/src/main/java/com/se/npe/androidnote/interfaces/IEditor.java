package com.se.npe.androidnote.interfaces;

import android.widget.EditText;

import com.se.npe.androidnote.models.Note;


/**
 * The Editor(not the EditorActivity) will implement this interface
 *
 * @author MashPlant
 */

public interface IEditor {
    // the 'add' functions add items to the current cursor, the user needn't care about it
    void addPicture(String picturePath);

    void addVideo(String videoPath);

    EditText addSound(String soundPath);

    void addText(String text);

    void loadNote(Note note);

    Note buildNote();
}
