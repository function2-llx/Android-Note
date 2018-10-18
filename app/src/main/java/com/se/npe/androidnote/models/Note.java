package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.se.npe.androidnote.interfaces.IData;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Note entity
 * I've thought about making it an interface, such as INote
 * But I now think this may not bring much convenience and make Note an concrete class
 *
 * @author MashPlant
 */

public class Note {
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
    private Date Starttime = new Date(0);
    private Date Modifytime = new Date(0);
    private int DBindex = -1;

    public Date getStarttime() {
        return Starttime;
    }

    public Date getModifytime() {
        return Modifytime;
    }

    public void setStarttime(Date time) {
        Starttime = time;
    }

    public void setModifytime(Date time) {
        Modifytime  = time;
    }

    public Note() {
        this.title = "this is tile for " + DBindex;
        this.content = new ArrayList<IData>();
    }

    public Note(String title, List<IData> content) {
        this.title = title;
        this.content = content;
    }

    public Note(String title, List<IData> content, int index, String timestart,String timemodify) {
        this.DBindex = index;
        this.title = title;
        this.content = content;
        this.Starttime.setTime(Long.parseLong(timestart));
        this.Modifytime.setTime(Long.parseLong(timemodify));
    }

    public String getTitle() {
        return title;
    }

    public void setIndex(int index) {
        DBindex = index;
    }

    public int getIndex() {
        return DBindex;
    }

    public List<IData> getContent() {
        return content;
    }

    public PreviewData getPreview() {
        String text = null;
        String picpath = null;
        List<IData> templist = getContent();
        for (int i = 0; i < templist.size(); i++) {
            if (picpath == null && templist.get(i).toString().charAt(0) == 'P') {
                picpath = templist.get(i).toString().split("asdfg")[1];
            } else if (text == null && templist.get(i).toString().charAt(0) == 'T') {
                text = templist.get(i).toString().split("asdfg")[1];
            }
        }
        if (text == null) text = "无预览文字";
        if (picpath == null) picpath = "";
        Note.PreviewData previewData = new Note.PreviewData(title, text, picpath);
        return previewData;
    }

    public void loadFromFile(String fileName) {
        File file = new File(fileName);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        byte b[] = new byte[(int) file.length()];
        try {
            int len = inputStream.read(b);
            if (len == -1) return;
        } catch (Exception e) {
            System.out.println("Wrong!");
        }

        String tempcontent = new String(b);
        String[] StrArray = tempcontent.split("qwert");

        title = StrArray[0];

        content = new ArrayList<IData>();

        for (int i = 1; i < StrArray.length; i++) {
            if (StrArray[i].charAt(0) == 'S') {
                String[] tempArray = StrArray[i].split("asdfg");
                SoundData tempSoundData = new SoundData(tempArray[1], tempArray[2]);
                content.add(tempSoundData);
            } else if (StrArray[i].charAt(0) == 'T') {
                String[] tempArray = StrArray[i].split("asdfg");
                TextData tempTextData = new TextData(tempArray[1]);
                content.add(tempTextData);
            } else if (StrArray[i].charAt(0) == 'V') {
                String[] tempArray = StrArray[i].split("asdfg");
                VideoData tempVideoData = new VideoData(tempArray[1]);
                content.add(tempVideoData);
            } else if (StrArray[i].charAt(0) == 'P') {
                String[] tempArray = StrArray[i].split("asdfg");
                Bitmap mBitmap = BitmapFactory.decodeFile(StrArray[1]);
                PictureData tempPictureData = new PictureData(tempArray[1], mBitmap);
                content.add(tempPictureData);
            }
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        String string = getTitle() + "qwert";
        List<IData> templist = getContent();

        for (int i = 0; i < templist.size(); i++) {
            string = string + templist.get(i).toString() + "qwert";
        }

        byte[] bs = string.getBytes();
        try {
            outputStream.write(bs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        // Identify note by its DBindex
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return DBindex == note.DBindex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(DBindex);
    }

    @Override
    public String toString() {
        String string = "Title:" + getTitle() + "\nContents:";
        List<IData> tempList = getContent();
        for (int i = 0; i < tempList.size(); i++) {
            string = string + "\n" + tempList.get(i).toString();
        }
        return string;
    }
}
