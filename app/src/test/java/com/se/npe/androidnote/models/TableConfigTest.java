package com.se.npe.androidnote.models;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.se.npe.androidnote.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TableConfigTest {

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        Context context = activity.getApplicationContext();
        TableOperate.init(context); // initialize SAVE_PATH
    }

    @After
    public void tearDown() {
        SingletonResetter.resetTableOperateSingleton();
    }

    @Test
    public void savePathTest() {
        String savePath = "/test";
        TableConfig.FileSave.setSavePath(savePath);
        assertEquals(savePath, TableConfig.FileSave.getSavePath());
    }

    @Test
    public void sorterTest() {
        assertEquals(TableConfig.Sorter.getSorterFields()[0], TableConfig.Sorter.getDefaultSorterField());
        TableConfig.Sorter.getSorterFieldToComparator(TableConfig.Sorter.getDefaultSorterField());
        assertEquals(TableConfig.Sorter.getDefaultSorterField(), TableConfig.Sorter.getSorterOptionToField(R.id.sort_title));
    }
}