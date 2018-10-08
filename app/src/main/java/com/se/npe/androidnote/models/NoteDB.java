package com.se.npe.androidnote.models;

import com.se.npe.androidnote.interfaces.INoteCollection;
import com.se.npe.androidnote.models.MySQLiteOpenHelper;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public class NoteDB implements INoteCollection {
    private static NoteDB manager;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase db;

    private NoteDB() {
        mySQLiteOpenHelper = MySQLiteOpenHelper.getInstance(this);
        if (db == null) {
            db = mySQLiteOpenHelper.getWritableDatabase();
        }
    }

    public static NoteDB newInstances() {
        if (manager == null) {
            manager = new DbManager();
        }
        return manager;
    }
    
    public SQLiteDatabase getDataBase() {
        return db;
    }

    @Override
    public List<Note> getAllNotes() {
        return null;
    }

    @Override
    public List<Note> getSearchResult(String parameter) {
        return null;
    }

    @Override
    public void addNote(Note note) {

    }

    @Override
    public Note getNoteAt(int index) {
        return null;
    }

    @Override
    public void setNoteAt(int index, Note note) {

    }

    @Override
    public void removeNoteAt(int index) {

    }

    @Override
    public void loadFromFile(String fileName) {

    }

    @Override
    public void saveToFile(String fileName) {

    }
}
