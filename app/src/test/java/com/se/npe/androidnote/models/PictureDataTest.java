package com.se.npe.androidnote.models;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class PictureDataTest {

    private static final String EXAMPLE_MIX_IN = "test";
    private static final Bitmap exampleBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
    private PictureData pictureData;
    private PictureData pictureDataEquals;
    private PictureData pictureDataNotEquals;

    @Before
    public void setUp() {
        pictureData = getExamplePictureData(EXAMPLE_MIX_IN);
        pictureDataEquals = getExamplePictureData(EXAMPLE_MIX_IN);
        pictureDataNotEquals = getExamplePictureData(EXAMPLE_MIX_IN + EXAMPLE_MIX_IN);
    }

    @Test
    public void getPicturePath() {
        assertEquals(getExamplePicturePath(EXAMPLE_MIX_IN), pictureData.getPicturePath());
    }

    @Test
    public void getPicture() {
        assertTrue(pictureData.getPicture().sameAs(getExamplePicture()));
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

    @NonNull
    private PictureData getExamplePictureData(String mixIn) {
        return new PictureData(getExamplePicturePath(mixIn), getExamplePicture());
    }

    @NonNull
    private String getExamplePicturePath(String mixIn) {
        return "This is the PictureData picture path for " + mixIn;
    }

    @NonNull
    private Bitmap getExamplePicture() {
        // TODO: Add mixIn for Bitmap
        return exampleBitmap;
    }
}