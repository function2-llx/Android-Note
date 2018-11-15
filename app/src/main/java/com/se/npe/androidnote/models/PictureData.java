package com.se.npe.androidnote.models;

import com.se.npe.androidnote.interfaces.IData;

import java.util.Objects;

/**
 * Picture data <-> an ImageView in the editor
 *
 * @author MashPlant
 */

public class PictureData implements IData {
    private String picturePath;

    public PictureData(String picturePath) {
        this.picturePath = picturePath;
    }

    @Override
    public String getType() {
        return "Pic";
    }

    @Override
    public String getPath() {
        return picturePath;
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public String toString() {
        return "Picture" + TableConfig.FileSave.LINE_SEPARATOR + picturePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PictureData that = (PictureData) o;
        return Objects.equals(picturePath, that.picturePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(picturePath);
    }
}
