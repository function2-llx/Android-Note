package com.se.npe.androidnote.editor;

import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.se.npe.androidnote.EditorActivity;
import com.se.npe.androidnote.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class SortRichEditorTest {
    SortRichEditor editor;
    EditorActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
        editor = activity.findViewById(R.id.rich_editor);
        assertNotNull(editor);
    }

    @Test
    public void testEmptyViewClick() throws Exception {
        Class<?> clazz = SortRichEditor.class;
        Field field = clazz.getDeclaredField("emptyView");
        assertNotNull(field);
        field.setAccessible(true);
        RelativeLayout emptyView = (RelativeLayout)field.get(editor);
        emptyView.performClick();
        editor.sort();
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