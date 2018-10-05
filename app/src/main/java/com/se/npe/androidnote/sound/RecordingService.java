package com.se.npe.androidnote.sound;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
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
    //    public static final String OUTPUT_PATH = OUTPUT_DIR + "out.mp4";
    private static int currentFile = 0;
    private static final String LOG_TAG = "RecordingService";

    private MediaRecorder mRecorder = null;

    private long mStartingTimeMillis = 0;
    private int mElapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private TimerTask mIncrementTimerTask = null;

    private long cropTime = 0;

    public static String getOutputPath() {
        return OUTPUT_DIR + currentFile + ".mp4";
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("StartRecording")) { // initial record
            startRecording();
        } else { // half way request
            int startTime = intent.getIntExtra("StartTime", 0);
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

    private void findValidPath() {
        File f;
        while ((f = new File(getOutputPath())).exists())
            ++currentFile;
    }

    public void startRecording() {
        findValidPath();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(getOutputPath());
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
//        mRecorder
        mRecorder.stop();
        mRecorder.release();
        //remove notification
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }
        mRecorder = null;
    }

    private void startTimer() {
        Timer timer = new Timer();
        mIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;
                if (onTimerChangedListener != null)
                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
                NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mgr.notify(1, createNotification());
            }
        };
        timer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
    }

    private Notification createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_mic_white_36dp)
                        .setContentTitle(getString(R.string.notification_recording))
                        .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
                        .setOngoing(true);

        mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
                new Intent[]{new Intent(getApplicationContext(), SoundRecorderActivity.class)}, 0));

        return mBuilder.build();
    }
}
