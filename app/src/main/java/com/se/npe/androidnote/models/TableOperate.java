package com.se.npe.androidnote.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.se.npe.androidnote.events.NoteClearEvent;
import com.se.npe.androidnote.events.NoteDeleteEvent;
import com.se.npe.androidnote.events.NoteModifyEvent;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.INoteCollection;
import com.se.npe.androidnote.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TableOperate implements INoteCollection {
    private static final String LOG_TAG = Note.class.getSimpleName();

    private DBManager manager;
    private SQLiteDatabase db;
    private static File configFile;
    private static TableOperate tableOperate;

    public static void init(Context context) {

        tableOperate = new TableOperate(context);
        TableConfig.SAVE_PATH = Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath();
        initConfigFile();
    }

    private static void initConfigFile() {
        File file = new File(TableConfig.SAVE_PATH + "/config");
        if (!file.exists()) {
            file.mkdirs();
        }
        configFile = new File(TableConfig.SAVE_PATH + "/config/searchconfig.txt");
        try {
            if (!configFile.exists()) {
                if (!configFile.createNewFile())
                    throw new IOException("Creating config file fails");
                setSearchConfig(TableConfig.Sorter.SORTER_FIELDS[0]);
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

    //Note结构编码
    private String contentToString(List<IData> src) {
        StringBuilder stringBuilder = new StringBuilder();
        for (IData data : src) {
            stringBuilder.append(data.toString());
            stringBuilder.append(TableConfig.FileSave.LIST_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    //Note结构解码
    private List<IData> stringToContent(String src) {
        List<IData> content = new ArrayList<>();
        String[] strArray = src.split(TableConfig.FileSave.LIST_SEPARATOR);
        for (String aStrArray : strArray) {
            if (strArray[0].length() == 0) continue;
            if (aStrArray.charAt(0) == 'S') {
                String[] tempArray = aStrArray.split(TableConfig.FileSave.LINE_SEPARATOR);
                SoundData tempSoundData = new SoundData(tempArray[1], tempArray[2]);
                content.add(tempSoundData);
            } else if (aStrArray.charAt(0) == 'T') {
                String[] tempArray = aStrArray.split(TableConfig.FileSave.LINE_SEPARATOR);
                TextData tempTextData = new TextData(tempArray[1]);
                content.add(tempTextData);
            } else if (aStrArray.charAt(0) == 'V') {
                String[] tempArray = aStrArray.split(TableConfig.FileSave.LINE_SEPARATOR);
                VideoData tempVideoData = new VideoData(tempArray[1]);
                content.add(tempVideoData);
            } else if (aStrArray.charAt(0) == 'P') {
                String[] tempArray = aStrArray.split(TableConfig.FileSave.LINE_SEPARATOR);
                PictureData tempPictureData = new PictureData(tempArray[1]);
                content.add(tempPictureData);
            }
        }
        return content;
    }

    //TagList编码
    private String tagListToString(List<String> src) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : src) {
            stringBuilder.append(string);
            stringBuilder.append(TableConfig.FileSave.LIST_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    //TagList解码
    private List<String> stringToTagList(String src) {
        if (src.length() == 0) return new ArrayList<>();
        String[] strings = src.split(TableConfig.FileSave.LIST_SEPARATOR);
        return Arrays.asList(strings);
    }

    @Nullable
    public static String getSearchConfig() {
        try (InputStream inputStream = new FileInputStream(configFile)) {
            byte[] b = new byte[(int) configFile.length()];
            int len = inputStream.read(b);
            if (len == -1) return null;
            return new String(b);
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
            return null;
        }
    }

    public static void setSearchConfig(@NonNull String searchConfig) {
        try (OutputStream outputStream = new FileOutputStream(configFile)) {
            byte[] bs = searchConfig.getBytes();
            outputStream.write(bs);
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
    }

    @Override
    public List<Note> getAllNotes(String groupName,List<String> tagList) {
        ArrayList<Note> noteList = new ArrayList<>();
        Cursor c;
        if(groupName == "") {
            c = db.rawQuery("select * from " + TableConfig.TABLE_NAME ,null);
        }
        else {
            c = db.rawQuery("select * from " + TableConfig.TABLE_NAME + " where " + TableConfig.Note.NOTE_GROUP + "= ?", new String[]{groupName});
        }
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1), stringToContent(c.getString(2)), c.getInt(0), c.getString(3), c.getString(4), stringToTagList(c.getString(5)), c.getString(6));
            if(tagList == null)noteList.add(temp);
            else {
                List<String> tempTag = temp.getTag();
                for(int i = 0;i < tagList.size();i ++) {
                    if(tempTag.contains(tagList.get(i))) {
                        noteList.add(temp);
                        break;
                    }
                }
            }
        }
        c.close();
        return noteList;
    }

    @Override
    public List<Note> fuzzySearch(String parameter,String groupName,List<String> tagList) {
        ArrayList<Note> noteList = new ArrayList<>();
        String sql;
        if(groupName == "") {
            sql = "select * from " + TableConfig.TABLE_NAME
                    + " where " + TableConfig.Note.NOTE_TITLE + " like '%" + parameter + "%'";
        }
        else {
            sql = "select * from " + TableConfig.TABLE_NAME
                    + " where " + TableConfig.Note.NOTE_TITLE + " like '%" + parameter + "%' AND " + TableConfig.Note.NOTE_GROUP + " = " + "'" + groupName + "'";
        }
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1), stringToContent(c.getString(2)), c.getInt(0), c.getString(3), c.getString(4), stringToTagList(c.getString(5)), c.getString(6));
            if(tagList == null)noteList.add(temp);
            else {
                List<String> tempTag = temp.getTag();
                for(int i = 0;i < tagList.size();i ++) {
                    if(tempTag.contains(tagList.get(i))) {
                        noteList.add(temp);
                        break;
                    }
                }
            }
        }
        c.close();
        return noteList;
    }

    public void addGroup(String groupName) {
        ContentValues cValue = new ContentValues();
        cValue.put(TableConfig.Group.GROUP_NAME, groupName);
        db.insert(TableConfig.GROUP_TABLE, null, cValue);
    }

    public void removeGroup(String groupName) {
        db.execSQL("delete from " + TableConfig.GROUP_TABLE + " where " + TableConfig.Group.GROUP_NAME + "=?", new String[]{groupName});
        List<Note> noteList = getAllNotes(groupName,null);
        for (int i = 0; i < noteList.size(); i++) {
            removeNote(noteList.get(i));
        }
    }

    @Override
    public List<String> getAllGroup() {
        ArrayList<String> groupnameList = new ArrayList<>();
        Cursor c = db.rawQuery("select * from " + TableConfig.GROUP_TABLE, null);
        while (c.moveToNext()) {
            groupnameList.add(c.getString(0));
        }
        c.close();
        return groupnameList;
    }

    public List<String> getAllTags() {
        ArrayList<String> tagNameList = new ArrayList<>();
        Cursor c = db.rawQuery("select * from " + TableConfig.TABLE_NAME, null);
        while (c.moveToNext()) {
            String tag = c.getString(5);
            List<String> taglist = stringToListString(tag);
            for (int i = 0; i < taglist.size(); i++) {
                if(!tagNameList.contains(taglist.get(i))) {
                    tagNameList.add(taglist.get(i));
                }
            }
        }
        c.close();
        return tagNameList;
    }

    @Override
    public void addNote(Note note) {
        Log.d("debug0001", "insert into " + TableConfig.TABLE_NAME + " values(" + note.getTitle() + "," + contentToString(note.getContent()) + ")");
        ContentValues cValue = new ContentValues();
        cValue.put(TableConfig.Note.NOTE_TITLE, note.getTitle());
        cValue.put(TableConfig.Note.NOTE_CONTENT, contentToString(note.getContent()));
        cValue.put(TableConfig.Note.NOTE_START_TIME, Long.toString(note.getStartTime().getTime()));
        cValue.put(TableConfig.Note.NOTE_MODIFY_TIME, Long.toString(note.getModifyTime().getTime()));
        cValue.put(TableConfig.Note.NOTE_TAG, tagListToString(note.getTag()));
        cValue.put(TableConfig.Note.NOTE_GROUP, note.getGroupName());
        db.insert(TableConfig.TABLE_NAME, null, cValue);
        String sql = "select * from " + TableConfig.TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToLast();
        int count = cursor.getInt(0);
        cursor.close();
        Log.d("debug0001", Integer.toString(count));
        note.setIndex(count);
    }

    @Override
    public void setNote(Note note) {
        db.execSQL("update " + TableConfig.TABLE_NAME + " set " + TableConfig.Note.NOTE_TITLE + "=?," + TableConfig.Note.NOTE_TAG + "=?," + TableConfig.Note.NOTE_START_TIME + "=?," + TableConfig.Note.NOTE_MODIFY_TIME + "=?," + TableConfig.Note.NOTE_GROUP + "=?," + TableConfig.Note.NOTE_CONTENT + "=? where " + TableConfig.Note.NOTE_ID + "=?",
                new Object[]{note.getTitle(), tagListToString(note.getTag()), Long.toString(note.getStartTime().getTime()), Long.toString(note.getModifyTime().getTime()), note.getGroupName(), contentToString(note.getContent()), Integer.toString(note.getIndex())});
    }

    @Override
    public void removeNote(Note note) {
        db.execSQL("delete from " + TableConfig.TABLE_NAME + " where " + TableConfig.Note.NOTE_ID + "=?", new String[]{Integer.toString(note.getIndex())});
    }

    @Override
    public void removeAllNotes() {
        db.delete(TableConfig.TABLE_NAME, null, null);
    }

    Note getNoteAt(int index) {
        ArrayList<Note> noteList = new ArrayList<>();
        Cursor c = db.rawQuery("select * from " + TableConfig.TABLE_NAME + " where " + TableConfig.Note.NOTE_ID + "= ?", new String[]{Integer.toString(index)});
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1), stringToContent(c.getString(2)), c.getInt(0), c.getString(3), c.getString(4), stringToTagList(c.getString(5)), c.getString(6));
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

    public void modify(Note note) {
        if (note.getIndex() == -1)
            addNote(note);
        else
            setNote(note);

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
        removeNote(event.getNote());
    }

    @Subscribe(sticky = true)
    public void onClearNote(NoteClearEvent event) {
        removeAllNotes();
    }
}