package com.se.npe.androidnote;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.models.FileOperate;
import com.se.npe.androidnote.models.TableConfig;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.models.Note;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SplashActivity extends Activity {
    static class ResourceInit extends AsyncTask<Void, String, Void> {
        WeakReference<SplashActivity> ref;

        ResourceInit(SplashActivity ref) {
            this.ref = new WeakReference<>(ref);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            publishProgress("loading database...");
            TableConfig.SAVE_PATH = ref.get().getExternalFilesDir(null).getAbsolutePath()+ "/AndroidNote";
            TableOperate.init(ref.get());

            Note note = new Note("12345",new ArrayList<IData>());
            note.getTag().add("haha");
            Log.d("debug0001","TaglistCheck"+Integer.toString(note.getTag().size()));
            TableOperate.getInstance().addNote(note);
            Note note2 = new Note("12345",new ArrayList<IData>());
            note2.getTag().add("hehe");
            TableOperate.getInstance().addNote(note2);

            Log.d("debug0001","TagCheck"+Integer.toString(TableOperate.getInstance().getSearchResultFuzzy("12345").size()));
            Log.d("debug0001","TagCheck"+Integer.toString(TableOperate.getInstance().getSearchResultFuzzyWithTag("12345","haha").size()));

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
            SplashActivity ref = this.ref.get();
            Intent srcIntent = ref.getIntent();
            Intent dstIntent = new Intent(ref, ListActivity.class);
            dstIntent.putExtras(srcIntent);
            ref.startActivity(dstIntent);
            ref.finish();
        }
    }

    private TextView state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        state = findViewById(R.id.splash_init_state);
        new ResourceInit(this).execute();
    }
}
