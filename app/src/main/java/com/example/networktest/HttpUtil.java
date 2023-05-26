package com.example.networktest;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Callback;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {

    public static String sendHttpURLConnectionRequest(final String address){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            Log.e("HttpUtil", e.getMessage(), e);
            return e.getMessage();
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }

    /**
     * 另外需要注意的是，onFinish()方法和 onError()方法最终还是在子线程中运行的，
     * 因此 我们不可以在这里执行任何的 UI 操作，如果需要根据返回的结果来更新 UI，则仍然要使用runOnUiThread。
     * @param address
     * @param listener
     */
    public static void asyncSendHttpURLConnectionRequest(final String address, final HttpCallbackListener listener){
        new Thread(() ->{
            HttpURLConnection connection = null;
            try {
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                if (listener != null){
                    // 回调onFinish()方法
                    listener.onFinish(response.toString());
                }
            } catch (Exception e) {
                Log.e("HttpUtil", e.getMessage(), e);
                if (listener != null){
                    // 回调onError()方法
                    listener.onError(e);
                }
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }
        }).start();
    }


    public static void asyncSendOkHttpRequest(final String address, final Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }


}
