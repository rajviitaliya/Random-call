package com.example.randomcall.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.randomcall.Model.Interface;
import com.example.randomcall.Model.UserModel;
import com.example.randomcall.R;
import com.example.randomcall.databinding.ActivityCallBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Call extends AppCompatActivity {
    ActivityCallBinding binding;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String user_id = "";
    String createdby;
    String frdusername;
  String uniqid="";

     boolean pageexit=false;
  boolean isaudio=true;
  boolean isvideo=true;
  boolean ispeerconn=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        user_id = getIntent().getStringExtra("userid");
        String incoming = getIntent().getStringExtra("incoming");
        createdby = getIntent().getStringExtra("createdby");
        frdusername = incoming;
        SetUpWebView();
        binding.micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isaudio=!isaudio;
                    callJavaScripFunction("javascript:toggleAudio(\""+isaudio+"\")");
                    if (isaudio){
                        binding.micBtn.setImageResource(R.drawable.btn_unmute_normal);
                    }else {
                        binding.micBtn.setImageResource(R.drawable.btn_mute_normal);
                    }
            }
        });
        binding.videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isvideo=!isvideo;
                callJavaScripFunction("javascript:toggleVideo(\""+isvideo+"\")");
                if (isvideo){
                    binding.videoBtn.setImageResource(R.drawable.btn_video_normal);
                }else {
                    binding.videoBtn.setImageResource(R.drawable.btn_video_muted);
                }
            }

        });
        binding.endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void SetUpWebView() {
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }
        });
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webView.addJavascriptInterface(new Interface(this), "Android");
        loadVideoCall();
    }
    public void loadVideoCall(){
        String filepath="file:android_asset/call.html";
        binding.webView.loadUrl(filepath);
        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializepeer();
            }
        });
    }
    public void initializepeer(){
        uniqid=getuniqid();
        callJavaScripFunction("javascript:init(\""+uniqid+"\")");
        if (createdby.equalsIgnoreCase(user_id)){
            if (pageexit)
                return;
            databaseReference.child(user_id).child("connId").setValue(uniqid);
            databaseReference.child(user_id).child("isavailable").setValue(true);
            binding.loadingGroup.setVisibility(View.GONE);
            binding.controls.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference().child("profiles").child(frdusername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel model=snapshot.getValue(UserModel.class);
                    Glide.with(Call.this).load(model.getProfile()).into(binding.profile);
                    binding.name.setText(model.getName());
                    binding.city.setText(model.getCity());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    frdusername=createdby;
                    FirebaseDatabase.getInstance().getReference().child("profiles").child(frdusername).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel userModel=snapshot.getValue(UserModel.class);
                            Glide.with(Call.this).load(userModel.getProfile()).into(binding.profile);
                            binding.name.setText(userModel.getName());
                            binding.city.setText(userModel.getCity());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child("users").child(frdusername).child("connId").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue()!=null){
                                sedcallrequest();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            },3000);
        }

    }
    public void onpeerconn(){
        ispeerconn=true;
    }
    void  sedcallrequest(){
        if (!ispeerconn) {
            Toast.makeText(this, "please check connection", Toast.LENGTH_SHORT).show();
            return;
        }
        listenconnid();
    }
    void  listenconnid(){
        databaseReference.child(frdusername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()==null){
                    return;
                }
                binding.loadingGroup.setVisibility(View.GONE);
                binding.controls.setVisibility(View.VISIBLE);
                String connid=snapshot.getValue(String.class);
                callJavaScripFunction("javascript:startCall(\""+connid+"\")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void callJavaScripFunction(String function){binding.webView.post(new Runnable() {
        @Override
        public void run() {
             binding.webView.evaluateJavascript(function,null);
        }
    });
    }
    String getuniqid(){
        return UUID.randomUUID().toString();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageexit=true;
        databaseReference.child(createdby).setValue(null);
        finish();
    }
}
