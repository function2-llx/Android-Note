package com.se.npe.androidnote;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class ExampleInstrumentedTest {
    @Test
    public final void useAppContext() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Assert.assertEquals("com.se.npe.androidnote", appContext.getPackageName());
    }
}