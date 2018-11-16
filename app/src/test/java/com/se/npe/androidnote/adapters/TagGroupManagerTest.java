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

@RunWith(RobolectricTestRunner.class)
public class TagGroupManagerTest {

    private TagGroupManager tagGroupManager;
    private List<String> wholeTagList;

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        Context context = activity.getApplicationContext();
        tagGroupManager = new TagGroupManager(context);
        TableOperate.init(context); // initialize SAVE_PATH
        TableOperateTest.addExampleNote(new ArrayList<>());

        wholeTagList = new ArrayList<>();
        for (int i = 0; i < TableOperateTest.NOTE_LIST_SIZE; ++i) {
            wholeTagList.add(DataExample.getExampleNoteTag(String.valueOf(i)));
        }
    }

    @After
    public void tearDown() {
        SingletonResetter.resetTableOperateSingleton();
    }

    @Test
    public void switchCheckedState() {
        tagGroupManager.init();
        // check 0 & 3 with tag-all unchecked
        tagGroupManager.switchCheckedState(0);
        tagGroupManager.switchCheckedState(3);
        List<String> tagList = new ArrayList<>();
        tagList.add(DataExample.getExampleNoteTag(String.valueOf(0)));
        tagList.add(DataExample.getExampleNoteTag(String.valueOf(3)));
        assertEquals(tagList, tagGroupManager.getCheckedTags());
        // with tag-all checked
        tagGroupManager.switchCheckedState(TableOperateTest.NOTE_LIST_SIZE);
        assertEquals(wholeTagList, tagGroupManager.getCheckedTags());
    }

    @Test
    public void init() {
        tagGroupManager.init();
        assertEquals(wholeTagList, tagGroupManager.getCheckedTags());
    }

    @Test
    public void showAndHide() {
        tagGroupManager.show();
        assertEquals(View.VISIBLE, tagGroupManager.getVisibility());
        assertEquals(wholeTagList, tagGroupManager.getCheckedTags());
        tagGroupManager.hide();
        assertEquals(View.INVISIBLE, tagGroupManager.getVisibility());
    }
}