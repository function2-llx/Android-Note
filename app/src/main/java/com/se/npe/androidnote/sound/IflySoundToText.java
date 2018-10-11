package com.se.npe.androidnote.sound;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.se.npe.androidnote.interfaces.ISoundToText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

// Reference : https://blog.csdn.net/changerzhuo_319/article/details/54092206
public class IflySoundToText {
    public interface OnTextReadyListener {
        void onTextReady(String text);

        void onTextFinished(String all);
    }

    //private static final String APPID = "5bbcc9e0";
    //private static final String APPID = "=5bbc8c0f";
    private StringBuffer mResult = new StringBuffer();

    /* ms */
    private int maxWaitTime = 500;
    private int perWaitTime = 100;
    /* error appear times */
    private String fileName = "";
    private SpeechRecognizer mIat = null;

    public void acceptTask(Context context, String soundPath, OnTextReadyListener listener) {
        fileName = soundPath;
        if (mIat == null)
            mIat = SpeechRecognizer.createRecognizer(context, null);
        new FileByteLoader(listener).execute();
    }

    private ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize) {
        ArrayList<byte[]> array = new ArrayList<>();
        if (spsize <= 0 || length <= 0 || buffer == null
                || buffer.length < length)
            return array;
        int size = 0;
        while (size < length) {
            int left = length - size;
            if (spsize < left) {
                byte[] sData = new byte[spsize];
                System.arraycopy(buffer, size, sData, 0, spsize);
                array.add(sData);
                size += spsize;
            } else {
                byte[] sData = new byte[left];
                System.arraycopy(buffer, size, sData, 0, left);
                array.add(sData);
                size += left;
            }
        }
        return array;
    }

    private class MyRecognizerListener implements RecognizerListener {
        OnTextReadyListener listener;

        public MyRecognizerListener(OnTextReadyListener listener) {
            this.listener = listener;
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
        }

        @Override
        public void onBeginOfSpeech() {
            Log.e("audioBegin:", "begin");
        }

        @Override
        public void onEndOfSpeech() {
            Log.e("audioEnd:", "end");
//            if (listener != null) {
//                listener.onTextFinished(mResult.toString());
//            }
        }

        @Override
        public void onResult(RecognizerResult result, boolean b1) {
            Log.e("addResult:", result.getResultString());
            String text = result.getResultString();
            mResult.append(text);
            if (listener != null) {
                listener.onTextReady(text);
            }
        }

        @Override
        public void onError(SpeechError error) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    }

//    private RecognizerListener recListener = new RecognizerListener() {
//
//        @Override
//        public void onVolumeChanged(int i, byte[] bytes) {
//
//        }
//
//        @Override
//        public void onBeginOfSpeech() {
//            Log.e("audioBegin:", "begin");
//        }
//
//        @Override
//        public void onEndOfSpeech() {
//            Log.e("audioEnd:", "end");
//        }
//
//        @Override
//        public void onResult(RecognizerResult result, boolean b1) {
//            Log.e("addResult:", result.getResultString());
//            mResult.append(result.getResultString());
//        }
//
//        @Override
//        public void onError(SpeechError error) {
//
//        }
//
//        @Override
//        public void onEvent(int i, int i1, int i2, Bundle bundle) {
//
//        }
//    };

    class FileByteLoader extends AsyncTask<Void, Void, Void> {
        private FileInputStream fis = null;
        private byte[] voiceBuffer = null;
        private OnTextReadyListener listener;

        public FileByteLoader(OnTextReadyListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("enterAsync:", "Enter Async");
            try {
                fis = new FileInputStream(new File(fileName));
                voiceBuffer = new byte[fis.available()];
                fis.read(voiceBuffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != fis) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (0 == voiceBuffer.length) {
                mResult.append("no audio available!");
            } else {
                Log.e("enterAsync :", "beginSpeech");
                mResult.setLength(0);
                SpeechRecognizer recognizer = SpeechRecognizer.getRecognizer();
                mIat.setParameter(SpeechConstant.DOMAIN, "iat");
                mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
                mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
                mIat.setParameter(SpeechConstant.RESULT_TYPE, "plain");

                ArrayList<byte[]> buffers = splitBuffer(voiceBuffer,
                        voiceBuffer.length, 4800);

                mIat.startListening(new MyRecognizerListener(listener));
//                mIat.writeAudio(voiceBuffer, 0, voiceBuffer.length);
                for (int i = 0; i < buffers.size(); i++) {
                    // 4.8K 10ms
                    mIat.writeAudio(buffers.get(i), 0, buffers.get(i).length);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mIat.stopListening();
//                Log.e("my", mIat.isListening() + " ");
                while (mIat.isListening()) {
                    if (maxWaitTime < 0) {
                        mResult.setLength(0);
                        mResult.append("解析超时！");
                        break;
                    }
                    try {
                        Thread.sleep(perWaitTime);
                        maxWaitTime -= perWaitTime;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listener.onTextFinished(mResult.toString());
        }
    }
}
