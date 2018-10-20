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

    static class IFlyFeeder extends AsyncTask<Void, Void, Void> {
        private static final int SLEEP_MILL = 1000; // 1000ms
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
            iat = SpeechRecognizer.createRecognizer(null, null);
            iat.setParameter(SpeechConstant.DOMAIN, "iat");
            iat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            iat.setParameter(SpeechConstant.ACCENT, "mandarin");
            iat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
            iat.setParameter(SpeechConstant.RESULT_TYPE, "plain");
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
            final ResultPool target = ref.get();
            while (true) {
                final long now = System.currentTimeMillis();
                byte[] voiceBuffer = null;
                try {
                    voiceBuffer = new byte[fis.available() - currentPcmByte];
                    fis.skip(currentPcmByte);
                    fis.read(voiceBuffer);
                    currentPcmByte = fis.available();
                } catch (IOException e) {
                    Logger.log(LOG_TAG, e);
                }
                if (voiceBuffer != null && voiceBuffer.length != 0) {
                    iat.startListening(new RecognizerListener() {
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
                            // skip 。
                            if (isLast) {
                                return;
                            }
                            String result = recognizerResult.getResultString();
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
                    });
                    iat.writeAudio(voiceBuffer, 0, voiceBuffer.length);
                    iat.stopListening();
                }
                try {
                    Thread.sleep(SLEEP_MILL);
                } catch (InterruptedException e) {
                    Logger.log(LOG_TAG, e);
                }
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
            if (!f.delete()) {
                throw new IOException("delete file failed " + TEMP_OUTPUT_PATH);
            }
        }
        if (!f.createNewFile()) {
            throw new IOException("create file failed " + TEMP_OUTPUT_PATH);
        }
        startTime = System.currentTimeMillis();
        recorder = new AudioUtil.AudioRecordThread(TEMP_OUTPUT_PATH);
        recorder.start();
        iFlyFeeder = new IFlyFeeder(this);
        iFlyFeeder.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
