package com.se.npe.androidnote.editor;

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
    public void testEmptyViewClick() {
        Class<?> clazz = SortRichEditor.class;
        Field field = null;
        RelativeLayout emptyView = null;

        EditorActivity activity2 = Robolectric.setupActivity(EditorActivity.class);
        SortRichEditor editor2 = activity2.findViewById(R.id.rich_editor);

        try {
            field = clazz.getDeclaredField("emptyView");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        assertNotNull(field);

        // sonar cube complains about it, and thinks we need a null check
        // actually we don't
        if (field != null) {
            field.setAccessible(true);
        }

        try {
            if (field != null) {
                emptyView = (RelativeLayout) field.get(editor2);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        assertNotNull(emptyView);

        editor2.sort();

        // same as above
        if (emptyView != null) {
            emptyView.performClick();
        }

        try {
            emptyView = (RelativeLayout) field.get(editor);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        assertNotNull(emptyView);

        // same as above
        if (emptyView != null) {
            emptyView.performClick();
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
        try {
            editor.addSound(MEDIA_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            editor.addPicture(MEDIA_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            editor.addVideo(MEDIA_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.destroy();
    }

    @After
    public void tearDown() {
        File f = new File(MEDIA_PATH);
        ReturnValueEater.eat(f.delete());
    }
}