package com.se.npe.androidnote.util;

import android.os.AsyncTask;
import android.util.Log;

public abstract class MyAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    static int idCounter = 0;
    int id = ++idCounter;

    public MyAsyncTask() {
        Log.e("my", "MyAsyncTask constructor " + id);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.e("my", "MyAsyncTask finalize " + id + " " + toString());
    }
}
