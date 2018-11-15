package com.se.npe.androidnote;

import android.content.Intent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class EditorActivityTest {
    EditorActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(EditorActivity.class);
        assertNotNull(activity);
    }

    @Test
    public void testOnCreate() {
        assertNotNull(activity.findViewById(R.id.toolbar));
        assertNotNull(activity.findViewById(R.id.rich_editor));
        activity.findViewById(R.id.insert_picture).performClick();
        activity.findViewById(R.id.insert_video).performClick();
        activity.findViewById(R.id.insert_sound).performClick();
        activity.findViewById(R.id.rearrange_editor).performClick();

        // test another if branch
        Intent intent = new Intent();
        intent.putExtra(EditorActivity.VIEW_ONLY, true);
        EditorActivity activity2 = Robolectric.buildActivity(EditorActivity.class, intent).create().get();
        assertNotNull(activity2);
    }

    @Test
    public void testDestroy() {
        activity.onDestroy();
    }

    @Test
    public void testOpenCamera() {
        Class<?> clazz = EditorActivity.class;
        Method method;
        try {
            method = clazz.getDeclaredMethod("openCamera", int.class);
            assertNotNull(method);
            method.setAccessible(true);
            method.invoke(activity, 1);
        } catch (Exception e) {
            //Do Nothing
        }
    }

    @Test
    public void testPickMedia() {
        Class<?> clazz = EditorActivity.class;
        Method method;
        try {
            method = clazz.getDeclaredMethod("pickMedia", int.class);
            assertNotNull(method);
            method.setAccessible(true);
            method.invoke(activity, 1);
        } catch (Exception e) {
            //Do Nothing
        }
    }
}