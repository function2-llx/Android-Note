package com.se.npe.androidnote.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.interfaces.INoteFileConverter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class NoteZipConverterTest {

    private NoteZipConverter noteZipConverter;

    @Before
    public void setUp() {
        noteZipConverter = new NoteZipConverter();
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        Context context = activity.getApplicationContext();
        TableOperate.init(context); // initialize SAVE_PATH
    }

    @Test
    public void importNoteFromFile() {
        exportNoteToFile();
        noteZipConverter.importNoteFromFile((Note note) ->
                        assertEquals(DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN), note)
                , getExportFilePath());
    }

    @Test
    public void exportNoteToFile() {
        Note note = DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN);
        noteZipConverter.exportNoteToFile((String filePathName) ->
                        assertEquals(getExportFilePath(), filePathName),
                note, DataExample.EXAMPLE_MIX_IN);
        File exportDir = new File(INoteFileConverter.getExportDirPath());
        File exportFile = new File(getExportFilePath());
        assertTrue(exportFile.exists());
        assertNotEquals(0, exportDir.listFiles().length);
    }

    @NonNull
    private String getExportFilePath() {
        return INoteFileConverter.getExportFilePath(DataExample.EXAMPLE_MIX_IN + ".note");
    }
}