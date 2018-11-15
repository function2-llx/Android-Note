package com.se.npe.androidnote;

import android.content.Intent;
import android.view.MenuItem;
import static org.robolectric.Shadows.shadowOf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
<<<<<<< app/src/test/java/com/se/npe/androidnote/EditorActivityTest.java
=======
import org.robolectric.RuntimeEnvironment;

import com.se.npe.androidnote.editor.SoundPlayer;
import com.se.npe.androidnote.models.TableOperate;
>>>>>>> app/src/test/java/com/se/npe/androidnote/EditorActivityTest.java

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
            /* no operation */
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
            /* no operation */
        }
    }

    @Test
    public void onOptionsItemSelected() {
        TableOperate.init(RuntimeEnvironment.application.getApplicationContext());
        clickOptionsMenuItem(android.R.id.home);
        clickOptionsMenuItem(R.id.menu_save);
        clickOptionsMenuItem(R.id.menu_markdown);
        clickOptionsMenuItem(R.id.viewonly_share);
        clickOptionsMenuItem(R.id.share);
        clickOptionsMenuItem(R.id.viewonly_export);
        clickOptionsMenuItem(R.id.export);
    }

    private void clickOptionsMenuItem(int optionsMenuItemId) {
        MenuItem menuItem = shadowOf(activity).getOptionsMenu().findItem(optionsMenuItemId);
        try {
            activity.onOptionsItemSelected(menuItem);
        } catch (Exception e) {
            /* no operation */
>>>>>>> app/src/test/java/com/se/npe/androidnote/EditorActivityTest.java
        }
    }

    @Test
    public void testClick() {
        activity.findViewById(R.id.insert_picture).performClick();
        activity.findViewById(R.id.insert_video).performClick();
        activity.findViewById(R.id.insert_sound).performClick();
    }

    @Test
    public void onActivityResult() {
        Intent intent = new Intent(activity, SoundPlayer.class);
        activity.startActivityForResult(intent, 0);
    }
}