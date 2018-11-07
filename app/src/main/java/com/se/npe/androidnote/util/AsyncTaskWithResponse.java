package com.se.npe.androidnote.util;

import android.os.AsyncTask;

public abstract class AsyncTaskWithResponse<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public interface AsyncResponse<Result> {
        void processFinish(Result result);
    }

    private AsyncResponse<Result> delegate;

    public AsyncTaskWithResponse(AsyncResponse<Result> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(Result result) {
        delegate.processFinish(result);
    }
}
