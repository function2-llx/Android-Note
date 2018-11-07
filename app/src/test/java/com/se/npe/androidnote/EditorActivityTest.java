package com.se.npe.androidnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.se.npe.androidnote.editor.SortRichEditor;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import com.se.npe.androidnote.sound.ResultPool;

import java.util.Date;

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
}