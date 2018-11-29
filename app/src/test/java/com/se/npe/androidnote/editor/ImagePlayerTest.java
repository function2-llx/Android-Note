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
public class ImagePlayerTest {
    EditorActivity activity = null;
    SortRichEditor editor = null;
    ImagePlayer image = null;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
        image = new ImagePlayer(editor.getContext());
        assertNotNull(image);
    }

    @Test
    public void getImageView() {
        image.getImageView();
    }
}