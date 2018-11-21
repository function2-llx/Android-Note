package com.se.npe.androidnote.editor;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class EditorMarkdownTest {
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
    public void testBlockQuotesController() {

    }

    @Test
    public void testStyleController() {

    }

    @Test
    public void testStrikeThroughController() {

    }

    @Test
    public void testCodeController() {

    }

    @Test
    public void testListController() {

    }
}
