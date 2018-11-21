package com.se.npe.androidnote.editor;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.util.ReturnValueEater;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class EditorViewOnlyTest {
    private SortRichEditor editor;
    private EditorActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
    }

    @Test
    public void setViewOnly() {
        editor.setViewOnly();
        assertTrue(editor.testIsViewOnly());
    }
}
