package com.se.npe.androidnote.interfaces;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.models.TableOperate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class INoteFileConverterTest {

    private static final String FILE_NAME = "test";

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        Context context = activity.getApplicationContext();
        TableOperate.init(context); // initialize TableConfig.SAVE_PATH
    }

    @Test
    public void createFileToExport() {
        INoteFileConverter.createFileToExport(FILE_NAME);
        // successfully create dir
        File exportDir = new File(INoteFileConverter.getExportDirPath());
        assertTrue(exportDir.exists());
        // successfully create file
        File exportFile = new File(INoteFileConverter.getExportFilePath(FILE_NAME));
        assertTrue(exportFile.exists());
        assertNotNull(exportDir.listFiles());
    }
}