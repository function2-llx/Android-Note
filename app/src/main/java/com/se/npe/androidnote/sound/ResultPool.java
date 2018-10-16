package com.se.npe.androidnote.sound;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Store the result of asr, sorted by time.
 * Provide interfaces to retrieve the results within a range of time.
 *
 * @author MashPlant
 */

public class ResultPool {
    private ArrayList<Long> times = new ArrayList<>();
    private ArrayList<String> results = new ArrayList<>();

    public void putResult(long time, String result) {
        times.add(time);
        results.add(result);
    }

    String resultFrom(long offset) {
        StringBuilder sb = new StringBuilder();
        int index = Collections.binarySearch(times, System.currentTimeMillis() - offset);
        if (index < 0) {
            index = -index - 1;
        }
        for (; index < results.size(); ++index) {
            sb.append(results.get(index));
        }
        return sb.toString();
    }

    String resultRange(long begin, long end) {
        return null;
    }
}
