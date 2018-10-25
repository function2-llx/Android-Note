package com.se.npe.androidnote.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class VideoDataTest {

    private VideoData videoData;
    private VideoData videoDataEquals;
    private VideoData videoDataNotEquals;

    @Before
    public void setUp() {
        videoData = DataExample.getExampleVideoData(DataExample.EXAMPLE_MIX_IN);
        videoDataEquals = DataExample.getExampleVideoData(DataExample.EXAMPLE_MIX_IN);
        videoDataNotEquals = DataExample.getExampleVideoData(DataExample.EXAMPLE_MIX_IN + DataExample.EXAMPLE_MIX_IN);
    }

    @Test
    public void getVideoPath() {
        assertEquals(DataExample.getExampleVideoPath(DataExample.EXAMPLE_MIX_IN), videoData.getVideoPath());
    }

    @Test
    public void equalsTest() {
        // Equals VideoData are same
        assertTrue(videoDataEquals.equals(videoData));
        assertEquals(videoData.getVideoPath(), videoDataEquals.getVideoPath());
        // Not equals VideoData are different
        assertFalse(videoDataNotEquals.equals(videoData));
        assertNotEquals(videoData.getVideoPath(), videoDataNotEquals.getVideoPath());
    }

    @Test
    public void hashCodeTest() {
        assertEquals(videoData.hashCode(), videoDataEquals.hashCode());
        assertNotEquals(videoData.hashCode(), videoDataNotEquals.hashCode());
    }

    @Test
    public void toStringTest() {
        assertEquals(videoData.toString(), videoDataEquals.toString());
        assertNotEquals(videoData.toString(), videoDataNotEquals.toString());
    }
}