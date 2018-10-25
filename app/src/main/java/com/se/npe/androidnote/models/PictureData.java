package com.se.npe.androidnote.models;

import com.se.npe.androidnote.interfaces.IData;

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
    public boolean equals(Object o) {
        return o instanceof PictureData && ((PictureData) o).picturePath.equals(picturePath);
    }

    @Override
    public int hashCode() {
        return picturePath.hashCode();
    }

    @Override
    public String toString() {
        return "Picture" + TableConfig.Filesave.LINE_SEPERATOR + picturePath;
    }
}
