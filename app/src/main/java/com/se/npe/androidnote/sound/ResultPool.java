package com.se.npe.androidnote.sound;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.se.npe.androidnote.util.Logger;
import com.se.npe.androidnote.util.ReturnValueEater;
import com.se.npe.androidnote.util.ThreadSleep;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Store the result of asr, sorted by time.
 * Provide interfaces to retrieve the results within a range of time.
 *
 * @author MashPlant
 */

public class ResultPool {
    public static final int SLEEP_MILL = 2500; // 2500ms
    private static final String LOG_TAG = ResultPool.class.getSimpleName();
    private static final String OUTPUT_DIR =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidNote/";
    // always save the recording file at 0.pcm
    // when a request comes, crop a part of 0.pcm to generate a x.wav
    private static final String TEMP_OUTPUT_PATH = OUTPUT_DIR + 0 + ".pcm";
    private static ResultPool instance = new ResultPool();
    private ArrayList<Long> times = new ArrayList<>();
    private ArrayList<String> results = new ArrayList<>();
    private int currentFile = 1;
    private AudioUtil.AudioRecordThread recorder;
    private long startTime;

    static class MyRecognizerListener implements RecognizerListener {
        ResultPool target;
        long now;

        MyRecognizerListener(ResultPool target, long now) {
            this.target = target;
            this.now = now;
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            // no-op
        }

        @Override
        public void onBeginOfSpeech() {
            // no-op
        }

        @Override
        public void onEndOfSpeech() {
            // no-op
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            // skip '。' & space
            String result = recognizerResult.getResultString();
            if ("".equals(result) || "。".equals(result)) {
                return;
            }
            target.putResult(now, result);
        }

        @Override
        public void onError(SpeechError speechError) {
            // no-op
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            // no-op
        }
    }

    static class IFlyFeeder extends AsyncTask<Void, Void, Void> {
        private WeakReference<ResultPool> ref;
        private int currentPcmByte;
        private FileInputStream fis;
        private SpeechRecognizer iat;

        IFlyFeeder(ResultPool ref) {
            this.ref = new WeakReference<>(ref);
            try {
                fis = new FileInputStream(new File(TEMP_OUTPUT_PATH));
            } catch (IOException e) {
                Logger.log(LOG_TAG, e);
            }
            try {
                iat = SpeechRecognizer.createRecognizer(null, null);
                iat.setParameter(SpeechConstant.DOMAIN, "iat");
                iat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                iat.setParameter(SpeechConstant.ACCENT, "mandarin");
                iat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
                iat.setParameter(SpeechConstant.RESULT_TYPE, "plain");
            } catch (VerifyError e) {
                // no-op
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            try {
                fis.close();
            } catch (IOException e) {
                Logger.log(LOG_TAG, e);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ResultPool target = ref.get();
            while (true) {
                // must check cancelled or not to terminate doInBackground()
                // (calling cancel() from outside WON'T terminate doInBackground())
                if (isCancelled()) {
                    return null;
                }
                long now = System.currentTimeMillis();
                byte[] voiceBuffer = null;
                try {
                    voiceBuffer = new byte[fis.available() - currentPcmByte];
                    boolean ok = fis.skip(currentPcmByte) == currentPcmByte;
                    ok = ok && fis.read(voiceBuffer) == voiceBuffer.length;
                    currentPcmByte = fis.available();
                    if (!ok) {
                        Log.e(LOG_TAG, "skip or read not complete, but continued anyway");
                    }
                } catch (IOException e) {
                    Logger.log(LOG_TAG, e);
                }
                if (voiceBuffer != null && voiceBuffer.length != 0) {
                    iat.startListening(new MyRecognizerListener(target, now));
                    iat.writeAudio(voiceBuffer, 0, voiceBuffer.length);
                    iat.stopListening();
                }
                ThreadSleep.sleep(SLEEP_MILL);
            }
        }
    }

    private IFlyFeeder iFlyFeeder;

    // no constructor
    private ResultPool() {
    }

    public void startRecording() throws IOException {
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists() && !dir.mkdir()) {
            throw new IOException("mkdir failed");
        }
        File f = new File(TEMP_OUTPUT_PATH);
        if (f.exists()) {
            ReturnValueEater.eat(f.delete());
        }
        ReturnValueEater.eat(f.createNewFile());
        try {
            startTime = System.currentTimeMillis();
            recorder = new AudioUtil.AudioRecordThread(TEMP_OUTPUT_PATH);
            recorder.start();
            iFlyFeeder = new IFlyFeeder(this);
            iFlyFeeder.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            Logger.log(LOG_TAG, e);
        }
    }

    public long getStartTime() {
        return startTime;
    }

    private String findValidPath() {
        String ret;
        while (new File(ret = OUTPUT_DIR + currentFile + ".wav").exists())
            ++currentFile;
        return ret;
    }

    public String getCurrentPath() {
        return OUTPUT_DIR + currentFile + ".wav";
    }

    public void generateWav(long requestStartTime) {
        String outPath = findValidPath();
        if (requestStartTime < startTime) {
            throw new IllegalArgumentException("requestStartTime must <= startTime");
        }
        long relativeStart = requestStartTime - startTime;
        long relativeEnd = System.currentTimeMillis() - startTime;
        // findValidPath() called in client(horrible hack)
        try {
            AudioUtil.pcmToWav(TEMP_OUTPUT_PATH, outPath, relativeStart, relativeEnd);
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
    }

    public void stopRecording() {
        if (recorder != null) {
            recorder.stopRecording();
            recorder = null;
        }
        if (iFlyFeeder != null) {
            iFlyFeeder.cancel(true);
            iFlyFeeder = null;
        }
    }

    public static ResultPool getInstance() {
        return instance;
    }

    public void clearAll() {
        times.clear();
        results.clear();
    }

    // set to public only for test
    public void putResult(long time, String result) {
        if (!times.isEmpty()) {
            long lastTime = times.get(times.size() - 1);
            if (lastTime > time) {
                throw new IllegalArgumentException("time must >= lastTime");
            }
        }
        times.add(time);
        results.add(result);
    }

    public String resultFrom(long requestStart) {
        StringBuilder sb = new StringBuilder();
        int index = Collections.binarySearch(times, requestStart);
        if (index < 0) {
            index = -index - 1;
        }
        for (; index < results.size(); ++index) {
            sb.append(results.get(index));
        }
        return sb.toString();
    }
}
