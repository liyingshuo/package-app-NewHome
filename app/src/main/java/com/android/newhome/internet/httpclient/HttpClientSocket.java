package com.android.newhome.internet.httpclient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.newhome.application.Application;
import com.android.newhome.internet.typedefined.TypeDefined;
import com.android.newhome.internet.urlconstructor.UrlConstructor;

import org.json.JSONArray;
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
                        receiveMessage(httpURLConnection);
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

    public void receiveMessage(HttpURLConnection httpURLConnection) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                try {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    //httpURLConnection接收数据
                    String message = "";
                    String str = "";
                    JSONObject jsonObject = new JSONObject();
                    while((str = bufferedReader.readLine()) != null){

                        String[] key_value = str.split("=");
                        String key = key_value[0];
                        String value = key_value[1];

                        //判断是否是数据头
                        if(key.equals("type") && jsonObject.has("type")){
                            message = message + "\n";
                            //将数据添加到全局JsonArray中
                            JSONArray jsonArray = judjeJsonArrayType(value);
                            jsonArray.put(jsonObject);
                            //为之后的数据新建JSONObject
                            jsonObject = new JSONObject();
                        }

                        message = message + str + "\n";
                        jsonObject.put(key,value);
                    }
                    //接受的最后一组数据也要保存，否则数据会丢失
                    JSONArray jsonArray = judjeJsonArrayType(jsonObject.getString("type"));
                    jsonArray.put(jsonObject);


                    msg.what = 3; //消息接收成功
                    msg.obj = message;
                    handler.sendMessage(msg);

                    //拿到了返回的数据之后记得关闭I/OputStream以及创建的连接HttpURLConnection
                    inputStream.close();
                    //发送并接收数据成功则退出函数，不重新执行runable
                    return;

                } catch (Exception e) {
                    Log.e("HttpClientSocket", "receiveMessage: bufferedReader.readLine failed");
                    e.printStackTrace();
                }
                msg.what = 2; //消息接收失败
                handler.sendMessage(msg);

                //每隔100ms后重新调用receive服务发送数据
                //若请求已经发送并成功响应一次，这里再次接收消息会失败
                //如有需要，应该重新发送请求，然后重新接收响应
                //那假如第一次请求响应失败，该如何处理呢
                //这里还没有遇到这个情况，服务器运行正常的情况下，响应都成功了
                //handler.postDelayed(this::run, 100);

            }
        });
        thread.start();
    }

    private JSONArray judjeJsonArrayType(String string){
        JSONArray jsonArray = null;

        if (string.equals(Integer.toString(TypeDefined.TYPE_REQUEST_HOME))){
            jsonArray = Application.g_homeinfolist;
        } else if (string.equals(Integer.toString(TypeDefined.TYPE_REQUEST_MAP))) {
        } else if (string.equals(Integer.toString(TypeDefined.TYPE_REQUEST_ADD))) {
        } else if (string.equals(Integer.toString(TypeDefined.TYPE_REQUEST_MESSAGE))) {
        } else if (string.equals(Integer.toString(TypeDefined.TYPE_REQUEST_MYSELF))) {
        } else {
        }

        return jsonArray;
    }
}
