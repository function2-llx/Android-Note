package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class VideoDataTest {

    private static final String exampleMixIn = "test";
    private VideoData videoData;
    private VideoData videoDataEquals;
    private VideoData videoDataNotEquals;

    @Before
    public void setUp() {
        videoData = getExampleVideoData(exampleMixIn);
        videoDataEquals = getExampleVideoData(exampleMixIn);
        videoDataNotEquals = getExampleVideoData(exampleMixIn + exampleMixIn);
    }

    @Test
    public void getVideoPath() {
        assertEquals(getExampleVideoPath(exampleMixIn), videoData.getVideoPath());
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

    @NonNull
    private VideoData getExampleVideoData(String mixIn) {
        return new VideoData(getExampleVideoPath(mixIn));
    }

    @NonNull
    private String getExampleVideoPath(String mixIn) {
        return "This is the VideoData video path for " + mixIn;
    }
}