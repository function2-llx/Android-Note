package com.se.npe.androidnote.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.database.Cursor;

import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.INoteCollection;

import java.util.ArrayList;
import java.util.List;

public class TableOperate implements INoteCollection{
    private DBManager manager;
    private SQLiteDatabase db;

    public TableOperate(Context context) {

        manager = DBManager.newInstances(context);
        db = manager.getDataBase();
    }

    public String encodeNote(List<IData> src) {
        String string = "";
        for (int i = 0; i < src.size(); i++) {
            string = string + src.get(i).toString() + "\n";
        }
        return string;
    }

    public List<IData> decodeNote(String src){
        List<IData> content = new ArrayList<IData>();
        String[] StrArray = src.split(" ");
        for (int i = 1; i < StrArray.length; i++) {
            if(StrArray[i].charAt(0) == 'S') {
                String[] tempArray = StrArray[i].split(" ");
                SoundData tempSoundData = new SoundData(tempArray[1],tempArray[2]);
                content.add(tempSoundData);
            }
            else if(StrArray[i].charAt(0) == 'T') {
                String[] tempArray = StrArray[i].split(" ");
                TextData tempTextData = new TextData(tempArray[1]);
                content.add(tempTextData);
            }
            else if(StrArray[i].charAt(0) == 'V') {
                String[] tempArray = StrArray[i].split(" ");
                VideoData tempVideoData = new VideoData(tempArray[1]);
                content.add(tempVideoData);
            }
            else if(StrArray[i].charAt(0) == 'P'){
                String[] tempArray = StrArray[i].split(" ");
                Bitmap mBitmap = BitmapFactory.decodeFile(StrArray[1]);
                PictureData tempPictureData = new PictureData(tempArray[1],mBitmap);
                content.add(tempPictureData);
            }
        }
        return content;
    }

    public List<Note> getAllNotes() {
        ArrayList<Note> Notelist = new ArrayList<Note>();
        Cursor c = db.rawQuery("select * from "+TableConfig.TABLE_NAME, null);
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1),decodeNote(c.getString(2)));
            Notelist.add(temp);
        }
        c.close();
        return Notelist;
    }

    public List<Note> getSearchResult(String parameter){
        ArrayList<Note> Notelist = new ArrayList<Note>();
        Cursor c = db.rawQuery("select * from "+TableConfig.TABLE_NAME+" where "+TableConfig.Note.NOTE_TITLE+"= ?", new String[] { parameter });
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1),decodeNote(c.getString(2)));
            Notelist.add(temp);
        }
        c.close();
        return Notelist;
    }

    public void addNote(Note note){
        db.execSQL("insert into "+TableConfig.TABLE_NAME+" values(?,?)", new Object[] { note.getTitle(),encodeNote(note.getContent()) });
    }

    public Note getNoteAt(int index){
        ArrayList<Note> Notelist = new ArrayList<Note>();
        Cursor c = db.rawQuery("select * from "+TableConfig.TABLE_NAME+" where "+TableConfig.Note.NOTE_ID+"= ?", new String[] { Integer.toString(index) });
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1),decodeNote(c.getString(2)));
            Notelist.add(temp);
        }
        c.close();
        return Notelist.get(0);
    }

    public void setNoteAt(int index, Note note){
        db.execSQL("update "+TableConfig.TABLE_NAME+" set "+TableConfig.Note.NOTE_TITLE+"=?,"+TableConfig.Note.NOTE_CONTENT+"=? where "+TableConfig.Note.NOTE_ID+"=?",
                new Object[] { note.getTitle(), encodeNote(note.getContent()),Integer.toString(index) });
    }

    public void removeNoteAt(int index){
        db.execSQL("delete from "+TableConfig.TABLE_NAME+" where "+TableConfig.Note.NOTE_ID+"=?", new String[] { Integer.toString(index) });
    }

    public void loadFromFile(String fileName){

    }

    public void saveToFile(String fileName){

    }

}