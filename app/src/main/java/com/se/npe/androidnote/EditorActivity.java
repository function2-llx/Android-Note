package com.se.npe.androidnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.se.npe.androidnote.editor.SortRichEditor;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.sound.RecordingService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity {
    private static final long MAX_SIZE = 188743680L; // 180 MB
    private static final int MAX_PICK = 15;
    private static final int PICKER_SOUND = 0;
    private static final String TAG = "Editor-MashPlant";
    private SortRichEditor editor;
    private Note oldNote;
    private FloatingActionsMenu insertMedia;
    private long startTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        EventBus.getDefault().register(this);
        editor = findViewById(R.id.rich_editor);
        insertMedia = findViewById(R.id.insert_media);
        findViewById(R.id.insert_picture).setOnClickListener((v) -> {
            insertMedia.collapse();
            getPictureOrVideo(PickerConfig.PICKER_IMAGE);
        });
        findViewById(R.id.insert_video).setOnClickListener((v) -> {
            insertMedia.collapse();
            getPictureOrVideo(PickerConfig.PICKER_VIDEO);
        });
        findViewById(R.id.insert_sound).setOnClickListener((v) -> {
            insertMedia.collapse();
            startActivityForResult(
                    new Intent(this, SoundRecorderActivity.class)
                            .putExtra(RecordingService.START_TIME, startTime)
                    , PICKER_SOUND);
        });
        findViewById(R.id.rearrange_editor).setOnClickListener((v) -> {
            insertMedia.collapse();
            editor.sort();
        });
        // deferred built, or we will get NPE
        if (oldNote != null) {
            editor.loadNote(oldNote);
        }

        // start recording right now
        startService(new Intent(this, RecordingService.class)
                .putExtra(RecordingService.START_RECORDING, true));
        startTime = System.currentTimeMillis();
    }

    private void getPictureOrVideo(int code) {
        Intent intent = new Intent(this, PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE, code);
        intent.putExtra(PickerConfig.MAX_SELECT_SIZE, MAX_SIZE);
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, MAX_PICK);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PickerConfig.RESULT_CODE) {
            ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            switch (requestCode) {
                case PickerConfig.PICKER_IMAGE:
                    for (Media media : select) {
                        editor.addPicture(media.path);
                    }
                    break;
                case PickerConfig.PICKER_VIDEO:
                    for (Media media : select) {
                        editor.addVideo(media.path);
                    }
                    break;
                default:
                    break;
            }
        } else if (resultCode == SoundRecorderActivity.RESULT_CODE && requestCode == PICKER_SOUND) {
            String path = data.getStringExtra(RecordingService.SOUND_PATH);
            editor.addSound(path);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(editor.buildNote());
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        RecordingService.stopRecording();
    }

    @Subscribe(sticky = true)
    public void getNoteFromOld(Note note) {
        oldNote = note;
    }
}
