package com.se.npe.androidnote.models;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.util.Logger;
import com.se.npe.androidnote.util.ReturnValueEater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataExample {
    public static final String EXAMPLE_MIX_IN = "test";
    public static final String EXAMPLE_TITLE_WHOLE_NOTE_LIST = "title";
    public static final String EXAMPLE_TITLE_EMPTY_NOTE_LIST = "wtf???";

    // no constructor
    private DataExample() {
    }

    // Path
    @NonNull
    public static String getExamplePath(String mixIn) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + mixIn;
    }

    // GroupName
    @NonNull
    public static String getExampleGroupName(String mixIn) {
        return "Group" + mixIn;
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
        return getExamplePath("PictureData" + File.separator + mixIn + ".jpg");
    }

    // SoundData
    @NonNull
    static SoundData getExampleSoundData(String mixIn) {
        return new SoundData(getExampleSoundPath(mixIn), getExampleSoundText(mixIn));
    }

    @NonNull
    static String getExampleSoundPath(String mixIn) {
        return getExamplePath("SoundData" + File.separator + mixIn + ".mp3");
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
        return getExamplePath("VideoData" + File.separator + mixIn + ".wav");
    }

    // Note
    @NonNull
    static String getExampleNoteTitle(String mixIn) {
        return "This is the title for " + mixIn;
    }

    @NonNull
    public static List<String> getExampleNoteTags(String mixIn) {
        List<String> tags = new ArrayList<>();
        tags.add(getExampleNoteTag(mixIn));
        return tags;
    }

    @NonNull
    public static String getExampleNoteTag(String mixIn) {
        return "Tag" + mixIn;
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
    public static Note getExampleNote(String mixIn) {
        return new Note(getExampleNoteTitle(mixIn), getExampleDataList(mixIn), getExampleNoteTags(mixIn), getExampleGroupName(mixIn));
    }

    // File
    static File getExampleFile(String mixIn) {
        File file = new File(getExamplePath(mixIn));
        try (OutputStream os = new FileOutputStream(file)) {
            ReturnValueEater.eat(file.createNewFile());
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
        File directory = new File(getExamplePath(mixIn));
        ReturnValueEater.eat(directory.mkdirs());
        return directory;
    }
}
