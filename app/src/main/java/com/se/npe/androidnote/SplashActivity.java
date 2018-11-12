package com.se.npe.androidnote;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.se.npe.androidnote.models.TableOperate;

import java.lang.ref.WeakReference;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        state = findViewById(R.id.splash_init_state);
        new ResourceInit(this).execute();
    }
}
