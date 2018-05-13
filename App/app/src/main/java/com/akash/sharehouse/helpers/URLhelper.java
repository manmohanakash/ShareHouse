package com.akash.sharehouse.helpers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.util.Log;

import com.akash.sharehouse.Login;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;
import org.json.JSONTokener;

public class URLhelper extends AsyncTask<Void, Void, String> {



    private onFinishListener listener;
    private String action;
    private RequestBody requestBody;
    private static OkHttpClient client;



    public URLhelper(String action,
                             RequestBody requestBody,
                             onFinishListener listener) {
        if (client == null) {
            Log.i("OkHttpClient", "Initialized...");
            client = new OkHttpClient.Builder()
                    .cookieJar(new CookieJar() {
                        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();


                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url.host(), cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url.host());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    }).build();
        }

        this.action = action;
        this.requestBody = requestBody;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            Log.e("Thread", "Thread exception");
        }


        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(AppConfig.hostName)
                .port(AppConfig.port)
                .addPathSegment(action)
                .build();



        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = null;

        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            Log.e("Thread", "Thread exception");
        }

        if (result != null) {
            try {
                Log.i("TEST RESPONSE", result);
                JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
                String status = obj.getString("type");
                if (status.equalsIgnoreCase("success")) {
                    listener.onFinishSuccess(obj);
                } else {
                    if(obj.has("action") && obj.getString("action").equals("login")){
                        listener.onFinishFailed("SESSION EXPIRED");
                    }
                    else
                    listener.onFinishFailed(obj.getString("message"));
                }

            } catch (Exception e) {
                Log.e("JSON Parse", e.getMessage());
                e.printStackTrace();
                listener.onFinishFailed("Bad response from Server. Try again later");
            }
        } else
            listener.onFinishFailed("Unable to establish connection to server");
    }

    public interface onFinishListener {
        public void onFinishSuccess(JSONObject response);
        public void onFinishFailed(String msg);
    }

}
