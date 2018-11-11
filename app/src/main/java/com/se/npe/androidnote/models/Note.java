package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;

import com.se.npe.androidnote.interfaces.IData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Note entity
 * I've thought about making it an interface, such as INote
 * But I now think this may not bring much convenience and make Note an concrete class
 *
 * @author MashPlant
 */

public class Note {
    private static final String LOG_TAG = Note.class.getSimpleName();

    public static class PreviewData {
        // using public field just for convenience
        public @NonNull
        String title;
        public @NonNull
        String text;

        public @NonNull
        String groupName;

        public @NonNull
        String picturePath;
        public @NonNull
        Date startTime;
        public @NonNull
        Date modifyTime;

        public PreviewData(@NonNull String title, @NonNull String text, @NonNull String groupName, @NonNull String picturePath, @NonNull Date startTime, @NonNull Date modifyTime) {
            this.title = title;
            this.text = text;
            this.groupName = groupName;
            this.picturePath = picturePath;
            this.startTime = startTime;
            this.modifyTime = modifyTime;
        }
    }

    private String title;
    private List<IData> content;
    private Date startTime = new Date(0);
    private Date modifyTime = new Date(0);
    private List<String> tag;
    private int indexDB = -1;
    private String groupName = "";

    public Note() {
        this.title = "this is tile for " + indexDB;
        this.content = new ArrayList<>();
        this.tag = new ArrayList<>();
    }

    public Note(String title, List<IData> content) {
        this.title = title;
        this.content = content;
        this.tag = new ArrayList<>();
    }

    public Note(String title, List<IData> content, List<String> tag) {
        this.title = title;
        this.content = content;
        this.tag = tag;
    }

    public Note(String title, List<IData> content, int index, String timeStart, String timeModify, List<String> tag, String groupName) {
        this.indexDB = index;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.startTime.setTime(Long.parseLong(timeStart));
        this.modifyTime.setTime(Long.parseLong(timeModify));
        this.groupName = groupName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<IData> getContent() {
        return content;
    }

    public void setContent(List<IData> content) {
        this.content = content;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date time) {
        startTime = time;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date time) {
        modifyTime = time;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public int getIndex() {
        return indexDB;
    }

    public void setIndex(int index) {
        indexDB = index;
    }

    public PreviewData getPreview() {
        String text = null;
        String picPath = null;
        List<IData> content = getContent();
        for (int i = 0; i < content.size(); i++) {
            if (picPath == null && content.get(i).toString().charAt(0) == 'P') {
                picPath = content.get(i).toString().split(TableConfig.FileSave.LINE_SEPARATOR)[1];
            } else if (text == null && content.get(i).toString().charAt(0) == 'T') {
                text = content.get(i).toString().split(TableConfig.FileSave.LINE_SEPARATOR)[1];
            }
        }
        if (text == null) text = "无预览文字";
        if (picPath == null) picPath = "";
        return new Note.PreviewData(title, text, groupName, picPath, startTime, modifyTime);
    }

    @Override
    public boolean equals(Object o) {
        // Identify note by its DBindex
        return o instanceof Note && ((Note) o).indexDB == indexDB;
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexDB);
    }

    @Override
    public String toString() {
        String string = "Title:" + getTitle() + "\nContents:";
        List<IData> templist = getContent();
        StringBuilder sb = new StringBuilder();
        sb.append(string);
        for (int i = 0; i < templist.size(); i++) {
            sb.append('\n');
            sb.append(templist.get(i).toString());
        }
        return sb.toString();
    }
}
