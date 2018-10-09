package com.se.npe.androidnote.models;

import android.graphics.Bitmap;

import com.se.npe.androidnote.interfaces.IData;

/**
 * Picture data <-> an ImageView in the editor
 *
 * @author MashPlant
 */

public class PictureData implements IData {
    private String picturePath;
    private Bitmap picture;

    public PictureData(String picturePath, Bitmap picture) {
        this.picturePath = picturePath;
        this.picture = picture;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public Bitmap getPicture() {
        return picture;
    }
    public String toString(){
        return "Picture"+"asdfg"+picturePath;
    }
}
