package com.se.npe.androidnote.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.INoteCollection;
import com.se.npe.androidnote.util.Logger;
import com.se.npe.androidnote.util.ReturnValueEater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableOperate implements INoteCollection {
    private static final String LOG_TAG = Note.class.getSimpleName();
    private static final String constantStringWhere = " where ";
    //Magic avoid Code Smell
    private DBManager manager;
    private SQLiteDatabase db;
    private static File configFile;
    private static TableOperate tableOperate;

    public static void init(Context context) {
        tableOperate = new TableOperate(context);
        TableConfig.FileSave.setSavePath(context.getExternalFilesDir(null).getAbsolutePath());
        initConfigFile();
    }

    private static String getCondition(String label, String parameter, boolean op) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(label);
        if (op) {
            stringBuilder.append(" like ");
            stringBuilder.append("'%");
            stringBuilder.append(parameter);
            stringBuilder.append("%'");
        } else {
            stringBuilder.append(" = ");
            stringBuilder.append("'");
            stringBuilder.append(parameter);
            stringBuilder.append("'");
        }
        return stringBuilder.toString();
    }

    private static String getSql(String tableName, String condition1, String condition2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Select * from ");
        stringBuilder.append(tableName);
        if (!condition1.equals("")) {
            stringBuilder.append(constantStringWhere);
            stringBuilder.append(condition1);
        }
        if (!condition2.equals("")) {
            stringBuilder.append(" AND ");
            stringBuilder.append(condition2);
        }
        return stringBuilder.toString();
    }

    private static void initConfigFile() {
        File file = new File(TableConfig.FileSave.getSavePath() + File.separator + "config");
        ReturnValueEater.eat(file.mkdirs());
        configFile = new File(TableConfig.FileSave.getSavePath() + File.separator + "config" + File.separator + "searchconfig.txt");
        try {
            ReturnValueEater.eat(configFile.createNewFile());
            setSearchConfig(TableConfig.Sorter.getDefaultSorterField());
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
    }

    //Note结构编码
    private String contentToString(@NonNull List<IData> src) {
        StringBuilder stringBuilder = new StringBuilder();
        for (IData data : src) {
            stringBuilder.append(data.toString());
            stringBuilder.append(TableConfig.FileSave.LIST_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    //Note结构解码
    private List<IData> stringToContent(@NonNull String src) {
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
    private String tagListToString(@NonNull List<String> src) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : src) {
            stringBuilder.append(string);
            stringBuilder.append(TableConfig.FileSave.LIST_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    //TagList解码
    private List<String> stringToTagList(@NonNull String src) {
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
    public List<Note> getAllNotes(String groupName, List<String> tagList) {
        ArrayList<Note> noteList = new ArrayList<>();
        Cursor c;
        if (groupName.equals("")) {
            c = db.rawQuery(getSql(TableConfig.Note.NOTE_TABLE_NAME, "", ""), null);
        } else {
            c = db.rawQuery(getSql(TableConfig.Note.NOTE_TABLE_NAME, getCondition(TableConfig.Note.NOTE_GROUP, groupName, false), ""), null);
        }
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1), stringToContent(c.getString(2)), c.getInt(0), c.getString(3), c.getString(4), stringToTagList(c.getString(5)), c.getString(6));
            if (tagList == null || tagList.isEmpty()) noteList.add(temp);
            else {
                List<String> tempTag = temp.getTag();
                for (int i = 0; i < tagList.size(); i++) {
                    if (tempTag.contains(tagList.get(i))) {
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
    public List<Note> fuzzySearch(String parameter, String groupName, List<String> tagList) {
        ArrayList<Note> noteList = new ArrayList<>();
        String sql;
        if (groupName.equals("")) {
            sql = getSql(TableConfig.Note.NOTE_TABLE_NAME, getCondition(TableConfig.Note.NOTE_TITLE, parameter, true), "");
        } else {
            sql = getSql(TableConfig.Note.NOTE_TABLE_NAME, getCondition(TableConfig.Note.NOTE_TITLE, parameter, true), getCondition(TableConfig.Note.NOTE_GROUP, groupName, false));
        }
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1), stringToContent(c.getString(2)), c.getInt(0), c.getString(3), c.getString(4), stringToTagList(c.getString(5)), c.getString(6));
            if (tagList == null || tagList.isEmpty()) noteList.add(temp);
            else {
                List<String> tempTag = temp.getTag();
                for (int i = 0; i < tagList.size(); i++) {
                    if (tempTag.contains(tagList.get(i))) {
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
        db.insert(TableConfig.Group.GROUP_TABLE_NAME, null, cValue);
    }

    public void removeGroup(String groupName) {
        db.execSQL("delete from " + TableConfig.Group.GROUP_TABLE_NAME + constantStringWhere + TableConfig.Group.GROUP_NAME + "=?", new String[]{groupName});
        List<Note> noteList = getAllNotes(groupName, null);
        for (int i = 0; i < noteList.size(); i++) {
            removeNote(noteList.get(i));
        }
    }

    @Override
    public List<String> getAllGroups() {
        ArrayList<String> groupnameList = new ArrayList<>();
        Cursor c = db.rawQuery(getSql(TableConfig.Group.GROUP_TABLE_NAME, "", ""), null);
        while (c.moveToNext()) {
            groupnameList.add(c.getString(0));
        }
        c.close();
        return groupnameList;
    }


    public List<String> getAllTags() {
        ArrayList<String> tagNameList = new ArrayList<>();
        Cursor c = db.rawQuery(getSql(TableConfig.Note.NOTE_TABLE_NAME, "", ""), null);
        while (c.moveToNext()) {
            String tag = c.getString(5);
            List<String> tagList = stringToTagList(tag);
            for (int i = 0; i < tagList.size(); i++) {
                if (!tagNameList.contains(tagList.get(i))) {
                    tagNameList.add(tagList.get(i));
                }
            }
        }
        c.close();
        return tagNameList;
    }

    @Override
    public void addNote(Note note) {
        Log.d("debug0001", "insert into " + TableConfig.Note.NOTE_TABLE_NAME + " values(" + note.getTitle() + "," + contentToString(note.getContent()) + ")");
        ContentValues cValue = new ContentValues();
        cValue.put(TableConfig.Note.NOTE_TITLE, note.getTitle());
        cValue.put(TableConfig.Note.NOTE_CONTENT, contentToString(note.getContent()));
        cValue.put(TableConfig.Note.NOTE_START_TIME, Long.toString(note.getStartTime().getTime()));
        cValue.put(TableConfig.Note.NOTE_MODIFY_TIME, Long.toString(note.getModifyTime().getTime()));
        cValue.put(TableConfig.Note.NOTE_TAG, tagListToString(note.getTag()));
        cValue.put(TableConfig.Note.NOTE_GROUP, note.getGroupName());
        db.insert(TableConfig.Note.NOTE_TABLE_NAME, null, cValue);
        String sql = getSql(TableConfig.Note.NOTE_TABLE_NAME, "", "");
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToLast();
        int count = cursor.getInt(0);
        cursor.close();
        note.setIndex(count);
    }

    @Override
    public void setNote(Note note) {
        db.execSQL("update " + TableConfig.Note.NOTE_TABLE_NAME + " set " + TableConfig.Note.NOTE_TITLE + "=?," + TableConfig.Note.NOTE_TAG + "=?," + TableConfig.Note.NOTE_START_TIME + "=?," + TableConfig.Note.NOTE_MODIFY_TIME + "=?," + TableConfig.Note.NOTE_GROUP + "=?," + TableConfig.Note.NOTE_CONTENT + "=?" + constantStringWhere + TableConfig.Note.NOTE_ID + "=?",
                new Object[]{note.getTitle(), tagListToString(note.getTag()), Long.toString(note.getStartTime().getTime()), Long.toString(note.getModifyTime().getTime()), note.getGroupName(), contentToString(note.getContent()), Integer.toString(note.getIndex())});
    }

    public void modifyNote(Note note) {
        if (note.getIndex() == -1)
            addNote(note);
        else
            setNote(note);
    }

    @Override
    public void removeNote(Note note) {
        db.execSQL("delete from " + TableConfig.Note.NOTE_TABLE_NAME + constantStringWhere + TableConfig.Note.NOTE_ID + "=?", new String[]{Integer.toString(note.getIndex())});
    }

    @Override
    public void removeAllNotes() {
        db.delete(TableConfig.Note.NOTE_TABLE_NAME, null, null);
    }

    Note getNoteAt(int index) {
        ArrayList<Note> noteList = new ArrayList<>();
        Cursor c = db.rawQuery(getSql(TableConfig.Note.NOTE_TABLE_NAME, getCondition(TableConfig.Note.NOTE_ID, Integer.toString(index), false), ""), null);
        while (c.moveToNext()) {
            Note temp = new Note(c.getString(1), stringToContent(c.getString(2)), c.getInt(0), c.getString(3), c.getString(4), stringToTagList(c.getString(5)), c.getString(6));
            noteList.add(temp);
        }
        c.close();
        return noteList.get(0);
    }
}