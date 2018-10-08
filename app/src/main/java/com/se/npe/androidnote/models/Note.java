package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.se.npe.androidnote.interfaces.IData;

import java.util.List;
import java.util.ArrayList;

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
 * @author MashPlant
 * */

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

    static int tot;
    final int id;

    public Note()
    {
        this.id = tot++;
        this.title = "this is tile for " + id;
        this.content = new ArrayList<IData>();
    }

    public Note(String title, List<IData> content) {
        this.id = tot++;
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public List<IData> getContent() {
        return content;
    }

    public PreviewData getPreview() {
        String text = null;
        String picpath = null;
        List<IData> templist = getContent();
        for (int i = 0; i < templist.size(); i++) {
            if(picpath == null&&templist.get(i).toString().charAt(0) == 'P')
            {
                picpath = templist.get(i).toString().split(" ")[1];
            }
            else if(text == null&&templist.get(i).toString().charAt(0) == 'T')
            {
                text = templist.get(i).toString().split(" ")[1];
            }
        }
        if(text == null)text = "无预览文字";
        if(picpath == null)picpath = "";
        Note.PreviewData previewData = new Note.PreviewData(title,text,picpath);
        return previewData;
    }

    public void loadFromFile(String fileName) {
        File file = new File(fileName);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte b[] = new byte[(int)file.length()];
        try{
            inputStream.read(b);
        }
        catch(Exception e){
            System.out.println("Wrong!");
        }

        String tempcontent = new String(b);
        String[] StrArray = tempcontent.split("\n");

        title = StrArray[0];

        content = new ArrayList<IData>();

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
        }
        String string = getTitle() + "\n";
        List<IData> templist = getContent();

        for (int i = 0; i < templist.size(); i++) {
            string = string + templist.get(i).toString() + "\n";
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
}
