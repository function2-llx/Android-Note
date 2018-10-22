package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class SoundDataTest {

    private static final String EXAMPLE_MIX_IN = "test";
    private SoundData soundData;
    private SoundData soundDataEquals;
    private SoundData soundDataNotEquals;

    @Before
    public void setUp() {
        soundData = getExampleSoundData(EXAMPLE_MIX_IN);
        soundDataEquals = getExampleSoundData(EXAMPLE_MIX_IN);
        soundDataNotEquals = getExampleSoundData(EXAMPLE_MIX_IN + EXAMPLE_MIX_IN);
    }

    @Test
    public void getSoundPath() {
        assertEquals(getExampleSoundPath(EXAMPLE_MIX_IN), soundData.getSoundPath());
    }

    @Test
    public void getText() {
        assertEquals(getExampleSoundText(EXAMPLE_MIX_IN), soundData.getText());
    }

    @Test
    public void equalsTest() {
        // Equals SoundData are same
        assertTrue(soundDataEquals.equals(soundData));
        assertEquals(soundData.getSoundPath(), soundDataEquals.getSoundPath());
        assertEquals(soundData.getText(), soundDataEquals.getText());
        // Not equals SoundData are different
        assertFalse(soundDataNotEquals.equals(soundData));
        assertFalse(soundDataNotEquals.getSoundPath().equals(soundData.getSoundPath())
                && soundDataNotEquals.getText().equals(soundData.getText()));
    }

    @Test
    public void hashCodeTest() {
        assertEquals(soundDataEquals.hashCode(), soundData.hashCode());
        assertNotEquals(soundDataNotEquals.hashCode(), soundData.hashCode());
    }

    @Test
    public void toStringTest() {
        assertEquals(soundDataEquals.toString(), soundData.toString());
        assertNotEquals(soundDataNotEquals.toString(), soundData.toString());
    }

    @NonNull
    private SoundData getExampleSoundData(String mixIn) {
        return new SoundData(getExampleSoundPath(mixIn), getExampleSoundText(mixIn));
    }

    @NonNull
    private String getExampleSoundPath(String mixIn) {
        return "This is the SoundData sound path for " + mixIn;
    }

    @NonNull
    private String getExampleSoundText(String mixIn) {
        return "This is the SoundData text for " + mixIn;
    }
}