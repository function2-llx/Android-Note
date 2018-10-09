package com.se.npe.androidnote.sound;

import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.se.npe.androidnote.interfaces.ISoundToText;

public class IflySoundToText implements ISoundToText {
    @Override
    public String toText(String soundPath, Context context) {
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=5bbc8c0f");
        return null;
    }
}
