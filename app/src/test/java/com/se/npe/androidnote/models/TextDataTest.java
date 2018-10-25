package com.se.npe.androidnote.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class TextDataTest {

    private TextData textData;
    private TextData textDataEquals;
    private TextData textDataNotEquals;

    @Before
    public void setUp() {
        textData = DataExample.getExampleTextData(DataExample.EXAMPLE_MIX_IN);
        textDataEquals = DataExample.getExampleTextData(DataExample.EXAMPLE_MIX_IN);
        textDataNotEquals = DataExample.getExampleTextData(DataExample.EXAMPLE_MIX_IN + DataExample.EXAMPLE_MIX_IN);
    }

    @Test
    public void getText() {
        assertEquals(DataExample.getExampleText(DataExample.EXAMPLE_MIX_IN), textData.getText());
    }

    @Test
    public void getType() {
        assertEquals("Text", textData.getType());
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
}