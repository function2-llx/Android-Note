package com.se.npe.androidnote.sound;

import com.se.npe.androidnote.util.ReturnValueEater;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
public class ResultPoolTest {

    @Test
    public void putResult() {
        ResultPool instance = ResultPool.getInstance();
        instance.putResult(100, "hello");
        instance.putResult(200, "world");
        try {
            instance.putResult(150, "error");
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance.clearAll();
    }

    @Test
    public void getStartTime() {
        ReturnValueEater.eat(ResultPool.getInstance().getStartTime());
    }

    @Test
    public void getCurrentPath() {
        ReturnValueEater.eat(ResultPool.getInstance().getCurrentPath());
    }

    @Test
    public void normalUsage() throws InterruptedException {
        ResultPool instance = ResultPool.getInstance();
        try {
            instance.startRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.sleep(ResultPool.SLEEP_MILL + 100);
        try {
            instance.generateWav(System.currentTimeMillis() - 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance.stopRecording();
        ReturnValueEater.eat(instance.resultFrom(0));
    }

    @Test
    public void clearAll() {
        ResultPool.getInstance().clearAll();
    }
}