package com.se.npe.androidnote;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mob.MobSDK;
import com.se.npe.androidnote.editor.SortRichEditor;
import com.se.npe.androidnote.events.NoteSelectEvent;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TableConfig;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.sound.ResultPool;
import com.se.npe.androidnote.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

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
    public static final String VIEW_ONLY = "VIEW_ONLY";
    public static final String CURRENT_GROUP = "CURRENT_GROUP";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activiry_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private PlatformActionListener platformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Logger.logInfo("kid", "分享成功");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Logger.logError("kid", "分享失败");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Logger.logInfo("kid", "分享取消");
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                editor.destroy();
                save();
                finish();
                break;
            }

            case R.id.menu_save: {
                save();
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.menu_markdown:
                if (editor.changeIsMarkdown()) {
                    item.setTitle("Plain");
                } else {
                    item.setTitle("Markdown");
                }
                break;
            case R.id.share: {
                OnekeyShare oks = new OnekeyShare();
                oks.disableSSOWhenAuthorize();
                oks.setTitle(getString(R.string.share));
                oks.setTitleUrl("http://sharesdk.cn");
                oks.setText("我是分享文本");
//                oks.setImagePath(getRe);//确保SDcard下面存在此张图片
                oks.setUrl("http://sharesdk.cn");
                oks.setComment("我是测试评论文本");
                oks.setSite(getString(R.string.app_name));
                oks.setSiteUrl("http://sharesdk.cn");
                oks.show(this);
//                Platform wechatPlatform = ShareSDK.getPlatform(Wechat.NAME);
//                Wechat.ShareParams sp = new Wechat.ShareParams();
//                sp.setShareType(Wechat.SHARE_TEXT);
//                sp.setTitle("test");
//                wechatPlatform.setPlatformActionListener(this.platformActionListener);
//                wechatPlatform.share(sp);

                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        Note note = editor.buildNote();
        if (oldNote != null)
            note.setIndex(oldNote.getIndex());
        note.setStartTime(createTime);
        note.setModifyTime(new Date());
        note.setGroupName(currentGroup);
        TableOperate.getInstance().modify(note);
        oldNote = note;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        EventBus.getDefault().register(this);
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
            editor.setViewOnly();
            insertMedia.setVisibility(View.GONE);
            findViewById(R.id.sound_player_text).setVisibility(View.GONE);
        } else {
            editor.setMarkdownController(findViewById(R.id.scroll_edit));
        }

        this.currentGroup = getIntent().getStringExtra(CURRENT_GROUP);

        // deferred built, or we will get NPE
        if (oldNote != null) {
            editor.loadNote(oldNote);
            this.createTime = oldNote.getStartTime();
        } else
            this.createTime = new Date();

        // start recording right now
        try {
            ResultPool.getInstance().startRecording();
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }

        MobSDK.init(this);
    }

    private void openCamera(int code) {
        Intent intent = new Intent(
                code == REQUEST_IMAGE_CAPTURE ? MediaStore.ACTION_IMAGE_CAPTURE : MediaStore.ACTION_VIDEO_CAPTURE);
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        File tempFile = new File(Environment.getExternalStorageDirectory(), filename);
        if (android.os.Build.VERSION.SDK_INT < 24) {
            tempMediaUri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempMediaUri);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(code == REQUEST_IMAGE_CAPTURE ? MediaStore.Images.Media.DATA : MediaStore.Video.Media.DATA,
                    tempFile.getAbsolutePath());
            tempMediaUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempMediaUri);
        }
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

    class PictureAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            OutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "rxMarkdown");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                outputStream = new FileOutputStream(dir.getAbsolutePath() + File.separator + "b.jpg");
                AssetManager assetManager = getAssets();
                inputStream = assetManager.open("b.jpg");
                byte[] buffer = new byte[1024];
                int read = 0;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            case REQUEST_VIDEO_CAPTURE:
            case REQUEST_IMAGE_CAPTURE: {
                try {
                    File f = new File(OUTPUT_DIR + tempMediaUri.getPath());
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdirs();
                    }
                    if (!f.exists()) {
                        f.createNewFile();
                    }
                    InputStream fis = getContentResolver().openInputStream(tempMediaUri);
                    FileOutputStream fos = new FileOutputStream(f);
                    byte[] bytes = new byte[fis.available()];
                    fis.read(bytes, 0, bytes.length);
                    fos.write(bytes, 0, bytes.length);
                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        editor.addPicture(OUTPUT_DIR + tempMediaUri.getPath());
                    } else {
                        editor.addVideo(OUTPUT_DIR + tempMediaUri.getPath());
                    }
                    fos.close();
                } catch (IOException e) {
                    Logger.log(LOG_TAG, e);
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        save();
        editor.destroy();
        super.onBackPressed();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        Note note = editor.buildNote();
//        if (oldNote != null) {
//            note.setIndex(oldNote.getIndex());
//        }
//        note.setStartTime(this.createTime);
//        note.setModifyTime(new Date());
//        EventBus.getDefault().post(new NoteModifyEvent(note));
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Note note = editor.buildNote();
        editor.destroy();
//        if (oldNote != null) {
//            note.setIndex(oldNote.getIndex());
//        }
//        note.setStartTime(this.createTime);
//        note.setModifyTime(new Date());
//        EventBus.getDefault().post(new NoteModifyEvent(note));

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
