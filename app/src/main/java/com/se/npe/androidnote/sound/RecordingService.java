package com.se.npe.androidnote.sound;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.se.npe.androidnote.R;
import com.se.npe.androidnote.SoundRecorderActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Daniel on 12/28/2014.
 */
public class RecordingService extends Service {
    public static final String OUTPUT_DIR =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidNote/";
    public static final String START_RECORDING = "StartRecording";
    public static final String AHEAD_TIME = "AheadTime"; // how long the user record ahead of time
    public static final String STARTED_TIME = "StartTime"; // how long EditorActivity start the service
    public static final String SOUND_PATH = "SoundPath";

    private static int currentFile = 1;
    private static final String LOG_TAG = "RecordingService";

    private MediaRecorder mRecorder = null;

    private long mStartingTimeMillis = 0;
    private int mElapsedSeconds = 0;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private long cropTime = 0;

    // always save the recording file at 0.mp4
    // when a request comes, crop a part of 0.mp4
    private static String TEMP_OUTPUT_PATH = OUTPUT_DIR + 0 + ".mp4";

    public static String getOutputPath() {
        return OUTPUT_DIR + currentFile + ".mp4";
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(START_RECORDING)) { // initial record
            startRecording();
        } else { // half way request
            int startTime = intent.getIntExtra(AHEAD_TIME, 0);
            long current = System.currentTimeMillis();
            cropTime = Math.max(0, current - mStartingTimeMillis - startTime * 1000);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        File f = new File(OUTPUT_DIR);
        if (!f.exists()) {
            if (!f.mkdir()) {
                Log.e(LOG_TAG, "mkdir failed");
            }
        }

    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }

    private void findValid() {
        File f;
        while ((f = new File(getOutputPath())).exists())
            ++currentFile;
    }

    private void removePrev() {
        File dirs = new File(TEMP_OUTPUT_PATH);
        if (dirs.exists()) {
            dirs.delete();
        }
    }

    public void startRecording() {
        removePrev();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(TEMP_OUTPUT_PATH);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void stopRecording() {
        // TODO don't release it
        mRecorder.stop();
        mRecorder.pause();
        mRecorder.release();
        //remove notification
        mRecorder = null;
    }
}
