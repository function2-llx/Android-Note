package com.se.npe.androidnote;

import android.support.v7.widget.Toolbar;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.se.npe.androidnote.editor.SortRichEditor;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

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
        // can't test button. todo

        SortRichEditor editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);

        FloatingActionsMenu insertMedia = activity.findViewById(R.id.insert_media);
        assertNotNull(insertMedia);
    }
}