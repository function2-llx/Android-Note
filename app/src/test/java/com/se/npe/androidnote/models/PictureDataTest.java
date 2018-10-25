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
    public void getPicturePath() {
        assertEquals(DataExample.getExamplePicturePath(DataExample.EXAMPLE_MIX_IN), pictureData.getPicturePath());
    }

    @Test
    public void equalsTest() {
        assertTrue(pictureDataEquals.equals(pictureData));
        assertEquals(pictureData.getPicturePath(), pictureDataEquals.getPicturePath());
        assertFalse(pictureDataNotEquals.equals(pictureData));
        assertNotEquals(pictureData.getPicturePath(), pictureDataNotEquals.getPicturePath());
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