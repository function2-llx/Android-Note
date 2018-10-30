package com.se.npe.androidnote.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class PictureDataTest {

    private PictureData pictureData;
    private PictureData pictureDataEquals;
    private PictureData pictureDataNotEquals;

    @Before
    public void setUp() {
        pictureData = DataExample.getExamplePictureData(DataExample.EXAMPLE_MIX_IN);
        pictureDataEquals = DataExample.getExamplePictureData(DataExample.EXAMPLE_MIX_IN);
        pictureDataNotEquals = DataExample.getExamplePictureData(DataExample.EXAMPLE_MIX_IN + DataExample.EXAMPLE_MIX_IN);
    }

    @Test
    public void getPath() {
        assertEquals(DataExample.getExamplePicturePath(DataExample.EXAMPLE_MIX_IN), pictureData.getPath());
    }

    @Test
    public void getType() {
        assertEquals("Pic", pictureData.getType());
    }

    @Test
    public void equalsTest() {
        assertTrue(pictureDataEquals.equals(pictureData));
        assertEquals(pictureData.getPath(), pictureDataEquals.getPath());
        assertFalse(pictureDataNotEquals.equals(pictureData));
        assertNotEquals(pictureData.getPath(), pictureDataNotEquals.getPath());
    }

    @Test
    public void hashCodeTest() {
        assertEquals(pictureData.hashCode(), pictureDataEquals.hashCode());
        assertNotEquals(pictureData.hashCode(), pictureDataNotEquals.hashCode());
    }

    @Test
    public void toStringTest() {
        assertEquals(pictureData.toString(), pictureDataEquals.toString());
        assertNotEquals(pictureData.toString(), pictureDataNotEquals.toString());
    }
}