package com.se.npe.androidnote.models;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataExample {
    static final String EXAMPLE_MIX_IN = "test";

    // Path
    @NonNull
    static String getExamplePath(String mixIn) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mixIn;
    }

    // TextData
    @NonNull
    static TextData getExampleTextData(String mixIn) {
        return new TextData(getExampleText(mixIn));
    }

    @NonNull
    static String getExampleText(String mixIn) {
        return "This is the TextData text for " + mixIn;
    }

    // PictureData
    @NonNull
    static PictureData getExamplePictureData(String mixIn) {
        return new PictureData(getExamplePicturePath(mixIn));
    }

    @NonNull
    static String getExamplePicturePath(String mixIn) {
        return getExamplePath("PictureData/" + mixIn + ".jpg");
    }

    // SoundData
    @NonNull
    static SoundData getExampleSoundData(String mixIn) {
        return new SoundData(getExampleSoundPath(mixIn), getExampleSoundText(mixIn));
    }

    @NonNull
    static String getExampleSoundPath(String mixIn) {
        return getExamplePath("SoundData/" + mixIn + ".mp3");
    }

    @NonNull
    static String getExampleSoundText(String mixIn) {
        return "This is the SoundData text for " + mixIn;
    }

    // SoundData
    @NonNull
    static VideoData getExampleVideoData(String mixIn) {
        return new VideoData(getExampleVideoPath(mixIn));
    }

    @NonNull
    static String getExampleVideoPath(String mixIn) {
        return getExamplePath("VideoData/" + mixIn + ".wav");
    }

    // Note
    @NonNull
    static String getExampleNoteTitle(String mixIn) {
        return "This is the title for " + mixIn;
    }

    @NonNull
    static List<String> getExampleNoteTag(String mixIn) {
        List<String> tags = new ArrayList<>();
        tags.add("tag");
        tags.add(mixIn);
        return tags;
    }

    @NonNull
    static List<IData> getExampleDataList(String mixIn) {
        // data list <-> note content
        List<IData> dataList = new ArrayList<>();
        dataList.add(getExampleTextData(mixIn));
        dataList.add(getExamplePictureData(mixIn));
        dataList.add(getExampleSoundData(mixIn));
        dataList.add(getExampleVideoData(mixIn));
        return dataList;
    }

    @NonNull
    static Note getExampleNote(String mixIn) {
        return new Note(getExampleNoteTitle(mixIn), getExampleDataList(mixIn), getExampleNoteTag(mixIn));
    }

    // File
    static File getExampleFile(String mixIn) {
        String path = getExamplePath(mixIn);
        File file = new File(path);
        try (OutputStream os = new FileOutputStream(file)) {
            file.createNewFile();
            byte[] buffer = new byte[128];
            for (int i = 0; i < buffer.length; ++i) {
                buffer[i] = (byte) i;
            }
            os.write(buffer, 0, buffer.length);
        } catch (IOException e) {
            Logger.log("ExampleFile", e);
        }
        return file;
    }

    static File getExampleDirectory(String mixIn) {
        String path = getExamplePath(mixIn);
        File directory = new File(path);
        directory.mkdirs();
        return directory;
    }

}
