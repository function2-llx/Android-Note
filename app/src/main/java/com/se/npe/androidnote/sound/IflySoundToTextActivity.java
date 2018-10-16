package com.se.npe.androidnote.sound;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

public class IflySoundToTextActivity extends AppCompatActivity {
    // show result
    private TextView textView;
    private SpeechRecognizer speechRecognizer;
    private String mEngineType = null;
    private SharedPreferences sharedPreferences;
    private int result = 0;

    @Override
    protected void onCreat(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        initView();
        initDate();
    }

    private void initDate() {
        speechRecognizer = SpeechRecognizer.createRecognizer(this, initListener);
        sharedPreferences = getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE);
        mEngineType = SpeechConstant.TYPE_CLOUD;
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.tv);
    }

    public void start(View view) {
        textView.setText("");
        result = speechRecognizer.startListening(recognizerListener);
        if (result != ErrorCode.SUCCESS) {
            Log.e("resultTag", "error" + result);
        }
    }

    public void stop() {

    }

    private InitListener initListener = new InitListener() {
        @Override
        public void onInit(int i) {

        }
    }

    private RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {

        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    }

    private void printResult() {

    }
}
