package com.se.npe.androidnote;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SoundRecorderActivityTest {
    private SoundRecorderActivity activity;
//    private ShadowActivity shadowActivity;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(SoundRecorderActivity.class);
//        shadowActivity = shadowOf(activity);
    }

    @Test
    public void onRecord() {
        activity.onRecord(true);
        activity.onRecord(false);
    }
}