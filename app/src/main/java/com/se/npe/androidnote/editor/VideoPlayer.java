package com.se.npe.androidnote.editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.se.npe.androidnote.R;

import cn.jzvd.JzvdStd;

public class VideoPlayer extends RelativeLayout {
    private JzvdStd jzvdStd;

    public VideoPlayer(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.video_player, this);
        jzvdStd = findViewById(R.id.video_player_jzvd);
    }

    public JzvdStd getJzvdStd() {
        return jzvdStd;
    }
}
