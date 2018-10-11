package com.se.npe.androidnote.models;

import android.support.v7.app.AppCompatActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class DBManagerTest {

    AppCompatActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(AppCompatActivity.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void newInstances() {
        // Check DBManager is properly set up
        DBManager dbManager = DBManager.newInstances(activity.getApplicationContext());
        assertNotNull(dbManager);
        // Check singleton pattern is used
        DBManager dbManager2 = DBManager.newInstances(activity.getApplicationContext());
        assertSame(dbManager, dbManager2);
    }

    @Test
    public void getDataBase() {
        DBManager dbManager = DBManager.newInstances(activity.getApplicationContext());
        assertNotNull(dbManager.getDataBase());
        // Check database is open
        assertTrue(dbManager.getDataBase().isOpen());
        // Check database is writable
        assertFalse(dbManager.getDataBase().isReadOnly());
    }
}