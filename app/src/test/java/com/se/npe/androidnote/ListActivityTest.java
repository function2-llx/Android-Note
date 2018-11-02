package com.se.npe.androidnote;

import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;

import static com.se.npe.androidnote.EditorActivity.CURRENT_GROUP;
import static com.se.npe.androidnote.EditorActivity.VIEW_ONLY;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class ListActivityTest {

    @Test
    public void onCreateOptionsMenu() {
        ListActivity activity = Robolectric.setupActivity(ListActivity.class);

    }

    @Test
    public void onOptionsItemSelected() {
    }

    @Test
    public void onCreate() {
    }

    @Test
    public void onDestroy() {
    }

    @Test
    public void onResume() {
    }

    public static void checkListActivityStartEditorActivity(ListActivity listActivity, boolean expectedViewOnly, String expectedCurrentGroup) {
        // check EditorActivity is properly started
        ShadowActivity shadowActivity = shadowOf(listActivity);
        Intent nextIntent = shadowActivity.getNextStartedActivity();
        assertEquals(EditorActivity.class.getName(), nextIntent.getComponent().getClassName());
        // check EditorActivity's extra is properly transported
        assertEquals(expectedViewOnly, nextIntent.getBooleanExtra(VIEW_ONLY, false));
        assertEquals(expectedCurrentGroup, nextIntent.getStringExtra(CURRENT_GROUP));
    }
}