package com.se.npe.androidnote.models;

import android.view.View;

import com.se.npe.androidnote.interfaces.IData;

/**
 * Sound data <-> a piece of video in the editor
 * maybe it will be shown as a MediaPlayer or just a ImageView of a start button
 * @author MashPlant
 * */

public class VideoData implements IData {

    @Override
    public View renderAsView() {
        return null;
    }
}
