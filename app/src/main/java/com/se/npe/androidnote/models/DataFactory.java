package com.se.npe.androidnote.models;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import com.se.npe.androidnote.interfaces.IData;

/**
 * Create a IDate from a view.
 * According to the type of the view, getDataFromView can return a PictureData or TextData or ...
 * @author MashPlant
 */

public class DataFactory {
    public static IData getDataFromView(View v) {
        @SuppressWarnings("unchecked")
        String tag = (String) v.getTag(); // possible extra message, for example the uri of image
        if (v instanceof EditText) {

        } else if (v instanceof ImageView) {

        } else if (v instanceof VideoView) {

        } // else if (v instanceof ??? for play sound) I don't know yet
        return null;
    }
}
