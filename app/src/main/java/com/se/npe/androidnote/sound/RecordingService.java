package com.se.npe.androidnote.sound;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Daniel on 12/28/2014.
 */
public class RecordingService extends Service {
    public static final String OUTPUT_DIR =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidNote/";
    public static final String START_RECORDING = "StartRecording";
    // when the client want the sound to start(considering ahead time)
    public static final String REQUEST_START_TIME = "RequestStartTime";
    public static final String START_TIME = "StartTime"; // when EditorActivity start the service
    public static final String SOUND_PATH = "SoundPath";
    // always save the recording file at 0.pcm
    // when a request comes, crop a part of 0.pcm to generate a x.wav
    private static final String TEMP_OUTPUT_PATH = OUTPUT_DIR + 0 + ".pcm";

    // everything is static for convenience
    // in this way there is no need to store/load the state
    private static int currentFile = 1;

    private static AudioUtil.AudioRecordThread recorder;
//    private static MediaRecorder mRecorder;

    private static long mStartingTimeMillis;

    private static long requestStartTime;

    // it shouldn't be public according to my design
    // but if the client simply call stopService(intent), the path won't be updated in time
    // so the client must call findValidPath() himself
    public static void findValidPath() {
        while (new File(getOutputPath()).exists())
            ++currentFile;
    }

    public static String getOutputPath() {
        return OUTPUT_DIR + currentFile + ".wav";
    }

    public static void stopRecording() {
        if(recorder == null)return ;
        recorder.stopRecording();
        recorder = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(START_RECORDING)) { // initial record
            try {
                startRecording();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // half way request begin, remember the request start time
            long startTime = intent.getLongExtra(REQUEST_START_TIME, -1);
            if (startTime < mStartingTimeMillis) {
                throw new IllegalArgumentException("REQUEST_START_TIME not found or invalid");
            }
            requestStartTime = startTime;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        long start = requestStartTime - mStartingTimeMillis;
        long end = System.currentTimeMillis() - mStartingTimeMillis;
        // findValidPath() called in client(horrible hack)
        try {
            AudioUtil.pcmToWav(TEMP_OUTPUT_PATH, getOutputPath(), start, end);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startRecording() throws IOException {
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
        recorder = new AudioUtil.AudioRecordThread(TEMP_OUTPUT_PATH);
        recorder.start();
        mStartingTimeMillis = System.currentTimeMillis();
    }
}
