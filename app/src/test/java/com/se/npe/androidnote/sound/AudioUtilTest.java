package com.se.npe.androidnote.sound;

import com.se.npe.androidnote.util.ReturnValueEater;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
public class AudioUtilTest {
    private static final String PCM_PATH = "tmp.pcm";
    private static final String WAV_PATH = "tmp.wav";

    @Before
    public void setUp() {
        File f = new File(PCM_PATH);
        try {
            ReturnValueEater.eat(f.createNewFile());
        } catch (IOException e) {
            // no-op
        }
        try (FileOutputStream out = new FileOutputStream(f)) {
            byte[] b = new byte[100000];
            out.write(b, 0, b.length);
        } catch (IOException e) {
            // no-op
        }
    }

    @Test
    public void testPcmToWav() {
        try {
            AudioUtil.pcmToWav(PCM_PATH, WAV_PATH, 0, 1000);
        } catch (IOException e) {
            // no-op
        }
    }

    @After
    public void tearDown() {
        File f = new File(PCM_PATH);
        ReturnValueEater.eat(f.delete());
        f = new File(WAV_PATH);
        ReturnValueEater.eat(f.delete());
    }
}