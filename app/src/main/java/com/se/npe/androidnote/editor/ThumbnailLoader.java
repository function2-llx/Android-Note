package com.se.npe.androidnote.editor;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class ThumbnailLoader extends AsyncTask<String, Void, Bitmap> {
    private WeakReference<ImageView> target;

    public ThumbnailLoader(ImageView target) {
        this.target = new WeakReference<>(target);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        target.get().setImageBitmap(bitmap);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String videoPath = strings[0];
        return ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
    }
}
