package com.se.npe.androidnote.models;

import android.view.View;

import com.se.npe.androidnote.interfaces.IData;

/**
 * Sound data <-> a piece of sound and text in the editor
 * maybe it will be shown as a MediaPlayer or just a ImageView of a start button
 * @author MashPlant
 * */

public class SoundData implements IData {
    private String soundPath;
    private String text;

    public SoundData(String soundPath, String text) {
        this.soundPath = soundPath;
        this.text = text;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public String getText() {
        return text;
    }

    public String toString(){
        return "Sound"+"asdfg"+soundPath + "asdfg" + text;
    }
}
