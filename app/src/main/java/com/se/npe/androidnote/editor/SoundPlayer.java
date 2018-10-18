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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.util.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.jzvd.JzvdStd;

public class SoundPlayer extends RelativeLayout {
    private static class ProgressSetter extends AsyncTask<Void, Void, Void> {
        WeakReference<SoundPlayer> ref;

        ProgressSetter(SoundPlayer ref) {
            this.ref = new WeakReference<>(ref);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private MediaPlayer mediaPlayer;

    public SoundPlayer(Context context) {
        this(context, null);
    }

    public SoundPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.sound_player, this);
        mediaPlayer = new MediaPlayer();
        findViewById(R.id.sound_player_play).setOnClickListener(v -> {
            mediaPlayer.start();
        });
        new ProgressSetter(this).execute();
    }

    public void setSource(String source) {
        try {
            mediaPlayer.setDataSource(source);
        } catch (IOException e) {
            Logger.log("my", e);
        }
        mediaPlayer.prepareAsync();
    }
}
