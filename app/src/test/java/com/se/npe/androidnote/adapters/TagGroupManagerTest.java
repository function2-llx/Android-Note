package com.se.npe.androidnote.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.se.npe.androidnote.models.DataExample;
import com.se.npe.androidnote.models.SingletonResetter;
import com.se.npe.androidnote.models.TableOperate;
import com.se.npe.androidnote.models.TableOperateTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class TagGroupManagerTest {

    private TagGroupManager tagGroupManager;

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        Context context = activity.getApplicationContext();
        tagGroupManager = new TagGroupManager(context);
        TableOperate.init(context); // initialize SAVE_PATH
        TableOperateTest.addExampleNote(new ArrayList<>());
    }

    @After
    public void tearDown() {
        SingletonResetter.resetTableOperateSingleton();
    }

    @Test
    public void switchCheckedState() {
        tagGroupManager.init();
        // check 0 & 3 with tag-all checked
        tagGroupManager.switchCheckedState(0);
        tagGroupManager.switchCheckedState(3);
        assertNull(tagGroupManager.getCheckedTags());
        // check 0 with tag-all unchecked
        tagGroupManager.switchCheckedState(TableOperateTest.NOTE_LIST_SIZE);
        List<String> tagList = new ArrayList<>();
        tagList.add(DataExample.getExampleNoteTag(String.valueOf(0)));
        tagList.add(DataExample.getExampleNoteTag(String.valueOf(3)));
        assertEquals(tagList, tagGroupManager.getCheckedTags());
    }

    @Test
    public void init() {
        tagGroupManager.init();
        assertNull(tagGroupManager.getCheckedTags());
    }

    @Test
    public void showAndHide() {
        tagGroupManager.show();
        assertEquals(View.VISIBLE, tagGroupManager.getVisibility());
        assertNull(tagGroupManager.getCheckedTags());
        tagGroupManager.hide();
        assertEquals(View.INVISIBLE, tagGroupManager.getVisibility());
    }
}