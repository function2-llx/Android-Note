package com.se.npe.androidnote.models;

import android.graphics.Bitmap;

import com.se.npe.androidnote.interfaces.IData;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        // TODO: remove field picture?
        // Only check whether picturePath are equal
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PictureData that = (PictureData) o;
        return Objects.equals(picturePath, that.picturePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(picturePath);
    }

    @Override
    public String toString() {
        return "Picture" + "asdfg" + picturePath;
    }
}
