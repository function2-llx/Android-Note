package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;

import com.se.npe.androidnote.interfaces.IData;

import java.util.ArrayList;
import java.util.List;

public class DataExample {
    static final String EXAMPLE_MIX_IN = "test";

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
        return "This is the PictureData picture path for " + mixIn;
    }

    // SoundData
    @NonNull
    static SoundData getExampleSoundData(String mixIn) {
        return new SoundData(getExampleSoundPath(mixIn), getExampleSoundText(mixIn));
    }

    @NonNull
    static String getExampleSoundPath(String mixIn) {
        return "This is the SoundData sound path for " + mixIn;
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
        return "This is the VideoData video path for " + mixIn;
    }

    // Note
    @NonNull
    static String getExampleNoteTitle(String mixIn) {
        return "This is the title for " + mixIn;
    }

    @NonNull
    static List<IData> getExampleDataList(String mixIn) {
        // Create a new example data list
        List<IData> dataList = new ArrayList<>();
        dataList.add(getExampleTextData(mixIn));
        dataList.add(getExamplePictureData(mixIn));
        dataList.add(getExampleSoundData(mixIn));
        dataList.add(getExampleVideoData(mixIn));
        return dataList;
    }

    @NonNull
    static Note getExampleNote(String mixIn) {
        // Create a new example note
        return new Note(getExampleNoteTitle(mixIn), getExampleDataList(mixIn));
    }
}
