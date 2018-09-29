package com.se.npe.androidnote.models;

import android.graphics.Bitmap;

import com.se.npe.androidnote.interfaces.IData;

/**
 * Picture data <-> an ImageView in the editor
 * @author MashPlant
 * */

public class PictureData implements IData {
    private String uri;
    private Bitmap picture;

}
