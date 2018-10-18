package com.se.npe.androidnote.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.util.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.jzvd.JzvdStd;

public class SoundPlayer extends RelativeLayout {
    private static class ProgressSetter extends AsyncTask<Void, Integer, Void> {
        WeakReference<SoundPlayer> ref;

        ProgressSetter(SoundPlayer ref) {
            this.ref = new WeakReference<>(ref);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MediaPlayer mediaPlayer = ref.get().mediaPlayer;
            ProgressBar progressBar = ref.get().progressBar;
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!mediaPlayer.isPlaying()) {
                    continue;
                }
                onProgressUpdate(progressBar.getMax() * mediaPlayer.getCurrentPosition()
                        / mediaPlayer.getDuration());
            }
//            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int now = values[0];
            ref.get().progressBar.setProgress(now);
        }
    }

    private ImageButton play, backward, forward;
    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;
    private ProgressSetter progressSetter;

    public SoundPlayer(Context context) {
        this(context, null);
    }

    public SoundPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.sound_player, this);
        play = findViewById(R.id.sound_player_play);
        backward = findViewById(R.id.sound_player_backward);
        forward = findViewById(R.id.sound_player_forward);
        mediaPlayer = new MediaPlayer();
        findViewById(R.id.sound_player_play).setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
//                play.setBackgroundResource(R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
//                play.setImageResource(R.drawable.ic_media_pause);
            }
        });
        progressBar = findViewById(R.id.sound_player_progress);
        progressSetter = new ProgressSetter(this);
        progressSetter.execute();
    }

    public void destroy() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        progressSetter.cancel(true);
    }

    public void setSource(String source) {
        try {
            mediaPlayer.setDataSource(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }
}
