package com.android.newhome.internet.httpclient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.newhome.internet.urlconstructor.UrlConstructor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HttpClientSocket {
    Handler handler;



    public HttpClientSocket(Handler handler) {
        this.handler = handler;
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(int type, String message) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                URL url = UrlConstructor.getURL(type, message);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setReadTimeout(8000);
                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", "liyingshuo");
                    jsonObject.put("password", "123456");
                    String data = "dataString=" + URLEncoder.encode(jsonObject.toString(), "UTF-8");

                    //httpURLConnection发送数据
                    outputStream.write(data.getBytes());
                    msg.what = 1; //消息发送成功
                    msg.obj = message;
                    handler.sendMessage(msg);

                    //httpURLConnection接收服务器数据
                    if (httpURLConnection.getResponseCode() == 200){
                        receiveMessage(type, httpURLConnection);
                    }

                    //拿到了返回的数据之后记得关闭I/OputStream以及创建的连接HttpURLConnection
                    outputStream.close();
                    httpURLConnection.disconnect();
                    //发送并接收数据成功则退出函数，不重新执行runable
                    return;

                } catch (JSONException e) {
                    Log.e("HttpClientSocket", "sendMessage: generate JSONObject failed");
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    Log.e("HttpClientSocket", "sendMessage: generate dataString failed");
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("HttpClientSocket", "sendMessage: generate HttpURLConnection failed");
                    Log.e("HttpClientSocket", "sendMessage: generate OutputStream failed");
                    e.printStackTrace();
                }
                msg.what = 0; //消息发送失败
                handler.sendMessage(msg);
                //每隔100ms后重新调用send服务发送数据
                handler.postDelayed(this::run, 100);
            }
        });
        thread.start();
    }

    public void receiveMessage(int type, HttpURLConnection httpURLConnection) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                try {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    //httpURLConnection接收数据
                    String message = bufferedReader.readLine();
                    msg.what = 3; //消息接收成功
                    msg.obj = message;
                    handler.sendMessage(msg);

                    //拿到了返回的数据之后记得关闭I/OputStream以及创建的连接HttpURLConnection
                    inputStream.close();
                    //发送并接收数据成功则退出函数，不重新执行runable
                    return;

                } catch (IOException e) {
                    Log.e("HttpClientSocket", "sendMessage: generate JSONObject failed");
                    e.printStackTrace();
                }
                msg.what = 2; //消息接收失败
                handler.sendMessage(msg);
                //每隔100ms后重新调用receive服务发送数据
                handler.postDelayed(this::run, 100);
            }
        });
        thread.start();
    }
}
