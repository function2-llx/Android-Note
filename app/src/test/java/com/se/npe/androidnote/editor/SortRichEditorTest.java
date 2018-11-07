package com.se.npe.androidnote.editor;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class SortRichEditorTest {
    SortRichEditor editor;
    EditorActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
    }

    @Test
    public void testEmptyViewClick() {
        editor.getEmptyView().performClick();
    }

    @Test
    public void sort() {
        assertNotNull(editor);
        editor.sort();
        editor.sort();
    }

    @Test
    public void changeIsMarkdown() {
        editor.changeIsMarkdown();
        editor.changeIsMarkdown();
    }

    @Test
    public void showOrHideKeyboard() {
    }

    @Test
    public void addPicture() {
    }

    @Test
    public void addVideo() {
    }

    @Test
    public void addSound() {
    }

    @Test
    public void destroy() {
    }

    @Test
    public void loadNote() {
    }

    @Test
    public void buildNote() {
    }

    @Test
    public void setViewOnly() {
        editor.setViewOnly();
        assertTrue(editor.testIsViewOnly());
    }

    @Test
    public void setMarkdownController() {
    }
}