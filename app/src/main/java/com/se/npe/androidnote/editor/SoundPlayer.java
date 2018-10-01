package com.se.npe.androidnote.editor;

import android.content.Context;
import android.util.AttributeSet;

import cn.jzvd.JzvdStd;

// only a marker interface to distinguish SoundPlayer from JzvdStd
interface ISoundPlayer {
}

public class SoundPlayer extends JzvdStd implements ISoundPlayer {
    public SoundPlayer(Context context) {
        super(context);
    }

    public SoundPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
