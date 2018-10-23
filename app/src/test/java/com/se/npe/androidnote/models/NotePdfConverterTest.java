package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.INoteFileConverter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class NotePdfConverterTest {

    private static final String EXAMPLE_MIX_IN = "test";
    private String exportDir;
    private NotePdfConverter notePdfConverter;

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        notePdfConverter = new NotePdfConverter(activity.getApplicationContext());
        exportDir = INoteFileConverter.getExportDirPath(activity.getApplicationContext());
    }

    @Test
    public void importNoteFromFile() {
    }

    @Test
    public void exportNoteToFile() {
        Note note = getExampleNote(EXAMPLE_MIX_IN);
        notePdfConverter.exportNoteToFile(note, EXAMPLE_MIX_IN + ".pdf");
    }

    @NonNull
    private Note getExampleNote(String mixIn) {
        // Create a new example note
        String title = "This is the title for " + mixIn;
        List<IData> content = new ArrayList<>();
        content.add(new TextData("This is the content for " + mixIn));
        return new Note(title, content);
    }
}