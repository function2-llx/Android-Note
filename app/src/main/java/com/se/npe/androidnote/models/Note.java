package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;

import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.util.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;


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
        String picturePath;

        public PreviewData(@NonNull String title, @NonNull String text, @NonNull String picturePath) {
            this.title = title;
            this.text = text;
            this.picturePath = picturePath;
        }
    }

    private String title;
    private List<IData> content;
    private Date starttime = new Date(0);
    private Date modifytime = new Date(0);
    private List<String> tag;
    private int indexDB = -1;

    public Date getStarttime() {
        return starttime;
    }

    public Date getModifytime() {
        return modifytime;
    }

    public void setStarttime(Date time) {
        starttime = time;
    }

    public void setModifytime(Date time) {
        modifytime  = time;
    }

    public Note() {
        this.title = "this is tile for " + indexDB;
        this.content = new ArrayList<IData>();
        this.tag = new ArrayList<String>();
    }

    public Note(String title, List<IData> content) {
        this.title = title;
        this.content = content;
        this.tag = new ArrayList<String>();
    }

    public Note(String title, List<IData> content, int index, String timestart,String timemodify,List<String> tag) {
        this.indexDB = index;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.starttime.setTime(Long.parseLong(timestart));
        this.modifytime.setTime(Long.parseLong(timemodify));
    }

    public String getTitle() {
        return title;
    }

    public void setIndex(int index) {
        indexDB = index;
    }

    public int getIndex() {
        return indexDB;
    }

    public List<IData> getContent() {
        return content;
    }

    public List<String> getTag()
    {
        return tag;
    }

    public PreviewData getPreview() {
        String text = null;
        String picpath = null;
        List<IData> templist = getContent();
        for (int i = 0; i < templist.size(); i++) {
            if (picpath == null && templist.get(i).toString().charAt(0) == 'P') {
                picpath = templist.get(i).toString().split(TableConfig.Filesave.LINE_SEPERATOR)[1];
            } else if (text == null && templist.get(i).toString().charAt(0) == 'T') {
                text = templist.get(i).toString().split(TableConfig.Filesave.LINE_SEPERATOR)[1];
            }
        }
        if (text == null) text = "无预览文字";
        if (picpath == null) picpath = "";
        return new Note.PreviewData(title, text, picpath);
    }

    public void loadFromFile(String fileName) {
        File file = new File(fileName);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Logger.log(LOG_TAG, e);
            return;
        }

        byte b[] = new byte[(int) file.length()];
        try {
            int len = inputStream.read(b);
            if (len == -1) return;
        } catch (Exception e) {
            Logger.log(LOG_TAG, e);
        }

        String tempcontent = new String(b);
        String[] StrArray = tempcontent.split(TableConfig.Filesave.LIST_SEPERATOR);

        title = StrArray[0];

        content = new ArrayList<IData>();

        for (int i = 1; i < StrArray.length; i++) {
            if (StrArray[i].charAt(0) == 'S') {
                String[] tempArray = StrArray[i].split(TableConfig.Filesave.LINE_SEPERATOR);
                SoundData tempSoundData = new SoundData(tempArray[1], tempArray[2]);
                content.add(tempSoundData);
            } else if (StrArray[i].charAt(0) == 'T') {
                String[] tempArray = StrArray[i].split(TableConfig.Filesave.LINE_SEPERATOR);
                TextData tempTextData = new TextData(tempArray[1]);
                content.add(tempTextData);
            } else if (StrArray[i].charAt(0) == 'V') {
                String[] tempArray = StrArray[i].split(TableConfig.Filesave.LINE_SEPERATOR);
                VideoData tempVideoData = new VideoData(tempArray[1]);
                content.add(tempVideoData);
            } else if (StrArray[i].charAt(0) == 'P') {
                String[] tempArray = StrArray[i].split(TableConfig.Filesave.LINE_SEPERATOR);
                PictureData tempPictureData = new PictureData(tempArray[1]);
                content.add(tempPictureData);
            }
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
    }

    public void saveToFile(String fileName) {


        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Logger.log(LOG_TAG, e);
            }
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Logger.log(LOG_TAG, e);
            return;
        }
        String string = getTitle() + TableConfig.Filesave.LIST_SEPERATOR;
        List<IData> templist = getContent();

        for (int i = 0; i < templist.size(); i++) {
            string = string + templist.get(i).toString() + TableConfig.Filesave.LIST_SEPERATOR;
        }

        byte[] bs = string.getBytes();
        try {
            outputStream.write(bs);
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        // Identify note by its DBindex
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return indexDB == note.indexDB;
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
