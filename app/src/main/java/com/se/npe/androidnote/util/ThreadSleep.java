package com.se.npe.androidnote.util;

/**
 * A wrapper class for Thread.sleep()
 */

public class ThreadSleep {
    // no constructor
    private ThreadSleep() {

    }

    public static void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (Exception e) {
            // no-op
        }
    }
}
