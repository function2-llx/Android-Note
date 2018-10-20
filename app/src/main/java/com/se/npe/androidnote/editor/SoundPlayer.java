package com.se.npe.androidnote.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.se.npe.androidnote.util.MyAsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.jzvd.JzvdStd;

public class SoundPlayer extends RelativeLayout {
    private static final String LOG_TAG = SoundPlayer.class.getSimpleName();

    private static class ProgressSetter extends MyAsyncTask<Void, Void, Void> {
        WeakReference<SoundPlayer> ref;

        ProgressSetter(SoundPlayer ref) {
            super();
            this.ref = new WeakReference<>(ref);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                try {
                    Thread.sleep(100);
                    publishProgress();
                } catch (InterruptedException e) {
                    Logger.log(LOG_TAG, e);
                }
            }
//            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            MediaPlayer mediaPlayer = ref.get().mediaPlayer;
            ProgressBar progressBar = ref.get().progressBar;
            int now = progressBar.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
            progressBar.setProgress(now);
        }
    }
    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;
    private ProgressSetter progressSetter;

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
            }
        });
        progressBar = findViewById(R.id.sound_player_progress);
        progressSetter = new ProgressSetter(this);
        // set the progressSetter work on multi thread
        // or only one progressSetter will be working
        progressSetter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            Logger.log(LOG_TAG, e);
        }
        mediaPlayer.prepareAsync();
    }
}
