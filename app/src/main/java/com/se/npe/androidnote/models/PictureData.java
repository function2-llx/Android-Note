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

    public PictureData(String picturePath)
    {
        this.picturePath = picturePath;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public Bitmap getPicture() {
        return picture;
    }

    @Override
    public boolean equals(Object another) {
        // TODO: remove field picture?
        // Only check whether picturePath are equal
        return another.getClass() == this.getClass()
                && this.picturePath.equals(((PictureData) another).picturePath);
    }

    @Override
    public String toString() {
        return "Picture" + "asdfg" + picturePath;
    }
}
