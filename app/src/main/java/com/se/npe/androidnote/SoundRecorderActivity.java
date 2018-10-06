package com.se.npe.androidnote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.se.npe.androidnote.sound.RecordingService;

public class SoundRecorderActivity extends AppCompatActivity {
    public static final int RESULT_CODE = 0;

    // Recording controls
    private FloatingActionButton mRecordButton = null;

    private TextView mRecordingPrompt;
    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    private Chronometer mChronometer = null;

    Intent consumerIntent;
    int aheadTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recorder);
        mChronometer = findViewById(R.id.chronometer);
        mRecordingPrompt = findViewById(R.id.recording_status_text);

        mRecordButton = findViewById(R.id.btnRecord);
        mRecordButton.setOnClickListener((v) -> {
            onRecord(mStartRecording);
            mStartRecording = !mStartRecording;
        });

        consumerIntent = getIntent();

        final String[] text = {"1 min ago", "30s ago", "now"};
        final int[] time = {60, 30, 0};
        final int startedTime = consumerIntent.getIntExtra(RecordingService.STARTED_TIME, 0);
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Ahead time")
                .setSingleChoiceItems(text, 2, (dialog, which) -> {
                    if (startedTime < time[which]) {
                        Toast.makeText(SoundRecorderActivity.this, "Haven't recorded that long",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    aheadTime = time[which];
                    onRecord(mStartRecording);
                    mStartRecording = !mStartRecording;
                    dialog.cancel();
                })
                .setCancelable(false)
                .show();
    }

    // Recording Start/Stop
    private void onRecord(boolean start) {
        Intent intent = new Intent(this, RecordingService.class);

        if (start) {
            // start recording
            mRecordButton.setIcon(R.drawable.ic_media_stop);

            //start Chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener((chronometer) -> {
                if (mRecordPromptCount == 0) {
                    mRecordingPrompt.setText(getString(R.string.record_in_progress).concat("."));
                } else if (mRecordPromptCount == 1) {
                    mRecordingPrompt.setText(getString(R.string.record_in_progress).concat(".."));
                } else if (mRecordPromptCount == 2) {
                    mRecordingPrompt.setText(getString(R.string.record_in_progress).concat("..."));
                    mRecordPromptCount = -1;
                }
                mRecordPromptCount++;
            });

            //start RecordingService
            intent.putExtra(RecordingService.AHEAD_TIME, aheadTime);
            startService(intent);
            //keep screen on while recording
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress).concat("."));
            mRecordPromptCount++;

        } else {
            //stop recording
            mRecordButton.setIcon(R.drawable.ic_mic_white_36dp);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mRecordingPrompt.setText(getString(R.string.record_prompt));

            stopService(intent);
            //allow the screen to turn off again once recording is finished
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            consumerIntent.putExtra(RecordingService.SOUND_PATH, RecordingService.getOutputPath());
            setResult(RESULT_CODE, consumerIntent);
            finish();
        }
    }
}
