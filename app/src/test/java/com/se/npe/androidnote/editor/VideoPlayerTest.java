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
public class VideoPlayerTest {
    private VideoPlayer video = null;

    @Before
    public void setUp() {
        EditorActivity activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        SortRichEditor editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
        video = new VideoPlayer(editor.getContext());
        assertNotNull(video);
    }

    @Test
    public void getJzvdStd() {
        video.getJzvdStd();
    }
}