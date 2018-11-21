package com.se.npe.androidnote;

import com.se.npe.androidnote.util.ThreadSleep;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SoundRecorderActivityTest {
    private SoundRecorderActivity activity;

    @Before
    public void setUp() {

        activity = Robolectric.setupActivity(SoundRecorderActivity.class);
    }

    @Test
    public void onRecord() {
        activity.onRecord(true);
        ThreadSleep.sleep(1000);
        activity.onRecord(false);
    }
}