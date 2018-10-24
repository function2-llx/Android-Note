package com.se.npe.androidnote;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.se.npe.androidnote.editor.SortRichEditor;
import com.se.npe.androidnote.events.NoteModifyEvent;
import com.se.npe.androidnote.events.NoteSelectEvent;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.sound.ResultPool;
import com.se.npe.androidnote.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditorActivity extends AppCompatActivity {
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final long MAX_SIZE = 188743680L; // 180 MB
    private static final int MAX_PICK = 15;
    private static final int PICKER_SOUND = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;

    private SortRichEditor editor;
    private Note oldNote;
    private long startTime;
    private Date createTime;
    public static final String VIEW_ONLY = "VIEW_ONLY";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activiry_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("From")
                    .setSingleChoiceItems(new String[]{"Camera", "Gallery"}, -1, (dialog, which) -> {
                        if (which == 0) {
                            takePictureOrVideo(REQUEST_IMAGE_CAPTURE);
                        } else {
                            pickPictureOrVideo(PickerConfig.PICKER_IMAGE);
                        }
                        dialog.cancel();
                    })
                    .setCancelable(false)
                    .show();
        });
        findViewById(R.id.insert_video).setOnClickListener(v -> {
            insertMedia.collapse();
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("From")
                    .setSingleChoiceItems(new String[]{"Camera", "Gallery"}, -1, (dialog, which) -> {
                        if (which == 0) {
                            takePictureOrVideo(REQUEST_VIDEO_CAPTURE);
                        } else {
                            pickPictureOrVideo(PickerConfig.PICKER_VIDEO);
                        }
                        dialog.cancel();
                    })
                    .setCancelable(false)
                    .show();
        });
        findViewById(R.id.insert_sound).setOnClickListener(v -> {
            insertMedia.collapse();
            startActivityForResult(new Intent(this, SoundRecorderActivity.class)
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
            this.createTime = oldNote.getStarttime();
        } else
            this.createTime = new Date();

        // start recording right now
        try {
            ResultPool.getInstance().startRecording();
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
    }

    private File createTempPictureOrVideo() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(timeStamp, "", storageDir);
    }

    private void takePictureOrVideo(int code) {
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
        }
        Intent intent = new Intent(
                code == REQUEST_IMAGE_CAPTURE ? MediaStore.ACTION_IMAGE_CAPTURE : MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, code);
    }

    private void pickPictureOrVideo(int code) {
        Intent intent = new Intent(this, PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE, code);
        intent.putExtra(PickerConfig.MAX_SELECT_SIZE, MAX_SIZE);
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, MAX_PICK);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case PickerConfig.PICKER_IMAGE: {
                ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
                for (Media media : select) {
                    editor.addPicture(media.path);
                }
            }
            break;
            case PickerConfig.PICKER_VIDEO: {
                ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
                for (Media media : select) {
                    editor.addVideo(media.path);
                }
            }
            break;
            case PICKER_SOUND: {
                String path = ResultPool.getInstance().getCurrentPath();
                final EditText editText = editor.addSound(path);
                final long requestStartTime = data.getLongExtra(SoundRecorderActivity.REQUEST_START_TIME, -1);
                new Handler().postDelayed(() -> editText.setText(ResultPool.getInstance().resultFrom(requestStartTime))
                        , 1000);
            }
            break;
            case REQUEST_IMAGE_CAPTURE: {
                Bitmap bitmap = data.getParcelableExtra("data");
                try {
                    File f = createTempPictureOrVideo();
                    FileOutputStream fos = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    editor.addPicture(f.getPath());
                } catch (IOException e) {
                    Logger.log(LOG_TAG, e);
                }
            }
            break;
            case REQUEST_VIDEO_CAPTURE: {
                editor.addVideo(data.getData().getPath());
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        editor.destroy();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Note note = editor.buildNote();
        editor.destroy();
        if (oldNote != null) {
            note.setIndex(oldNote.getIndex());
        }
        note.setStarttime(this.createTime);
        note.setModifytime(new Date());
        EventBus.getDefault().post(new NoteModifyEvent(note));

        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        ResultPool.getInstance().stopRecording();
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
