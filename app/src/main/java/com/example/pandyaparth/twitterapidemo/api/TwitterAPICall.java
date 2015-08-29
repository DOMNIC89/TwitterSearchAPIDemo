package com.example.pandyaparth.twitterapidemo.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pandyaparth.twitterapidemo.listeners.APIResponseListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pandya Parth on 29-08-2015.
 */
public class TwitterAPICall {

    private static final String TAG = TwitterAPICall.class.getSimpleName();

    public static void authenticateUser(Context context, final String url, final String consumerKey,
                                        final String secretKey, final APIResponseListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String encodeConsumer = null;
                String encodeSecret = null;
                try {
                    encodeConsumer = URLEncoder.encode(consumerKey, "UTF-8");
                    encodeSecret = URLEncoder.encode(secretKey, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String consumerKeySecretKey = encodeConsumer + ":" +encodeSecret;
                String base64Encoded = Base64.encodeToString(consumerKeySecretKey.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", "Basic "+base64Encoded);
                params.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return super.getParams();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String body = "grant_type=client_credentials";
                return body.getBytes();
            }
        };

        queue.add(request);
    }

    public static void twitterSearchAPI(Context context, final String url, final APIResponseListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        queue.add(request);
    }

    public static void searchUsers(Context context, final String url, final String accessToken, final APIResponseListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "user response"+response);
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer "+accessToken);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        queue.add(request);
    }
}
