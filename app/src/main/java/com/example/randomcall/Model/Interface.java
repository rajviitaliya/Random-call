package com.example.randomcall.Model;

import android.webkit.JavascriptInterface;

import com.example.randomcall.Activity.Call;

public class Interface {
    Call callActivity;
    public  Interface (Call callActivity){this.callActivity=callActivity;}
    @JavascriptInterface
    public  void onPeerConnected(){callActivity.onpeerconn();}

}
