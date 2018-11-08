package com.se.npe.androidnote.editor;

import android.view.inputmethod.EditorInfo;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class DeletableEditTextTest {
    EditorActivity activity;
    SortRichEditor sortRichEditor;
    DeletableEditText editor;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        sortRichEditor = activity.findViewById(R.id.rich_editor);
        assertNotNull(sortRichEditor);
        editor = new DeletableEditText(sortRichEditor.getContext());
        assertNotNull(editor);
    }

    @Test
    public void testOnCreateInputConnection() {
        editor.onCreateInputConnection(new EditorInfo());
    }

    @Test
    public void testDeleteSurroundingText() {
        editor.onCreateInputConnection(new EditorInfo()).deleteSurroundingText(1, 0);
    }
}