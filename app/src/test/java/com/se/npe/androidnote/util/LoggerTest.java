package com.se.npe.androidnote.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LoggerTest {
    private static final String LOG_TAG = "tag";
    private static final String LOG_MESSAGE = "message";

    @Test
    public void log() {
        Logger.log(LOG_TAG, new NullPointerException(LOG_MESSAGE));
    }

    @Test
    public void logInfo() {
        Logger.logInfo(LOG_TAG, LOG_MESSAGE);
    }

    @Test
    public void logError() {
        Logger.logError(LOG_TAG, LOG_MESSAGE);
    }
}