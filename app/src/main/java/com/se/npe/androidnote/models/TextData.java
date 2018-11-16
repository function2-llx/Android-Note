package com.se.npe.androidnote.models;

import com.se.npe.androidnote.interfaces.IData;

import java.util.Objects;

/**
 * Sound data <-> a EditText in the editor it is a piece of continuous text in
 * the editor
 *
 * @author MashPlant
 */

public class TextData implements IData {
    private String text;

    public TextData(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public String getType() {
        return "Text";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextData textData = (TextData) o;
        return Objects.equals(text, textData.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "Text" + TableConfig.FileSave.LINE_SEPARATOR + text;
    }
}
