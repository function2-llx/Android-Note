package com.se.npe.androidnote;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.se.npe.androidnote.interfaces.INoteFileConverter;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.NotePdfConverter;
import com.se.npe.androidnote.models.NoteZipConverter;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.sound.ResultPool;
import com.se.npe.androidnote.util.Logger;
import com.se.npe.androidnote.util.ReturnValueEater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;

public class EditorActivity extends AppCompatActivity {
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final long MAX_SIZE = 188743680L; // 180 MB
    private static final int MAX_PICK = 15;
    private static final int PICKER_SOUND = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    private static final String OUTPUT_DIR =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidNote/Media/";

    private SortRichEditor editor;
    private Note oldNote;
    private Date createTime;
    private String currentGroup = "";
    private Uri tempMediaUri;
    private boolean isViewOnly;
    public static final String VIEW_ONLY = "VIEW_ONLY";
    public static final String CURRENT_GROUP = "CURRENT_GROUP";
    public static final String INITIAL_NOTE = "INITIAL_NOTE";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isViewOnly) {
            getMenuInflater().inflate(R.menu.activity_editor_viewonly, menu);
        } else {
            getMenuInflater().inflate(R.menu.activity_editor, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void shareWechat(Platform weChat, Platform.ShareParams sp, String fileName) {
        Note note = editor.buildNote();

        sp.setShareType(Platform.SHARE_FILE);
        sp.setTitle(note.getTitle() + ".note");
        sp.setText("test");
        sp.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
        sp.setFilePath(fileName);
        weChat.share(sp);
    }

    private void share() {
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();

        oks.setTitle("test");
        oks.setText("test");
        oks.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");

        Note note = editor.buildNote();
        INoteFileConverter noteFileConverter = new NoteZipConverter();
        noteFileConverter.exportNoteToFile((String fileName) -> {
            oks.setFilePath(fileName);
            oks.setShareContentCustomizeCallback(
                (platform, shareParams) -> {
                    if (platform.getName().equals(Wechat.NAME))
                        shareWechat(platform, shareParams, fileName);
                }
            );
            oks.show(this);
        }, note, "temp");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                editor.destroy();
                save();
                finish();
                break;

            case R.id.menu_save:
                save();
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                break;


            case R.id.menu_markdown:
                // shows the mode (markdown/plain_text) to change to
                if (editor.changeIsMarkdown()) {
                    item.setTitle(R.string.editor_markdown);
                } else {
                    item.setTitle(R.string.editor_plain_text);
                }
                break;

            case R.id.viewonly_share:
            case R.id.share:
                share();
                break;

            case R.id.viewonly_export:
            case R.id.export:
                Note note = editor.buildNote();
                note.setStartTime(createTime);
                note.setModifyTime(new Date());
                new AlertDialog.Builder(this)
                        .setTitle("Format")
                        .setSingleChoiceItems(new String[]{"Note", "PDF"}, -1, (dialog, which) -> {
                            INoteFileConverter noteFileConverter;
                            if (which == 0) {
                                noteFileConverter = new NoteZipConverter();
                            } else {
                                noteFileConverter = new NotePdfConverter();
                            }
                            noteFileConverter.exportNoteToFile((String filePathName) ->
                                            Toast.makeText(getApplicationContext(), "Exported to " + filePathName, Toast.LENGTH_SHORT).show()
                                    , note, note.getTitle());
                            dialog.cancel();
                        })
                        .show();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        if (isViewOnly) {
            return;
        }
        Note note = editor.buildNote();
        if (oldNote != null)
            note.setIndex(oldNote.getIndex());
        note.setStartTime(createTime);
        note.setModifyTime(new Date());
        note.setGroupName(currentGroup);
        TableOperate.getInstance().modifyNote(note);
        oldNote = note;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        editor = findViewById(R.id.rich_editor);

        final FloatingActionsMenu insertMedia = findViewById(R.id.insert_media);
        findViewById(R.id.insert_picture).setOnClickListener(v -> {
            insertMedia.collapse();
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.baseline_photo_camera_black_48dp)
                    .setTitle("From")
                    .setSingleChoiceItems(new String[]{"Camera", "Gallery"}, -1, (dialog, which) -> {
                        if (which == 0) {
                            takeMedia(REQUEST_IMAGE_CAPTURE);
                        } else {
                            pickMedia(PickerConfig.PICKER_IMAGE);
                        }
                        dialog.cancel();
                    })
                    .show();
        });
        findViewById(R.id.insert_video).setOnClickListener(v -> {
            insertMedia.collapse();
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.baseline_videocam_black_48dp)
                    .setTitle("From")
                    .setSingleChoiceItems(new String[]{"Camera", "Gallery"}, -1, (dialog, which) -> {
                        if (which == 0) {
                            takeMedia(REQUEST_VIDEO_CAPTURE);
                        } else {
                            pickMedia(PickerConfig.PICKER_VIDEO);
                        }
                        dialog.cancel();
                    })
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
            isViewOnly = true;
            editor.setViewOnly();
            insertMedia.setVisibility(View.GONE);
            findViewById(R.id.scroll_edit).setVisibility(View.GONE);
        } else {
            editor.setMarkdownController(findViewById(R.id.scroll_edit));
        }
        // set current group
        this.currentGroup = getIntent().getStringExtra(CURRENT_GROUP);
        // set old note
        this.oldNote = (Note) getIntent().getSerializableExtra(INITIAL_NOTE);

        // deferred built, or we will get NPE
        if (oldNote != null) {
            editor.loadNote(oldNote);
            this.createTime = oldNote.getStartTime();
        } else {
            this.createTime = new Date();
        }

        // start recording right now
        try {
            ResultPool.getInstance().startRecording();
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
    }

    private void openCamera(int code) {
        Intent intent = new Intent(
            code == REQUEST_IMAGE_CAPTURE ? MediaStore.ACTION_IMAGE_CAPTURE : MediaStore.ACTION_VIDEO_CAPTURE);
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        File tempFile = new File(Environment.getExternalStorageDirectory(), filename);

        // open picture for android 7.0+
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(code == REQUEST_IMAGE_CAPTURE ? MediaStore.Images.Media.DATA : MediaStore.Video.Media.DATA,
                tempFile.getAbsolutePath());
        tempMediaUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempMediaUri);

        startActivityForResult(intent, code);
    }

    private void takeMedia(int code) {
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
        }
        openCamera(code);
    }

    private void pickMedia(int code) {
        Intent intent = new Intent(this, PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE, code);
        intent.putExtra(PickerConfig.MAX_SELECT_SIZE, MAX_SIZE);
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, MAX_PICK);
        startActivityForResult(intent, code);
    }

    void handleMediaResult(int requestCode) {
        File f = new File(OUTPUT_DIR + tempMediaUri.getPath());
        try (FileOutputStream fos = new FileOutputStream(f); InputStream fis = getContentResolver().openInputStream(tempMediaUri)) {
            if (fis == null) {
                Log.e(LOG_TAG, "getContentResolver().openInputStream(tempMediaUri) returned null");
                return;
            }
            if (!f.getParentFile().exists()) {
                boolean ok = f.getParentFile().mkdirs();
                ReturnValueEater.eat(ok);
            }
            if (!f.exists()) {
                boolean ok = f.createNewFile();
                ReturnValueEater.eat(ok);
            }
            byte[] bytes = new byte[fis.available()];
            int count = fis.read(bytes, 0, bytes.length);
            fos.write(bytes, 0, bytes.length);
            ReturnValueEater.eat(count);
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                editor.addPicture(OUTPUT_DIR + tempMediaUri.getPath());
            } else {
                editor.addVideo(OUTPUT_DIR + tempMediaUri.getPath());
            }
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PickerConfig.PICKER_IMAGE:
                for (Media media : data.<Media>getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT)) {
                    editor.addPicture(media.path);
                }
                break;

            case PickerConfig.PICKER_VIDEO:
                for (Media media : data.<Media>getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT)) {
                    editor.addVideo(media.path);
                }
                break;

            case PICKER_SOUND:
                String path = ResultPool.getInstance().getCurrentPath();
                final EditText editText = editor.addSound(path);
                final long requestStartTime = data.getLongExtra(SoundRecorderActivity.REQUEST_START_TIME, -1);
                // wait for the last speech to finish
                new Handler().postDelayed(() -> editText.setText(ResultPool.getInstance().resultFrom(requestStartTime))
                        , ResultPool.SLEEP_MILL);
                break;

            case REQUEST_VIDEO_CAPTURE:
            case REQUEST_IMAGE_CAPTURE:
                handleMediaResult(requestCode);
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.destroy();

        ResultPool.getInstance().stopRecording();
    }
}
