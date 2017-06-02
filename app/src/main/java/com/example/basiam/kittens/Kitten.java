package com.example.basiam.kittens;


import android.net.Uri;

import java.util.Random;

public class Kitten {

    private String mUrl;

    public Kitten(String eUrl) {

        mUrl = eUrl;
    }


    public Uri getUrl() {
        return Uri.parse(mUrl);
    }

    public int getCuteness() {
        return new Random().nextInt(11);
    }

    @Override
    public String toString() {
        return mUrl;
    }
}
