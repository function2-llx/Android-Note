package com.se.npe.androidnote.editor;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class SoundPlayerTest {
    private SoundPlayer sound = null;

    @Before
    public void setUp() {
        SoundPlayer.isUnderTest = true;
        EditorActivity activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        SortRichEditor editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
        editor.addSound("tmp.wav");
        sound = (SoundPlayer) (editor.containerLayout.getChildAt(0));
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
            /* no operation */
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
            /* no operation */
        }
    }

    @Test
    public void testProgressSetter() {
        new SoundPlayer.ProgressSetter(sound).run();
    }

    @After
    public void tearDown() {
        SoundPlayer.isUnderTest = false;
    }
}