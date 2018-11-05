package com.se.npe.androidnote.models;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.interfaces.INoteFileConverter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class NotePdfConverterTest {

    private NotePdfConverter notePdfConverter;

    @Before
    public void setUp() {
        notePdfConverter = new NotePdfConverter();
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        Context context = activity.getApplicationContext();
        TableConfig.SAVE_PATH = context.getExternalFilesDir(null).getAbsolutePath(); // initialize SAVE_PATH
    }

    @Test
    public void importNoteFromFile() {
    }

    @Test
    public void exportNoteToFile() {
        Note note = DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN);
        notePdfConverter.exportNoteToFile(note, DataExample.EXAMPLE_MIX_IN);

        File exportDir = new File(INoteFileConverter.getExportDirPath());
        File exportFile = new File(INoteFileConverter.getExportFilePath(DataExample.EXAMPLE_MIX_IN + ".pdf"));
        assertTrue(exportFile.exists());
        assertNotEquals(0, exportDir.listFiles().length);
        for (File file : exportDir.listFiles()) {
            System.out.println(file.getAbsolutePath());
        }
    }
}