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

    Context context;
    private NotePdfConverter notePdfConverter;

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        context = activity.getApplicationContext();
        notePdfConverter = new NotePdfConverter(context);
    }

    @Test
    public void importNoteFromFile() {
    }

    @Test
    public void exportNoteToFile() {
        Note note = DataExample.getExampleNote(DataExample.EXAMPLE_MIX_IN);
        notePdfConverter.exportNoteToFile(note, DataExample.EXAMPLE_MIX_IN);

        File exportDir = new File(INoteFileConverter.getExportDirPath(context));
        File exportFile = new File(INoteFileConverter.getExportFilePath(context, DataExample.EXAMPLE_MIX_IN + ".pdf"));
        assertTrue(exportFile.exists());
        assertNotEquals(0, exportDir.listFiles().length);
    }
}