package com.example.pandyaparth.twitterapidemo.listeners;

import com.android.volley.VolleyError;

/**
 * Created by Pandya Parth on 29-08-2015.
 */
public interface APIResponseListener {

    void onResponse(Object response);

    void onError(VolleyError error);

}
