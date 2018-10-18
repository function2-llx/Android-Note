package com.se.npe.androidnote.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.database.Cursor;
import android.content.ContentValues;
import android.support.design.widget.TabLayout;
import android.util.Log;

import com.se.npe.androidnote.events.ClearEvent;
import com.se.npe.androidnote.events.NoteDeleteEvent;
import com.se.npe.androidnote.events.NoteEvent;
import com.se.npe.androidnote.events.NoteModifyEvent;
import com.se.npe.androidnote.events.NoteSelectEvent;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.INoteCollection;
import com.se.npe.androidnote.events.DatabaseModifyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class TableOperate implements INoteCollection{
    private DBManager manager;
    private SQLiteDatabase db;
    private static TableOperate tableOperate;

    public static void init(Context context)
    {
        tableOperate = new TableOperate(context);
    }

    public static TableOperate getInstance()
    {
        if (tableOperate == null) {
            throw new NullPointerException("Table Operate needs to be initialized using TableOperate.init().");
        }
        return tableOperate;
    }

    private TableOperate(Context context) {
        manager = DBManager.newInstances(context);
        db = manager.getDataBase();
        EventBus.getDefault().register(this);
    }

    public void clearTable()
    {
        db.delete(TableConfig.TABLE_NAME,null,null);
    }

    public String encodeNote(List<IData> src) {
        String string = "";
        for (int i = 0; i < src.size(); i++) {
            if(i == src.size()-1)string = string + src.get(i).toString();
            else string = string + src.get(i).toString() + "qwert";
        }
        return string;
    }

    public List<IData> decodeNote(String src){
        Log.d("debug0001","decode:"+src);
        List<IData> content = new ArrayList<IData>();
        String[] StrArray = src.split("qwert");
        for (int i = 0; i < StrArray.length; i++) {
            Log.d("debug0001","str:"+StrArray[i]);
            if(StrArray[0].length() == 0) continue;
            if(StrArray[i].charAt(0) == 'S') {
                String[] tempArray = StrArray[i].split("asdfg");
                SoundData tempSoundData = new SoundData(tempArray[1],tempArray[2]);
                content.add(tempSoundData);
            }
            else if(StrArray[i].charAt(0) == 'T') {
                String[] tempArray = StrArray[i].split("asdfg");
                TextData tempTextData = new TextData(tempArray[1]);
                content.add(tempTextData);
            }
            else if(StrArray[i].charAt(0) == 'V') {
                String[] tempArray = StrArray[i].split("asdfg");
                VideoData tempVideoData = new VideoData(tempArray[1]);
                content.add(tempVideoData);
            }
            else if(StrArray[i].charAt(0) == 'P'){
                String[] tempArray = StrArray[i].split("asdfg");
//                Bitmap mBitmap = BitmapFactory.decodeFile(StrArray[1]);
                PictureData tempPictureData = new PictureData(tempArray[1]);
                content.add(tempPictureData);
            }
        }
        Log.d("debug0001","test:"+encodeNote(content));
        return content;
    }

    public List<Note> getAllNotes() {

        ArrayList<Note> Notelist = new ArrayList<Note>();
        Cursor c = db.rawQuery("select * from "+TableConfig.TABLE_NAME, null);
        while (c.moveToNext()) {
            Log.d("debug0001",c.getString(0)+" "+c.getString(1)+" "+c.getString(2));
            Note temp = new Note(c.getString(1),decodeNote(c.getString(2)),c.getInt(0),c.getString(3),c.getString(4));
            Notelist.add(temp);
        }
        c.close();
        return Notelist;
    }

    public List<Note> getSearchResult(String parameter){
        ArrayList<Note> Notelist = new ArrayList<Note>();
        Cursor c = db.rawQuery("select * from "+TableConfig.TABLE_NAME+" where "+TableConfig.Note.NOTE_TITLE+"= ?", new String[] { parameter });
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1),decodeNote(c.getString(2)),c.getInt(0),c.getString(3),c.getString(4));
            Notelist.add(temp);
        }
        c.close();
        return Notelist;
    }

    public List<Note> getSearchResultFuzzy(String parameter)
    {
        ArrayList<Note> Notelist = new ArrayList<Note>();
        String sql2 = "select * from " + TableConfig.TABLE_NAME
                + " where " + TableConfig.Note.NOTE_TITLE + " like '%"+parameter+"%'";
        Cursor c = db.rawQuery(sql2,null);
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1),decodeNote(c.getString(2)),c.getInt(0),c.getString(3),c.getString(4));
            Notelist.add(temp);
        }
        c.close();
        return Notelist;
    }

    public void addNote(Note note){
        Log.d("debug0001","insert into "+TableConfig.TABLE_NAME+" values("+note.getTitle()+","+encodeNote(note.getContent())+")");
        ContentValues cValue = new ContentValues();
        cValue.put(TableConfig.Note.NOTE_TITLE,note.getTitle());
        cValue.put(TableConfig.Note.NOTE_CONTENT,encodeNote(note.getContent()));
        cValue.put(TableConfig.Note.NOTE_STARTTIME,Long.toString(note.getStarttime().getTime()));
        cValue.put(TableConfig.Note.NOTE_MODIFYTIME,Long.toString(note.getModifytime().getTime()));
        db.insert(TableConfig.TABLE_NAME,null,cValue);
        String sql = "select * from "+TableConfig.TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToLast();
        int count = cursor.getInt(0);
        cursor.close();
        Log.d("debug0001",Integer.toString(count));
        note.setindex(count);

        EventBus.getDefault().post(new DatabaseModifyEvent("new note"));
    }

    public Note getNoteAt(int index){
        ArrayList<Note> Notelist = new ArrayList<Note>();
        Cursor c = db.rawQuery("select * from "+TableConfig.TABLE_NAME+" where "+TableConfig.Note.NOTE_ID+"= ?", new String[] { Integer.toString(index) });
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1),decodeNote(c.getString(2)),c.getInt(0),c.getString(3),c.getString(4));
            Notelist.add(temp);
        }
        c.close();
        return Notelist.get(0);
    }

    public void setNoteAt(int index, Note note){
        db.execSQL("update "+TableConfig.TABLE_NAME+" set "+TableConfig.Note.NOTE_TITLE+"=?,"+TableConfig.Note.NOTE_STARTTIME+"=?,"+TableConfig.Note.NOTE_MODIFYTIME+"=?,"+TableConfig.Note.NOTE_CONTENT+"=? where "+TableConfig.Note.NOTE_ID+"=?",
                new Object[] { note.getTitle(),Long.toString(note.getStarttime().getTime()),Long.toString(note.getModifytime().getTime()), encodeNote(note.getContent()),Integer.toString(index) });
        note.setindex(index);

        EventBus.getDefault().post(new DatabaseModifyEvent("modify note"));
    }

    public void removeNoteAt(int index){
        db.execSQL("delete from "+TableConfig.TABLE_NAME+" where "+TableConfig.Note.NOTE_ID+"=?", new String[] { Integer.toString(index) });
        EventBus.getDefault().post(new DatabaseModifyEvent("delete note"));
    }

    public void loadFromFile(String fileName){

    }

    public void saveToFile(String fileName){

    }

    @Override
    protected void finalize() throws Throwable {
        EventBus.getDefault().unregister(this);
        super.finalize();
    }

    @Subscribe (sticky = true)
    public void onReceiveNote(NoteModifyEvent event)
    {
        Note note = event.getNote();
        if (note.getIndex() == -1)
            addNote(note);
        else
            setNoteAt(note.getIndex(), note);
        System.err.print(note.getTitle());
    }

    @Subscribe (sticky = true)
    public void onDeleteNote(NoteDeleteEvent event)
    {
        this.removeNoteAt(event.getNote().getIndex());
    }

    @Subscribe (sticky = true)
    public void receiveClearEvent(ClearEvent event)
    {
        int size = getAllNotes().size();
        for (int i = 0; i < size; i++) {
            removeNoteAt(0);
        }
    }
}