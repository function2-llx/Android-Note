package com.se.npe.androidnote.interfaces;

import java.io.Serializable;

/**
 * Abstract data type in a note(only a mark interface)
 * A note is organized as a list of IData
 * This is for the purpose of moving and deleting element in a note
 *
 * @author MashPlant
 */

public interface IData extends Serializable {

    String getType();

    String getPath();

    String getText();
}
