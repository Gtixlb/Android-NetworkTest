package com.example.networktest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.responseText);

        // 发送RequestWithHttpURLConnection请求
        Button sendRequestBtn = findViewById(R.id.sendRequestBtn);
        sendRequestBtn.setOnClickListener(v -> {
            new Thread(() -> {
                sendRequestWithHttpURLConnection();
            }).start();
        });

        // 发送OkHttp请求
        Button sendOkHttpRequest = findViewById(R.id.sendOkHttpRequestBtn);
        sendOkHttpRequest.setOnClickListener(v -> {
            new Thread(() -> {
                sendRequestWithOkHttp();
            }).start();
        });

        // JSON解析响应数据
        Button parseJsonDataBtn = findViewById(R.id.parseJsonDataBtn);
        parseJsonDataBtn.setOnClickListener(v -> {
            new Thread(() -> {
                sendJSONTest();
            }).start();
        });

        // 异步回调HttpURLConnect
        Button asyncHttpURLConnectBtn = findViewById(R.id.asyncHttpURLConnect);
        asyncHttpURLConnectBtn.setOnClickListener(v -> {
            HttpUtil.asyncSendHttpURLConnectionRequest("https://www.baidu.com", new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    runOnUiThread(() -> textView.setText(response));
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> textView.setText(e.getMessage()));
                }
            });
        });

        // 异步回调OkHttp
        Button asyncOkHttpBtn = findViewById(R.id.asyncOkHttp);
        asyncOkHttpBtn.setOnClickListener(v -> {
            HttpUtil.asyncSendOkHttpRequest("https://iottest.leelen.net/third/an/opendoor", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("MainActivity", e.getMessage(), e);
                    runOnUiThread(()-> textView.setText(e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        try {
                            textView.setText(response.body().string());
                        } catch (IOException e) {
                            Log.e("MainActivity", e.getMessage(), e);
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        });

    }

    private void sendRequestWithOkHttp() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://www.hao123.com")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String bodyStr = response.body().string();
            showResponse(bodyStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendJSONTest() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://iottest.leelen.net/third/an/opendoor")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String bodyStr = response.body().string();
            showJSONResponse(bodyStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRequestWithHttpURLConnection() {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("https://www.baidu.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            // 显示框显示响应结果
            showResponse(response.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showResponse(String response) {
        runOnUiThread(() -> {
            textView.setText(response);
        });
    }

    private void showJSONResponse(String response) {
        runOnUiThread(() -> {
            textView.setText(JSONObject.parse(response).toString());
        });
    }
}