package com.se.npe.androidnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.se.npe.androidnote.editor.SortRichEditor;
import com.se.npe.androidnote.events.NoteModifyEvent;
import com.se.npe.androidnote.events.NoteSelectEvent;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.sound.RecordingService;
import com.se.npe.androidnote.sound.ResultPool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity {
    private static final long MAX_SIZE = 188743680L; // 180 MB
    private static final int MAX_PICK = 15;
    private static final int PICKER_SOUND = 0;
    private SortRichEditor editor;
    private Note oldNote;
    private long startTime;
    public static final String VIEW_ONLY = "VIEW_ONLY";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.activiry_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        this.setTitle(this.getResources().getString(R.string.editor_title));
        EventBus.getDefault().register(this);
        editor = findViewById(R.id.rich_editor);
        final FloatingActionsMenu insertMedia = findViewById(R.id.insert_media);
        findViewById(R.id.insert_picture).setOnClickListener(v -> {
            insertMedia.collapse();
            getPictureOrVideo(PickerConfig.PICKER_IMAGE);
        });
        findViewById(R.id.insert_video).setOnClickListener(v -> {
            insertMedia.collapse();
            getPictureOrVideo(PickerConfig.PICKER_VIDEO);
        });
        findViewById(R.id.insert_sound).setOnClickListener(v -> {
            insertMedia.collapse();
            startActivityForResult(
                    new Intent(this, SoundRecorderActivity.class)
                            .putExtra(RecordingService.START_TIME, startTime)
                    , PICKER_SOUND);
        });
        findViewById(R.id.rearrange_editor).setOnClickListener(v -> {
            insertMedia.collapse();
            editor.sort();
        });

        // set view only mode before load note
        // so that the component can be set as view only
        if (getIntent().getBooleanExtra(VIEW_ONLY, false)) {
            editor.setViewOnly();
            insertMedia.setVisibility(View.GONE);
        }
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
            ResultPool instance = ResultPool.getInstance();
            Toast.makeText(this, instance.resultFrom(RecordingService.getOffset()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Note note = editor.buildNote();
        if (oldNote != null) {
            note.setindex(oldNote.getIndex());
        }
        EventBus.getDefault().post(new NoteModifyEvent(note));

        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        RecordingService.stopRecording();
    }

    @Subscribe(sticky = true)
    public void getNoteFromOld(Note note) {
        oldNote = note;
    }

    @Subscribe(sticky = true)
    public void getNoteFromSelect(NoteSelectEvent event) {
        this.oldNote = event.getNote();
    }
}
