package com.se.npe.androidnote.editor;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static android.os.SystemClock.sleep;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class SoundPlayerTest {
    EditorActivity activity = null;
    SortRichEditor editor = null;
    SoundPlayer sound = null;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
        sound = new SoundPlayer(editor.getContext());
        assertNotNull(sound);
    }

    @Test
    public void destroy() {
        sound.destroy();
    }

    @Test
    public void setSource() {
        try {
            sound.setSource("test");
        } catch (Exception e) {
        }
    }

    @Test
    public void getEditText() {
        sound.getEditText();
    }

    @Test
    public void testClick() {
        try {
            sound.findViewById(R.id.sound_player_play).performClick();
            sound.findViewById(R.id.sound_player_play).performClick();
        } catch (Exception e) {
        }
    }
}