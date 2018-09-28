package com.se.npe.androidnote.models;

import android.view.View;

import com.se.npe.androidnote.interfaces.IData;

/**
 * Sound data <-> a EditText in the editor
 * it is a piece of continuous text in the editor
 * @author MashPlant
 * */

public class TextData implements IData{
    private String text;

    @Override
    public View renderAsView() {
        return null;
    }
}
