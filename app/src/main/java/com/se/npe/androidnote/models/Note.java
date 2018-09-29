package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;

import com.se.npe.androidnote.interfaces.IData;

import java.util.List;

/**
 * Note entity
 * I've thought about making it an interface, such as INote
 * But I now think this may not bring much convenience and make Note an concrete class
 * @author MashPlant
 * */

public class Note {
    static class PreviewData {
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

    public Note(String title, List<IData> content) {
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
        return null;
    }

    public void loadFromFile(String fileName) {

    }

    public void saveToFile(String fileName) {

    }
}
