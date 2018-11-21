package com.se.npe.androidnote.editor;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.util.ReturnValueEater;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class SortRichEditorTest {
    private SortRichEditor editor;
    private EditorActivity activity;
    private static final String MEDIA_PATH = "tmp";

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);

        // create medias for insertion
        File f = new File(MEDIA_PATH);
        try {
            ReturnValueEater.eat(f.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream out = new FileOutputStream(f)) {
            byte[] b = new byte[1000];
            out.write(b, 0, b.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sort() {
        assertNotNull(editor);
        editor.sort();
        editor.sort();
    }

    @Test
    public void changeIsMarkdown() {
        editor.changeIsMarkdown();
        editor.changeIsMarkdown();
    }

    @Test
    public void testComputeScroll() {
        editor.computeScroll();
    }

    @Test
    public void testAddMedia() {
        SoundPlayer.isUnderTest = true;
        editor.addSound(MEDIA_PATH);
        editor.addPicture(MEDIA_PATH);
        editor.addVideo(MEDIA_PATH);
        editor.destroy();
        LinearLayout containerLayout = editor.containerLayout;
        for (int i = 0; i < containerLayout.getChildCount(); ++i) {
            if (containerLayout.getChildAt(i) instanceof RelativeLayout) {
                RelativeLayout media = (RelativeLayout) containerLayout.getChildAt(i);
                View delete = media.getChildAt(1);
                delete.performClick();
            }
        }
        for (int i = 0; i < containerLayout.getChildCount(); ++i) {
            if (containerLayout.getChildAt(i) instanceof ImageView) {
                ImageView placeholder = (ImageView) containerLayout.getChildAt(i);
                placeholder.performClick();
            }
        }
        SoundPlayer.isUnderTest = false;
    }

    @Test
    public void testClickEmptyView() {
        editor.emptyView.performClick();
    }

    @After
    public void tearDown() {
        File f = new File(MEDIA_PATH);
        ReturnValueEater.eat(f.delete());
    }
}