package com.se.npe.androidnote.sound;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

public class IflySoundToTextActivity extends AppCompatActivity {
    // show result
    private TextView textView;
    private SpeechRecognizer speechRecognizer;
    private String mEngineType = null;
    private int result = 0;

    @Override
    protected void onCreat(@nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        initView();
        initDate();
    }

    private void initDate() {

    }

    private void initView() {

    }

    public void start() {

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
