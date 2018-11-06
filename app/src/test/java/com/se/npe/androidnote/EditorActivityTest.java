package com.se.npe.androidnote;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.se.npe.androidnote.editor.SortRichEditor;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import com.se.npe.androidnote.sound.ResultPool;

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
public class EditorActivityTest {
    @Test
    public void testCreateActivity() {
        EditorActivity activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        assertNotNull(toolbar);
        activity.setSupportActionBar(toolbar);
        assertNotNull(activity.getSupportActionBar());
        // can't test button.

        SortRichEditor editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);

        FloatingActionsMenu insertMedia = activity.findViewById(R.id.insert_media);
        assertNotNull(insertMedia);

        assertNotNull(activity.getIntent());
        if (activity.getIntent().getBooleanExtra(activity.VIEW_ONLY, false)) {
            editor.setViewOnly();
            assertTrue(editor.testIsViewOnly());
            insertMedia.setVisibility(View.GONE);
            assertTrue(insertMedia.getVisibility() == View.GONE);
            activity.findViewById(R.id.scroll_edit).setVisibility(View.GONE);
            assertTrue(activity.findViewById(R.id.scroll_edit).getVisibility() == View.GONE);
        } else {
            editor.setMarkdownController(activity.findViewById(R.id.scroll_edit));
        }
    }
}