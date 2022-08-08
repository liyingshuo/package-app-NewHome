package com.android.newhome.internet.urlconstructor;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlConstructor {

    public static URL getURL(int type, String message){
        String address = "";
        address += "http://10.221.129.198:8080/request?";
        address += "type=" + type;
//        address += "&";
//        address += "message=" + message;

        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            Log.e("UrlConstructor", "getURL: generate URL failed");
            e.printStackTrace();
        }
        return url;
    }
}
