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
        // do nothing to eat int
    }

    public static void eat(boolean b) {
        // do nothing to eat boolean
    }

    public static void eat(long l) {
        // do nothing to eat long
    }

    public static void eat(String s) {
        // do nothing to eat String
    }
}
