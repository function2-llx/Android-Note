package com.se.npe.androidnote;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mob.MobSDK;
import com.se.npe.androidnote.interfaces.INoteFileConverter;
import com.se.npe.androidnote.models.FileOperate;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.NotePdfConverter;
import com.se.npe.androidnote.models.NoteZipConverter;
import com.se.npe.androidnote.models.TableOperate;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class SplashActivity extends Activity {

    static class ResourceInit extends AsyncTask<Void, String, Void> {

        WeakReference<SplashActivity> ref;

        ResourceInit(SplashActivity ref) {
            this.ref = new WeakReference<>(ref);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            publishProgress("loading database...");
            TableOperate.init(ref.get());
            publishProgress("loading ifly...");
            SpeechUtility.createUtility(ref.get(), SpeechConstant.APPID + "=5bbc8c0f");


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            ref.get().state.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SplashActivity activity = ref.get();
            Intent srcIntent = activity.getIntent();
            Intent dstIntent = new Intent(activity, ListActivity.class);
            dstIntent.putExtras(srcIntent);
            activity.startActivity(dstIntent);
            activity.finish();
        }
    }

    private TextView state;

    private boolean initialized = false;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //manually ask for RW permission
        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }


        state = findViewById(R.id.splash_init_state);
        if (!initialized) {
            MobSDK.init(getApplicationContext());
            new ResourceInit(this).execute();
            initialized = true;
        }

        if (Objects.equals(getIntent().getAction(), Intent.ACTION_VIEW)) {
            final Uri uri = getIntent().getData();
            String path = FileUtils.getPath(this, uri);
            INoteFileConverter noteFileConverter;
            assert path != null;
            switch (FileOperate.getSuffix(path)) {
                case "note":
                    noteFileConverter = new NoteZipConverter();
                    break;
                case "pdf":
                    noteFileConverter = new NotePdfConverter();
                    break;
                default:
                    noteFileConverter = new NoteZipConverter();
                    break;
            }
            noteFileConverter.importNoteFromFile((Note note) -> {
                TableOperate.getInstance().addNote(note);
                Intent intent = new Intent(this, EditorActivity.class);
                intent.putExtra(EditorActivity.VIEW_ONLY, true);
                intent.putExtra(EditorActivity.INITIAL_NOTE, note);
                startActivity(intent);
            }, path);
        }
    }
}
