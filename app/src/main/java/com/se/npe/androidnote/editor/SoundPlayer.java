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
            int elapsed = 0;
            long lastTime = System.currentTimeMillis();
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long now = System.currentTimeMillis();
                elapsed += (int) (now - lastTime);
                lastTime = now;
                int percentage = ref.get().progressBar.getMax() * elapsed / ref.get().mediaPlayer.getDuration();
                if (percentage >= ref.get().progressBar.getMax()) {
                    break;
                }
                onProgressUpdate(percentage);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int now = values[0];
            ref.get().progressBar.setProgress(now);
        }
    }

    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;

    public SoundPlayer(Context context) {
        this(context, null);
    }

    public SoundPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.sound_player, this);
        mediaPlayer = new MediaPlayer();
        findViewById(R.id.sound_player_play).setOnClickListener(v -> {
            mediaPlayer.start();
            new ProgressSetter(this).execute();
        });
        progressBar = findViewById(R.id.sound_player_progress);
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
