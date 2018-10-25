package com.se.npe.androidnote.models;

import com.se.npe.androidnote.interfaces.IData;

import java.util.Objects;

/**
 * Sound data <-> a piece of sound and text in the editor
 * maybe it will be shown as a MediaPlayer or just a ImageView of a start button
 *
 * @author MashPlant
 */

public class SoundData implements IData {
    private String soundPath;
    private String text;

    public SoundData(String soundPath, String text) {
        this.soundPath = soundPath;
        this.text = text;
    }

    @Override
    public String getType() {
        return "Sound";
    }

    @Override
    public String getPath() {
        return soundPath;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoundData soundData = (SoundData) o;
        return Objects.equals(soundPath, soundData.soundPath) &&
                Objects.equals(text, soundData.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soundPath, text);
    }

    @Override
    public String toString() {
        return "Sound" + TableConfig.Filesave.LINE_SEPERATOR + soundPath + TableConfig.Filesave.LINE_SEPERATOR + text;
    }
}
