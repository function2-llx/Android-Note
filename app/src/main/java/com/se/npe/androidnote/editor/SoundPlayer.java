package com.se.npe.androidnote.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.se.npe.androidnote.R;

import java.lang.ref.WeakReference;

import cn.jzvd.JzvdStd;

public class SoundPlayer extends RelativeLayout {
    public SoundPlayer(Context context) {
        this(context, null);
    }

    public SoundPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.sound_player, this);
    }
//    private static class ThumbnailSetter extends Handler {
//        WeakReference<SoundPlayer> soundPlayer;
//
//        public ThumbnailSetter(SoundPlayer soundPlayer) {
//            this.soundPlayer = new WeakReference<>(soundPlayer);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            SoundPlayer instance = soundPlayer.get();
//            int width = instance.getWidth();
//            int height = instance.getHeight();
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            bitmap.eraseColor(Color.parseColor("#000000"));
//            instance.thumbImageView.setImageBitmap(bitmap);
//        }
//    }
//
//    ThumbnailSetter thumbnailSetter;
//
//    public SoundPlayer(Context context) {
//        super(context);
//        thumbnailSetter = new ThumbnailSetter(this);
//        thumbnailSetter.sendEmptyMessageDelayed(0, 50);
//    }
}
