package com.se.npe.androidnote.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Logger {
    // print the exception to logcat rather than e.printStackTrace() (in this way no output at all)
    public static void log(String tag, @NonNull Exception e) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(stream, true)) {
            e.printStackTrace(ps);
        }
        Log.e(tag, stream.toString());
    }

    public static void logInfo(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void logError(String tag, String msg) {
        Log.e(tag, msg);
    }
}
