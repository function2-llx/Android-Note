package com.se.npe.androidnote.models;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DBManagerTest {

    Context context;

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.setupActivity(AppCompatActivity.class);
        context = activity.getApplicationContext();
    }

    @After
    public void tearDown() {
        SingletonResetter.resetDBManagerSingleton();
    }

    @Test
    public void newInstances() {
        // Check DBManager is properly set up
        DBManager dbManager = DBManager.newInstances(context);
        assertNotNull(dbManager);
        // Check singleton pattern is used
        DBManager dbManager2 = DBManager.newInstances(context);
        assertSame(dbManager, dbManager2);
    }

    @Test
    public void getDataBase() {
        DBManager dbManager = DBManager.newInstances(context);
        assertNotNull(dbManager.getDataBase());
        // Check database is open
        assertTrue(dbManager.getDataBase().isOpen());
        // Check database is writable
        assertFalse(dbManager.getDataBase().isReadOnly());
    }
}