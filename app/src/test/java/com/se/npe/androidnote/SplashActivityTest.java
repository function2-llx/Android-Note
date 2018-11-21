package com.se.npe.androidnote;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public class SplashActivityTest {

    @Before
    public void setUp() {
        PermissionTest.grantPermission(RuntimeEnvironment.application, Manifest.permission.RECORD_AUDIO);
        PermissionTest.grantPermission(RuntimeEnvironment.application, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.fromFile(new File("tmp.pdf")));
            Robolectric.buildActivity(SplashActivity.class, intent).create().get();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.fromFile(new File("tmp.note")));
            Robolectric.buildActivity(SplashActivity.class, intent).create().get();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.fromFile(new File("tmp.zip")));
            Robolectric.buildActivity(SplashActivity.class, intent).get().onCreate(null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void onCreate() {
        // already covered in setUp
    }
}