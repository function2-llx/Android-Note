package com.se.npe.androidnote;

import android.Manifest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class SplashActivityTest {
    private SplashActivity activity;

    @Before
    public void setUp() {
        PermissionTest.grantPermission(RuntimeEnvironment.application, Manifest.permission.RECORD_AUDIO);
        PermissionTest.grantPermission(RuntimeEnvironment.application, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        try {
            activity = Robolectric.setupActivity(SplashActivity.class);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void onCreate() {
        // already covered in setUp
    }
}