package com.android.newhome.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.newhome.R;
import com.android.newhome.application.Application;
import com.android.newhome.internet.httpclient.HttpClientSocket;
import com.android.newhome.internet.typedefined.TypeDefined;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class HomeFragment extends Fragment {

    private View root;
    private TextView textView;
    private HomeViewModel homeViewModel;
    private Handler handler;
    private HttpClientSocket httpClientSocket;
    private int lastMsgWhat = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        textView = root.findViewById(R.id.text_home);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //初始化TcpClientSocket
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(lastMsgWhat == msg.what) return;
                lastMsgWhat = msg.what;
                String string = "";
                switch (msg.what){
                    case 0: //发送数据失败
                        textView.setText("tcpClientSocket发送数据失败");
                        break;
                    case 1: //发送数据成功
                        textView.setText(msg.obj.toString());
                        break;
                    case 2: //接收数据失败
                        //接收数据失败有两种情况
                        //1.第一次发送url请求，未收到响应，这种情况应该重新持续接收请求
                        //2.第一次发送url请求后成功接收响应，再次请求会失败
                        //这样的话，只需要显示已经接收到的数据就好
                        string = showJsonArrayInfoList(Application.g_homeinfolist);
                        if(string.equals("")){
                            string = "tcpClientSocket接收数据失败";
                            sendHomeInfoRequest();
                        }
                        textView.setText(string);
                        break;
                    case 3: //接收数据成功
                        string = "";
                        string = showJsonArrayInfoList(Application.g_homeinfolist);
                        textView.setText(string);
                        break;
                    default:
                        break;
                }
            }
        };

        httpClientSocket = new HttpClientSocket(handler);

        sendHomeInfoRequest();

        return root;
    }

    private void sendHomeInfoRequest() {
        //向服务器请求数据
        int type = TypeDefined.TYPE_REQUEST_HOME;
        String message = "TYPE_REQUEST_HOME";
        httpClientSocket.sendMessage(type, message);
    }

    private String showJsonArrayInfoList(JSONArray jsonArray){
        String string = "";
        for (int i = 0; i < Application.g_homeinfolist.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                string = string + jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return string;
    }
}