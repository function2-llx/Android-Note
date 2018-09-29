package com.se.npe.androidnote.temperary;

import android.util.Log;

import com.se.npe.androidnote.interfaces.IEditor;
import com.se.npe.androidnote.models.Note;

import java.util.List;

/**
 * An ad-hoc class that implements IEditor
 * You can use it for test
 *
 * @author MashPlant
 */

public class TestEditor implements IEditor {
    private static final String TAG = "TestEditor-MashPlant";

    @Override
    public void addPicture(String picturePath) {
        Log.d(TAG, "I am adding picture " + picturePath);
    }

    @Override
    public void addPictures(List<String> picturePaths) {
        Log.d(TAG, "I am adding " + picturePaths.size() + " picture(s)");
        for (int i = 0; i < picturePaths.size(); ++i) {
            Log.d(TAG, "picture" + i + " " + picturePaths.get(i));
        }
    }

    @Override
    public void addVideo(String videoPath) {
        Log.d(TAG, "I am adding video " + videoPath);
    }

    @Override
    public void addVideos(List<String> videoPaths) {
        Log.d(TAG, "I am adding " + videoPaths.size() + " video(s)");
        for (int i = 0; i < videoPaths.size(); ++i) {
            Log.d(TAG, "video" + i + " " + videoPaths.get(i));
        }
    }

    @Override
    public void addSound(String soundPath) {
        Log.d(TAG, "I am adding sound " + soundPath);
    }

    @Override
    public void loadNote(Note note) {
        Log.d(TAG, "I am loading note, title = " + note.getTitle());
    }

    @Override
    public Note buildNote() {
        return new Note("hello from TestEditor", null);
    }
}
