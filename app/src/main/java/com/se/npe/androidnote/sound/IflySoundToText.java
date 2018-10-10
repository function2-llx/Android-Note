package com.se.npe.androidnote.sound;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUtility;
import com.se.npe.androidnote.interfaces.ISoundToText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.os.SystemClock.sleep;

// Reference : https://blog.csdn.net/changerzhuo_319/article/details/54092206
public class IflySoundToText implements ISoundToText {
    private static final String APPID = "=5bbcc9e0";
    private StringBuffer mResult = new StringBuffer();

    /* ms */
    private int maxWaitTime = 500;
    private int perWaitTime = 100;
    /* error appear times */
    private int maxQueueTimes = 3;
    private String fileName = "";

    static {
        //Setting.setShowLog( false );
        SpeechUtility.createUtility("appid=" + APPID);
    }

    public String toText(String soundPath) throws InterruptedException {
        return toText(soundPath, true);
    }

    public String toText(String soundPath, boolean init) throws InterruptedException {
        if (init) {
            maxWaitTime = 500;
            maxQueueTimes = 3;
        }
        if (maxQueueTimes <= 0) {
            mResult.setLength(0);
            mResult.append("解析异常！");
            return mResult.toString();
        }
        fileName = soundPath;
        return recognize();
    }

    private String recognize() throws InterruptedException {
        if (SpeechRecognizer.getRecognizer() == null)
            SpeechRecognizer.createRecognizer();
        return RecognizePcmfileByte();
    }

    /* 模拟音速防止音频队列堵塞 */
    private String RecognizePcmfileByte() throws InterruptedException {
        FileInputStream fis = null;
        byte[] voiceBuffer = null;
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
            mResult.setLength(0);
            SpeechRecognizer recognizer = SpeechRecognizer.getRecognizer();
            recognizer.setParameter(SpeechConstant.DOMAIN, "iat");
            recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            recognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
            recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
            recognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
            recognizer.startListening(recListener);
            ArrayList<byte[]> buffers = splitBuffer(voiceBuffer,
                    voiceBuffer.length, 4800);
            for (int i = 0; i < buffers.size(); i++) {
                // 4.8K 150ms
                recognizer.writeAudio(buffers.get(i), 0, buffers.get(i).length);
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            recognizer.stopListening();

            while (recognizer.isListening()) {
                if (maxWaitTime < 0) {
                    mResult.setLength(0);
                    mResult.append("解析超时！");
                    break;
                }
                Thread.sleep(perWaitTime);
                maxWaitTime -= perWaitTime;
            }
        }
        return mResult.toString();
    }

    private ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize) {
        ArrayList<byte[]> array = new ArrayList<byte[]>();
        if (spsize <= 0 || length <= 0 || buffer == null
                || buffer.length < length)
            return array;
        int size = 0;
        while (size < length) {
            int left = length - size;
            if (spsize < left) {
                byte[] sdata = new byte[spsize];
                System.arraycopy(buffer, size, sdata, 0, spsize);
                array.add(sdata);
                size += spsize;
            } else {
                byte[] sdata = new byte[left];
                System.arraycopy(buffer, size, sdata, 0, left);
                array.add(sdata);
                size += left;
            }
        }
        return array;
    }

    private RecognizerListener recListener = new RecognizerListener() {

        public void onBeginOfSpeech() {
            Log.d("audioBegin:", "begin");
        }

        public void onEndOfSpeech() {
            Log.d("audioEnd:", "end");
        }

        public void onVolumeChanged(int volume) {
        }

        public void onResult(RecognizerResult result, boolean islast) {
            Log.d("audioResult:", result.getResultString());
            mResult.append(result.getResultString());
        }

        public void onError(SpeechError error) {
            try {
                toText(fileName);
                maxQueueTimes--;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        public void onEvent(int eventType, int arg1, int agr2, String msg) {
        }
    };
}
