package com.se.npe.androidnote.editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.se.npe.androidnote.R;

// well, just for the consistency of naming
public class ImagePlayer extends RelativeLayout {
    private ImageView imageView;

    public ImagePlayer(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.image_player, this);
        imageView = findViewById(R.id.image_player_image);
    }

    public ImageView getImageView() {
        return imageView;
    }
}
