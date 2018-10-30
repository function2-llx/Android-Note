package com.se.npe.androidnote.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class SoundDataTest {

    private SoundData soundData;
    private SoundData soundDataEquals;
    private SoundData soundDataNotEquals;

    @Before
    public void setUp() {
        soundData = DataExample.getExampleSoundData(DataExample.EXAMPLE_MIX_IN);
        soundDataEquals = DataExample.getExampleSoundData(DataExample.EXAMPLE_MIX_IN);
        soundDataNotEquals = DataExample.getExampleSoundData(DataExample.EXAMPLE_MIX_IN + DataExample.EXAMPLE_MIX_IN);
    }

    @Test
    public void getType() {
        assertEquals("Sound", soundDataEquals.getType());
    }

    @Test
    public void getPath() {
        assertEquals(DataExample.getExampleSoundPath(DataExample.EXAMPLE_MIX_IN), soundData.getPath());
    }

    @Test
    public void getText() {
        assertEquals(DataExample.getExampleSoundText(DataExample.EXAMPLE_MIX_IN), soundData.getText());
    }

    @Test
    public void equalsTest() {
        // Equals SoundData are same
        assertTrue(soundDataEquals.equals(soundData));
        assertEquals(soundData.getPath(), soundDataEquals.getPath());
        assertEquals(soundData.getText(), soundDataEquals.getText());
        // Not equals SoundData are different
        assertFalse(soundDataNotEquals.equals(soundData));
        assertFalse(soundDataNotEquals.getPath().equals(soundData.getPath())
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
}