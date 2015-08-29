package com.example.pandyaparth.twitterapidemo.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pandyaparth.twitterapidemo.R;
import com.example.pandyaparth.twitterapidemo.adapter.TwitterSearchListViewAdapter;
import com.example.pandyaparth.twitterapidemo.api.TwitterAPIService;
import com.example.pandyaparth.twitterapidemo.model.Authentication;
import com.example.pandyaparth.twitterapidemo.model.SearchResults;
import com.example.pandyaparth.twitterapidemo.model.Searches;
import com.example.pandyaparth.twitterapidemo.model.TwitterConstants;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "wh5N9OQt6J6CnoALsGtDjvTYp";
    private static final String TWITTER_SECRET = "ZnQsf8wkeNfqtYwhoBjy7Z5W8btWu2xnqLZoe3nrHFCF9SGHzh";

    private SearchView searchView = null;
    private TwitterSearchListViewAdapter mAdapter;
    private TwitterSession session;
    private ProgressBar mProgressbar;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("twitterAuthToken");
        if(bundle != null) {
            TwitterAuthToken authToken = bundle.getParcelable("token");
            String userName = bundle.getString("userName");
            long userId = bundle.getLong("id");
            session = new TwitterSession(authToken, userId, userName);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Twitter Search Demo");

        mListView = (ListView) findViewById(R.id.twitter_search_list_view);
        mProgressbar = (ProgressBar) findViewById(R.id.progressbar);
        mAdapter = new TwitterSearchListViewAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search_view);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        setupSearchView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(true);
        EditText searchText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(getResources().getColor(R.color.white));

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (manager != null) {
            searchView.setSearchableInfo(manager.getSearchableInfo(new ComponentName(this, MainActivity.class)));
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //new TwitterSearchTask().execute(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.e(TAG, newText);
        if (newText.length() >= 3) {
            if (newText.startsWith("@")) {
                String str = newText.split("@")[1];
                if (session!= null) {
                    mProgressbar.setVisibility(View.VISIBLE);
                    new TwitterAPIService(session).getCustomService().show(str, new Callback<List<User>>() {
                        @Override
                        public void success(Result<List<User>> result) {
                            if (result.data.size() > 0) {
                                mAdapter = new TwitterSearchListViewAdapter(MainActivity.this);
                                mAdapter.setUsers(result.data);
                                mAdapter.setType(1);
                                mProgressbar.setVisibility(View.GONE);
                                mListView.setAdapter(mAdapter);
                            }
                        }

                        @Override
                        public void failure(TwitterException e) {
                            e.getStackTrace();
                            Log.e(TAG, "" + e.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Please Login to Search User", Toast.LENGTH_SHORT).show();
                }
            } else {
                new TwitterSearchTask().execute(newText);
            }
        }
        return true;
    }

    private Searches jsonToSearches(String result) {
        Searches searches = null;
        if (result != null && result.length() > 0) {
            try {
                Gson gson = new Gson();
                // bring back the entire search object
                SearchResults sr = gson.fromJson(result, SearchResults.class);
                // but only pass the list of tweets found (called statuses)
                searches = sr.getStatuses();
            } catch (IllegalStateException ex) {
                // just eat the exception for now, but you'll need to add some handling here
            }
        }
        return searches;
    }

    private class TwitterSearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            result = getSearchStream(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "On Post Execute" + s);

            Searches searches = jsonToSearches(s);
            mAdapter = new TwitterSearchListViewAdapter(MainActivity.this);
            mAdapter.setSearches(searches);
            mAdapter.setType(0);
            mProgressbar.setVisibility(View.GONE);
            mListView.setAdapter(mAdapter);
        }

        private String getStream(String url) {
            String results = null;

            // Step 1: Encode consumer key and secret
            try {
                // URL encode the consumer key and secret
                String urlApiKey = URLEncoder.encode(TWITTER_KEY, "UTF-8");
                String urlApiSecret = URLEncoder.encode(TWITTER_SECRET, "UTF-8");

                // Concatenate the encoded consumer key, a colon character, and the encoded consumer secret
                String combined = urlApiKey + ":" + urlApiSecret;

                // Base64 encode the string
                String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

                // Step 2: Obtain a bearer token
                HttpPost httpPost = new HttpPost(TwitterConstants.TWITTER_TOKEN_URL);
                httpPost.setHeader("Authorization", "Basic " + base64Encoded);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
                String rawAuthorization = getResponseBody(httpPost);
                Authentication auth = jsonToAuthenticated(rawAuthorization);

                // Applications should verify that the value associated with the
                // token_type key of the returned object is bearer
                if (auth != null && auth.token_type.equals("bearer")) {

                    // Step 3: Authenticate API reaquests with bearer token
                    HttpGet httpGet = new HttpGet(url);

                    // construct a normal HTTPS request and include an Authorization
                    // header with the value of Bearer <>
                    httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
                    httpGet.setHeader("Content-Type", "application/json");
                    // update the results with the body of the response
                    results = getResponseBody(httpGet);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (IllegalStateException ex1) {
            }
            return results;
        }

        private String getSearchStream(String searchTerm) {
            String results = null;
            try {
                String encodedUrl = URLEncoder.encode(searchTerm, "UTF-8");
                if(searchTerm.startsWith("@")) {
                    results = getStream(TwitterConstants.TWITTER_USER_SEARCH_URL+encodedUrl);
                } else {
                    results = getStream(TwitterConstants.TWITTER_SEARCH_URL + encodedUrl);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (IllegalStateException ex1) {
            }
            return results;
        }

        private Authentication jsonToAuthenticated(String rawAuthorization) {
            Authentication auth = null;
            if (rawAuthorization != null && rawAuthorization.length() > 0) {
                try {
                    Gson gson = new Gson();
                    auth = gson.fromJson(rawAuthorization, Authentication.class);
                } catch (IllegalStateException ex) {
                    // just eat the exception for now, but you'll need to add some handling here
                }
            }
            return auth;
        }

        private String getResponseBody(HttpRequestBase request) {
            StringBuilder sb = new StringBuilder();
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String reason = response.getStatusLine().getReasonPhrase();

                if (statusCode == 200) {

                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();

                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    sb.append(reason);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (ClientProtocolException ex1) {
            } catch (IOException ex2) {
            }
            return sb.toString();
        }
    }
}
