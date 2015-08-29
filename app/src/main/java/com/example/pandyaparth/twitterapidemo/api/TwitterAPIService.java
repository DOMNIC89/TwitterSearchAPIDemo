package com.example.pandyaparth.twitterapidemo.api;

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;

/**
 * Created by Pandya Parth on 29-08-2015.
 */
public class TwitterAPIService extends TwitterApiClient {

    public TwitterAPIService(Session session) {
        super(session);
    }

    public CustomService getCustomService(){
        return getService(CustomService.class);
    }
}
