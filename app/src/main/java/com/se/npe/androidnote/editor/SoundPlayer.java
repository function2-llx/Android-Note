package com.se.npe.androidnote.editor;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.se.npe.androidnote.R;
import com.se.npe.androidnote.util.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class SoundPlayer extends RelativeLayout {
    private static final String LOG_TAG = SoundPlayer.class.getSimpleName();

    static class ProgressSetter implements Runnable {
        WeakReference<SoundPlayer> ref;

        ProgressSetter(SoundPlayer ref) {
            this.ref = new WeakReference<>(ref);
        }

        @Override
        public void run() {
            MediaPlayer mediaPlayer = ref.get().mediaPlayer;
            ProgressBar progressBar = ref.get().progressBar;
            int now = progressBar.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
            progressBar.setProgress(now);
            if (mediaPlayer.isPlaying()) {
                new Handler().postDelayed(this, 100);
            }
        }
    }

    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;
    private EditText editText;

    public SoundPlayer(Context context) {
        this(context, null);
    }

    public SoundPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.sound_player, this);
        mediaPlayer = new MediaPlayer();
        findViewById(R.id.sound_player_play).setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
                new Handler().postDelayed(new ProgressSetter(this), 100);
            }
        });
        progressBar = findViewById(R.id.sound_player_progress);
        editText = findViewById(R.id.sound_player_text);
    }

    public void destroy() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void setSource(String source) {
        try {
            mediaPlayer.setDataSource(source);
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
        mediaPlayer.prepareAsync();
    }

    public EditText getEditText() {
        return editText;
    }
}
