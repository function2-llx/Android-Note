package com.se.npe.androidnote.interfaces;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class INoteFileConverterTest {

    private Context context;
    private static final String FILE_NAME = "test";

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        context = activity.getApplicationContext();
    }

    @Test
    public void createFileToExport() {
        INoteFileConverter.createFileToExport(FILE_NAME);
        // successfully create dir
        File exportDir = new File(INoteFileConverter.getExportDirPath());
        assertNotNull(exportDir);
        // successfully create file
        File exportFile = new File(INoteFileConverter.getExportFilePath(FILE_NAME));
        assertNotNull(exportFile);
        assertNotNull(exportDir.listFiles());
    }
}