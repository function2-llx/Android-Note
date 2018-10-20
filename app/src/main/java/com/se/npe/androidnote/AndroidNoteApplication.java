package com.se.npe.androidnote;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.se.npe.androidnote.models.TableOperate;

public class AndroidNoteApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5bbc8c0f")).start();
        TableOperate.init(getApplicationContext());
    }
}
