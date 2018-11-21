package com.se.npe.androidnote.sound;

import com.iflytek.cloud.RecognizerResult;
import com.se.npe.androidnote.util.ReturnValueEater;
import com.se.npe.androidnote.util.ThreadSleep;

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
    public void normalUsage() {
        ResultPool instance = ResultPool.getInstance();
        try {
            instance.startRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ThreadSleep.sleep(ResultPool.SLEEP_MILL + 100);
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

    @Test
    public void testIFlyFeeder() {
        try {
            ResultPool.IFlyFeeder feeder = new ResultPool.IFlyFeeder(ResultPool.getInstance());
            feeder.execute();
            feeder.onCancelled();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            ResultPool.IFlyFeeder feeder = new ResultPool.IFlyFeeder(ResultPool.getInstance());
            feeder.onCancelled();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRecognizerListener() {
        try {
            ResultPool.MyRecognizerListener listener = new ResultPool.MyRecognizerListener(
                    ResultPool.getInstance(), System.currentTimeMillis());
            listener.onVolumeChanged(0, null);
            listener.onBeginOfSpeech();
            listener.onEndOfSpeech();
            listener.onError(null);
            listener.onEvent(0, 0, 0, null);
            listener.onResult(new RecognizerResult("hello"), false);
            listener.onResult(new RecognizerResult("。"), false);
            listener.onResult(new RecognizerResult(""), false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}