package com.se.npe.androidnote.editor;

import android.widget.RelativeLayout;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class SortRichEditorTest {
    SortRichEditor editor;
    EditorActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
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
            emptyView = (RelativeLayout) field.get(editor2);
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
        emptyView.performClick();
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
    public void testOnTouchEvent() {
    }

    @Test
    public void testPerformClick() {
        editor.performClick();
    }

    @Test
    public void testDestroy() {
        editor.destroy();
    }

    @Test
    public void setViewOnly() {
        editor.setViewOnly();
        assertTrue(editor.testIsViewOnly());
    }
}