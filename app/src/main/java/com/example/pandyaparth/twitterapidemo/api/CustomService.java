package com.example.pandyaparth.twitterapidemo.api;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Pandya Parth on 29-08-2015.
 */
public interface CustomService {

    @GET("/1.1/users/search.json")
    void show(@Query("q")String searchText, Callback<List<User>>cb);
}
