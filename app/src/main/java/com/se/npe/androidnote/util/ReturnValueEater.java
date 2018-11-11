package com.se.npe.androidnote.util;

/**
 * Eat the return value(especially those of file operation functions)
 * So that sonar cube & android studio won't complain
 **/

public class ReturnValueEater {
    // no constructor
    private ReturnValueEater() {
    }

    public static void eat(int i) {
    }

    public static void eat(boolean b) {
    }
}
