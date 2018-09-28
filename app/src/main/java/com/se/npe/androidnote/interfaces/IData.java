package com.se.npe.androidnote.interfaces;

import android.view.View;

/**
 * Abstract data type in a note.
 * A note is organized as a list of IData.
 * A IData can be created by a view(using DataFactory) and can render as a view.
 * This is for the purpose of moving and deleting element in a note.
 * @author MashPlant
 * */

public interface IData {
    View renderAsView();
}
