package com.se.npe.androidnote.sound;

import com.se.npe.androidnote.util.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResultPoolTest {
    private static final String LOG_TAG = ResultPoolTest.class.getSimpleName();
    private ResultPool instance;

    @Before
    public void setUp() throws Exception {
        instance = ResultPool.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        instance.clearAll();
        instance = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void putResultError() {
        try {
            instance.putResult(System.currentTimeMillis(), "");
            instance.putResult(-1, "");
        } finally {
            instance.clearAll();
        }
    }

    @Test
    public void putResult() {
        instance.putResult(System.currentTimeMillis(), "1");
        instance.putResult(System.currentTimeMillis(), "2");
        instance.putResult(System.currentTimeMillis(), "3");
        assertEquals(instance.resultFrom(10), "123");
        instance.clearAll();
        instance.putResult(System.currentTimeMillis(), "1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Logger.log(LOG_TAG, e);
        }
        instance.putResult(System.currentTimeMillis(), "2");
        assertEquals(instance.resultFrom(990), "2");
        instance.clearAll();
    }

    @Test
    public void resultFrom() {
    }
}