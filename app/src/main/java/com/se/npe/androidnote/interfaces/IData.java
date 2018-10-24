package com.se.npe.androidnote.interfaces;

/**
 * Abstract data type in a note(only a mark interface)
 * A note is organized as a list of IData
 * This is for the purpose of moving and deleting element in a note
 *
 * @author MashPlant
 */

public interface IData {

    String getType();

    String getPath();
}
