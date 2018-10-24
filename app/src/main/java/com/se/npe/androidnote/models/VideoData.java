package com.se.npe.androidnote.models;

import android.view.View;

import com.se.npe.androidnote.interfaces.IData;

import java.util.Objects;

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

    public String getPath() { return getVideoPath(); }

    public String getType() { return "Video"; }

    public String getText() { return ""; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoData videoData = (VideoData) o;
        return Objects.equals(videoPath, videoData.videoPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoPath);
    }

    @Override
    public String toString() {
        return "Video" + TableConfig.Filesave.LINE_SEPERATOR + videoPath;
    }
}
