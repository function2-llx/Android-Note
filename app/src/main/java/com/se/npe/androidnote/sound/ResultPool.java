package com.se.npe.androidnote.sound;

import android.os.Environment;

import com.se.npe.androidnote.util.Logger;

import java.io.File;
import java.io.IOException;
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

    public String generateWav(long requestStartTime) {
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
        return outPath;
    }

    public void stopRecording() {
        if (recorder == null) {
            return;
        }
        recorder.stopRecording();
        recorder = null;
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

    public String resultFrom(long offset) {
        StringBuilder sb = new StringBuilder();
        int index = Collections.binarySearch(times, System.currentTimeMillis() - offset);
        if (index < 0) {
            index = -index - 1;
        }
        for (; index < results.size(); ++index) {
            sb.append(results.get(index));
        }
        return sb.toString();
    }
}
