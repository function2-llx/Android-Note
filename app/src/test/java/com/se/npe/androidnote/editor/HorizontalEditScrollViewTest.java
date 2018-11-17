package com.se.npe.androidnote.editor;

import android.view.View;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class HorizontalEditScrollViewTest {
    private HorizontalEditScrollView markdownController = null;
    private SortRichEditor editor = null;
    private EditorActivity activity = null;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
        markdownController = activity.findViewById(R.id.scroll_edit);
        assertNotNull(markdownController);
    }

    @Test
    public void onClick() {
    }

    @Test
    public void onLongClick() {
        markdownController.onLongClick(activity.findViewById(R.id.img_block_quote));
    }
}