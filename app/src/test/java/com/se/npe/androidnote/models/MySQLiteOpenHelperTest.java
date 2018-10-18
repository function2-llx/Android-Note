package com.se.npe.androidnote.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class MySQLiteOpenHelperTest {

    Context context;

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        context = activity.getApplicationContext();
    }

    @After
    public void tearDown() {
        SingletonResetter.resetMySQLiteOpenHelperSingleton();
    }

    @Test
    public void getInstance() {
        // Check MySQLiteOpenHelper is properly set up
        MySQLiteOpenHelper mySQLiteOpenHelper = MySQLiteOpenHelper.getInstance(context);
        assertNotNull(mySQLiteOpenHelper);
        // Check singleton pattern is used
        MySQLiteOpenHelper mySQLiteOpenHelper2 = MySQLiteOpenHelper.getInstance(context);
        assertSame(mySQLiteOpenHelper, mySQLiteOpenHelper2);
        assertSame(mySQLiteOpenHelper.getReadableDatabase(), mySQLiteOpenHelper.getReadableDatabase());
        assertSame(mySQLiteOpenHelper.getWritableDatabase(), mySQLiteOpenHelper2.getWritableDatabase());
    }

    @Test
    public void onCreate() {
        MySQLiteOpenHelper mySQLiteOpenHelper = MySQLiteOpenHelper.getInstance(context);
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        assertNotNull(database);
    }

    @Test
    public void onUpgrade() {
        MySQLiteOpenHelper mySQLiteOpenHelper = MySQLiteOpenHelper.getInstance(context);
        assertEquals(mySQLiteOpenHelper.getWritableDatabase().getVersion(), 1);
        // Get access to private constructor of MySQLiteOpenHelper
        MySQLiteOpenHelper mySQLiteOpenHelperUpgraded;
        final int DATABASE_UPGRADED_VERSION = 2;
        try {
            Class mySQLiteOpenHelperClass = MySQLiteOpenHelper.class;
            Constructor mySQLiteOpenHelperConstructor = mySQLiteOpenHelperClass.getDeclaredConstructor(Context.class, String.class, SQLiteDatabase.CursorFactory.class, int.class);
            mySQLiteOpenHelperConstructor.setAccessible(true);
            // Depends on the private constructor of MySQLiteOpenHelper
            mySQLiteOpenHelperUpgraded = (MySQLiteOpenHelper) mySQLiteOpenHelperConstructor.newInstance(context, "Notes", null, DATABASE_UPGRADED_VERSION);
        } catch (Exception e) {
            throw new RuntimeException("Reflection on MySQLiteOpenHelper in MySQLiteOpenHelperTest failed.");
        }
        // Check database version is upgraded
        assertNotNull(mySQLiteOpenHelperUpgraded);
        assertEquals(mySQLiteOpenHelperUpgraded.getWritableDatabase().getVersion(), DATABASE_UPGRADED_VERSION);
        assertEquals(mySQLiteOpenHelperUpgraded.getWritableDatabase().getVersion(), DATABASE_UPGRADED_VERSION);
    }
}