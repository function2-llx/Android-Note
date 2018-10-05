package com.se.npe.androidnote.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import cn.jzvd.JzvdStd;

public class SoundPlayer extends JzvdStd {
    private static class ThumbnailSetter extends Handler {
        WeakReference<SoundPlayer> soundPlayer;

        public ThumbnailSetter(SoundPlayer soundPlayer) {
            this.soundPlayer = new WeakReference<>(soundPlayer);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SoundPlayer instance = soundPlayer.get();
            int width = instance.getWidth(), height = instance.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.parseColor("#FFFFFF"));
            instance.thumbImageView.setImageBitmap(bitmap);
        }
    }

    ThumbnailSetter thumbnailSetter;

    public SoundPlayer(Context context) {
        super(context);
        thumbnailSetter = new ThumbnailSetter(this);
        thumbnailSetter.sendEmptyMessageDelayed(0, 50);
    }
}
