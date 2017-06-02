package com.example.basiam.kittens;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class KittenLoader extends AsyncTaskLoader<List<Kitten>> {
    private static final String LOG_TAG = KittenLoader.class.getSimpleName();
    private String mUrl;


    public KittenLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Kitten> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Extract relevant fields from the JSON response and create an {@link Kitten} object
        Log.d(LOG_TAG, mUrl);
        List<Kitten> Kittens = QueryUtils.extractKittens(mUrl);

        // Return the {@link Kitten} object as the result fo the {@link KittenAsyncTask}
        return Kittens;
    }
}