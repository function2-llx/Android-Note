package com.se.npe.androidnote.models;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class PictureDataTest {

    private final static String exampleMixIn = "test";
    private final static Bitmap exampleBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
    private PictureData pictureData;
    private PictureData pictureDataEquals;
    private PictureData pictureDataNotEquals;

    @Before
    public void setUp() throws Exception {
        pictureData = getExamplePictureData(exampleMixIn);
        pictureDataEquals = getExamplePictureData(exampleMixIn);
        pictureDataNotEquals = getExamplePictureData(exampleMixIn + exampleMixIn);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getPicturePath() {
        assertEquals(getExamplePicturePath(exampleMixIn), pictureData.getPicturePath());
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