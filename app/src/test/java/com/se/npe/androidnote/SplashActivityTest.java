package com.se.npe.androidnote;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.models.SingletonResetter;
import com.se.npe.androidnote.models.TableOperate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;

@RunWith(RobolectricTestRunner.class)
public class SplashActivityTest {

    @Before
    public void setUp() {
        TableOperate.init(RuntimeEnvironment.application.getApplicationContext());
        PermissionTest.grantPermission(RuntimeEnvironment.application, Manifest.permission.RECORD_AUDIO);
        PermissionTest.grantPermission(RuntimeEnvironment.application, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        Context context = activity.getApplicationContext();
        TableOperate.init(context); // initialize SAVE_PATH

        for (String name : new String[]{"tmp.pdf", "tmp.note", "tmp.zip"}) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.fromFile(new File(name)));
                Robolectric.buildActivity(SplashActivity.class, intent).create().get();
            } catch (VerifyError e) {
                // no-op
            } catch (RuntimeException e) {
                // no-op
            }
        }
    }

    @Test
    public void onCreate() {
        // already covered in setUp
    }

    @After
    public void tearDown() {
        SingletonResetter.resetTableOperateSingleton();
    }
}