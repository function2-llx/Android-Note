package com.se.npe.androidnote.models;

import android.view.View;

import com.se.npe.androidnote.interfaces.IData;

/**
 * Sound data <-> a piece of video in the editor
 * maybe it will be shown as a MediaPlayer or just a ImageView of a start button
 *
 * @author MashPlant
 */

public class VideoData implements IData {
    String videoPath;

    public VideoData(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    @Override
    public boolean equals(Object another) {
        return another.getClass() == this.getClass()
                && this.videoPath.equals(((VideoData) another).videoPath);
    }

    @Override
    public String toString() {
        return "Video" + "asdfg" + videoPath;
    }
}
