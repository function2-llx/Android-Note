package com.se.npe.androidnote.editor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class PictureLoader extends AsyncTask<String, Void, Bitmap> {
    private WeakReference<ImageView> target;
    private int resultWidth; // always fill the width

    public PictureLoader(ImageView target, int resultWidth) {
        super();
        this.target = new WeakReference<>(target);
        this.resultWidth = resultWidth;
        Log.e("my", "picture load create");
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        target.get().setImageBitmap(bitmap);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Log.e("my", "picture load begin");
        String picturePath = strings[0];
        Bitmap old = BitmapFactory.decodeFile(picturePath);
        int width = old.getWidth();
        int height = old.getHeight();
        float scale = ((float) resultWidth) / width;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Log.e("my", "picture load success");
        return Bitmap.createBitmap(old, 0, 0, width, height, matrix, true);
    }
}
