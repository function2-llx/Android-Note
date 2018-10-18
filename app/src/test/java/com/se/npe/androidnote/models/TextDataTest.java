package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class TextDataTest {

    private static final String exampleMixIn = "test";
    private TextData textData;
    private TextData textDataEquals;
    private TextData textDataNotEquals;

    @Before
    public void setUp() {
        textData = getExampleTextData(exampleMixIn);
        textDataEquals = getExampleTextData(exampleMixIn);
        textDataNotEquals = getExampleTextData(exampleMixIn + exampleMixIn);
    }

    @Test
    public void getText() {
        assertEquals(getExampleText(exampleMixIn), textData.getText());
    }

    @Test
    public void equalsTest() {
        // Equals TextData are same
        assertTrue(textDataEquals.equals(textData));
        assertEquals(textData.getText(), textDataEquals.getText());
        // Not equals TextData are different
        assertFalse(textDataNotEquals.equals(textData));
        assertNotEquals(textData.getText(), textDataNotEquals.getText());
    }

    @Test
    public void hashCodeTest() {
        assertEquals(textData.hashCode(), textDataEquals.hashCode());
        assertNotEquals(textData.hashCode(), textDataNotEquals.hashCode());
    }

    @Test
    public void toStringTest() {
        assertEquals(textData.toString(), textDataEquals.toString());
        assertNotEquals(textData.toString(), textDataNotEquals.toString());
    }

    @NonNull
    private TextData getExampleTextData(String mixIn) {
        return new TextData(getExampleText(mixIn));
    }

    @NonNull
    private String getExampleText(String mixIn) {
        return "This is the TextData text for " + mixIn;
    }
}