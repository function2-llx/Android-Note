package com.se.npe.androidnote.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.util.Log;

import com.se.npe.androidnote.events.ClearEvent;
import com.se.npe.androidnote.events.NoteDeleteEvent;
import com.se.npe.androidnote.events.NoteModifyEvent;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.INoteCollection;
import com.se.npe.androidnote.events.DatabaseModifyEvent;
import com.se.npe.androidnote.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class TableOperate implements INoteCollection {
    private static final String LOG_TAG = Note.class.getSimpleName();

    private DBManager manager;
    private SQLiteDatabase db;
    private static File configFile;
    private static TableOperate tableOperate;

    public static void init(Context context) {
        tableOperate = new TableOperate(context);
        File file = new File(TableConfig.SAVE_PATH + "/config");
        if (!file.exists()) {
            file.mkdirs();
        }
        configFile = new File(TableConfig.SAVE_PATH + "/config/searchconfig.txt");
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
                setSearchConfig(-1);
            }
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
    }

    public static TableOperate getInstance() {
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

    public String encodeNote(List<IData> src) {
        String string = "";
        for (int i = 0; i < src.size(); i++) {
            string = string + src.get(i).toString() + TableConfig.Filesave.LIST_SEPARATOR;
        }
        return string;
    }

    public List<IData> decodeNote(String src) {
        List<IData> content = new ArrayList<IData>();
        String[] StrArray = src.split(TableConfig.Filesave.LIST_SEPARATOR);
        for (int i = 0; i < StrArray.length; i++) {
            Log.d("debug0001", "str:" + StrArray[i]);
            if (StrArray[0].length() == 0) continue;
            if (StrArray[i].charAt(0) == 'S') {
                String[] tempArray = StrArray[i].split(TableConfig.Filesave.LINE_SEPARATOR);
                SoundData tempSoundData = new SoundData(tempArray[1], tempArray[2]);
                content.add(tempSoundData);
            } else if (StrArray[i].charAt(0) == 'T') {
                String[] tempArray = StrArray[i].split(TableConfig.Filesave.LINE_SEPARATOR);
                TextData tempTextData = new TextData(tempArray[1]);
                content.add(tempTextData);
            } else if (StrArray[i].charAt(0) == 'V') {
                String[] tempArray = StrArray[i].split(TableConfig.Filesave.LINE_SEPARATOR);
                VideoData tempVideoData = new VideoData(tempArray[1]);
                content.add(tempVideoData);
            } else if (StrArray[i].charAt(0) == 'P') {
                String[] tempArray = StrArray[i].split(TableConfig.Filesave.LINE_SEPARATOR);
                PictureData tempPictureData = new PictureData(tempArray[1]);
                content.add(tempPictureData);
            }
        }
        return content;
    }

    public String listStringToString(List<String> src) {
        String string = "";
        for (int i = 0; i < src.size() - 1; i++) {
            string = string + src.get(i) + TableConfig.Filesave.LINE_SEPARATOR;
        }
        if (src.size() >= 2) string = string + TableConfig.Filesave.LINE_SEPARATOR;
        return string;
    }

    public List<String> stringToListString(String src) {
        if (src.length() == 0) return new ArrayList<String>();
        String[] strings = src.split(TableConfig.Filesave.LINE_SEPARATOR);
        return Arrays.asList(strings);
    }

    public static int getSearchConfig() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            Logger.log(LOG_TAG, e);
            return -2;
        }

        byte b[] = new byte[(int) configFile.length()];
        try {
            int len = inputStream.read(b);
            if (len == -1) return -2;
        } catch (Exception e) {
            Logger.log(LOG_TAG, e);
        }
        String tempcontent = new String(b);
        return Integer.parseInt(tempcontent);
    }

    public static void setSearchConfig(int x) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(configFile);
        } catch (FileNotFoundException e) {
            Logger.log(LOG_TAG, e);
            return;
        }

        String string = Integer.toString(x);

        byte[] bs = string.getBytes();
        try {
            outputStream.write(bs);
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
    }

    @Override
    public List<Note> getAllNotes() {
        ArrayList<Note> noteList = new ArrayList<Note>();
        Cursor c = db.rawQuery("select * from " + TableConfig.TABLE_NAME, null);
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1), decodeNote(c.getString(2)), c.getInt(0), c.getString(3), c.getString(4), stringToListString(c.getString(5)));
            noteList.add(temp);
        }
        c.close();
        return noteList;
    }

    @Override
    public List<Note> getSearchResult(String parameter) {
        ArrayList<Note> noteList = new ArrayList<Note>();
        Cursor c = db.rawQuery("select * from " + TableConfig.TABLE_NAME + " where " + TableConfig.Note.NOTE_TITLE + "= ?", new String[]{parameter});
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1), decodeNote(c.getString(2)), c.getInt(0), c.getString(3), c.getString(4), stringToListString(c.getString(5)));
            noteList.add(temp);
        }
        c.close();
        return noteList;
    }

    @Override
    public List<Note> getSearchResultFuzzy(String parameter) {
        ArrayList<Note> noteList = new ArrayList<Note>();
        String sql2 = "select * from " + TableConfig.TABLE_NAME
                + " where " + TableConfig.Note.NOTE_TITLE + " like '%" + parameter + "%'";
        Cursor c = db.rawQuery(sql2, null);
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1), decodeNote(c.getString(2)), c.getInt(0), c.getString(3), c.getString(4), stringToListString(c.getString(5)));
            noteList.add(temp);
        }
        c.close();
        return noteList;
    }

    @Override
    public void addNote(Note note) {
        Log.d("debug0001", "insert into " + TableConfig.TABLE_NAME + " values(" + note.getTitle() + "," + encodeNote(note.getContent()) + ")");
        ContentValues cValue = new ContentValues();
        cValue.put(TableConfig.Note.NOTE_TITLE, note.getTitle());
        cValue.put(TableConfig.Note.NOTE_CONTENT, encodeNote(note.getContent()));
        cValue.put(TableConfig.Note.NOTE_START_TIME, Long.toString(note.getStarttime().getTime()));
        cValue.put(TableConfig.Note.NOTE_MODIFY_TIME, Long.toString(note.getModifytime().getTime()));
        cValue.put(TableConfig.Note.NOTE_TAG, listStringToString(note.getTag()));
        db.insert(TableConfig.TABLE_NAME, null, cValue);
        String sql = "select * from " + TableConfig.TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToLast();
        int count = cursor.getInt(0);
        cursor.close();
        Log.d("debug0001", Integer.toString(count));
        note.setIndex(count);

        EventBus.getDefault().post(new DatabaseModifyEvent("new note"));
    }

    @Override
    public void setNote(Note note) {
        db.execSQL("update " + TableConfig.TABLE_NAME + " set " + TableConfig.Note.NOTE_TITLE + "=?," + TableConfig.Note.NOTE_TAG + "=?," + TableConfig.Note.NOTE_START_TIME + "=?," + TableConfig.Note.NOTE_MODIFY_TIME + "=?," + TableConfig.Note.NOTE_CONTENT + "=? where " + TableConfig.Note.NOTE_ID + "=?",
                new Object[]{note.getTitle(), listStringToString(note.getTag()), Long.toString(note.getStarttime().getTime()), Long.toString(note.getModifytime().getTime()), encodeNote(note.getContent()), Integer.toString(note.getIndex())});

        EventBus.getDefault().post(new DatabaseModifyEvent("modify note"));
    }

    @Override
    public void removeNote(Note note) {
        db.execSQL("delete from " + TableConfig.TABLE_NAME + " where " + TableConfig.Note.NOTE_ID + "=?", new String[]{Integer.toString(note.getIndex())});

        EventBus.getDefault().post(new DatabaseModifyEvent("delete note"));
    }

    @Override
    public void removeAllNotes() {
        db.delete(TableConfig.TABLE_NAME, null, null);
    }

    Note getNoteAt(int index) {
        ArrayList<Note> noteList = new ArrayList<Note>();
        Cursor c = db.rawQuery("select * from " + TableConfig.TABLE_NAME + " where " + TableConfig.Note.NOTE_ID + "= ?", new String[]{Integer.toString(index)});
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1), decodeNote(c.getString(2)), c.getInt(0), c.getString(3), c.getString(4), stringToListString(c.getString(5)));
            noteList.add(temp);
        }
        c.close();
        return noteList.get(0);
    }

    @Override
    protected void finalize() throws Throwable {
        EventBus.getDefault().unregister(this);
        super.finalize();
    }

    @Subscribe(sticky = true)
    public void onReceiveNote(NoteModifyEvent event) {
        Note note = event.getNote();
        if (note.getIndex() == -1)
            addNote(note);
        else
            setNote(note);
        System.err.print(note.getTitle());
    }

    @Subscribe(sticky = true)
    public void onDeleteNote(NoteDeleteEvent event) {
        this.removeNote(event.getNote());
    }

    @Subscribe(sticky = true)
    public void receiveClearEvent(ClearEvent event) {
        removeAllNotes();
    }
}