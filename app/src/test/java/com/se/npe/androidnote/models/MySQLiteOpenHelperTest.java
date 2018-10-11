package com.se.npe.androidnote.models;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class MySQLiteOpenHelperTest {

    AppCompatActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(AppCompatActivity.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        // Check MySQLiteOpenHelper is properly set up
        MySQLiteOpenHelper mySQLiteOpenHelper = MySQLiteOpenHelper.getInstance(activity.getApplicationContext());
        assertNotNull(mySQLiteOpenHelper);
        // Check singleton pattern is used
        MySQLiteOpenHelper mySQLiteOpenHelper2 = MySQLiteOpenHelper.getInstance(activity.getApplicationContext());
        assertSame(mySQLiteOpenHelper, mySQLiteOpenHelper2);
    }

    @Test
    public void onCreate() {
        MySQLiteOpenHelper mySQLiteOpenHelper = MySQLiteOpenHelper.getInstance(activity.getApplicationContext());
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        assertNotNull(database);
    }

    @Test
    public void onUpgrade() {
    }
}