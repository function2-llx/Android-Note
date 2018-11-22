package com.se.npe.androidnote.editor;

import android.widget.RelativeLayout;

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
        SoundPlayer.setIsUnderTest(true);
        EditorActivity activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        SortRichEditor editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
        editor.addSound("tmp.wav");
        for (int i = 0; i < editor.getContainerLayout().getChildCount(); ++i) {
            if (editor.getContainerLayout().getChildAt(i) instanceof RelativeLayout) {
                sound = (SoundPlayer) ((RelativeLayout) editor.getContainerLayout().getChildAt(i))
                        .getChildAt(0);
            }
        }
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
        try {
            new SoundPlayer.ProgressSetter(sound).run();
        } catch (Exception e) {
            // no-op
        }
    }

    @After
    public void tearDown() {
        SoundPlayer.setIsUnderTest(false);
    }
}