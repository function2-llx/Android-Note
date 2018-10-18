package com.se.npe.androidnote.sound;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Store the result of asr, sorted by time.
 * Provide interfaces to retrieve the results within a range of time.
 *
 * @author MashPlant
 */

public class ResultPool {
    private static ResultPool instance = new ResultPool();
    private ArrayList<Long> times = new ArrayList<>();
    private ArrayList<String> results = new ArrayList<>();

    public static ResultPool getInstance() {
        return instance;
    }

    public void clearAll() {
        times.clear();
        results.clear();
    }

    public void putResult(long time, String result) {
        if (!times.isEmpty()) {
            long lastTime = times.get(times.size() - 1);
            if (lastTime > time) {
                throw new IllegalArgumentException("time must >= lastTime");
            }
        }
        times.add(time);
        results.add(result);
        Log.e("putResultTag", "time : " + time + ", result : " + result);
    }

    public String resultFrom(long offset) {
        StringBuilder sb = new StringBuilder();
        int index = Collections.binarySearch(times, System.currentTimeMillis() - offset);
        if (index < 0) {
            index = -index - 1;
            if (index > 0)
                index--;
        }
        for (; index < results.size(); ++index) {
            sb.append(results.get(index));
        }
        return sb.toString();
    }

    public String resultRange(long begin, long end) {
        return null;
    }
}
