package com.se.npe.androidnote.util;

import android.os.AsyncTask;

/**
 * @param <P> Parameters
 * @param <G> Progress
 * @param <R> Result
 */
public abstract class AsyncTaskWithResponse<P, G, R> extends AsyncTask<P, G, R> {

    public interface AsyncResponse<R> {
        void processFinish(R result);
    }

    private AsyncResponse<R> delegate;

    public AsyncTaskWithResponse(AsyncResponse<R> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(R result) {
        delegate.processFinish(result);
    }
}
