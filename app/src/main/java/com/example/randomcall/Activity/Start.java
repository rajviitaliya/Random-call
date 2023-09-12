package com.example.randomcall.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.randomcall.Model.UserModel;
import com.example.randomcall.R;
import com.example.randomcall.databinding.ActivityStartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Start extends AppCompatActivity {
    ActivityStartBinding binding;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    int RequestCode = 1;
    String[] permission = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    long coins = 0;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase.getReference().child("profiles").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel = snapshot.getValue(UserModel.class);
                coins = userModel.getCoins();
                binding.coins.setText("You Have:" + coins);
                Glide.with(Start.this).load(userModel.getProfile()).into(binding.profilePicture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("databaseerror", error.getMessage());
            }
        });
        binding.findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("hereeeeee", "" + coins);
                if (ispermissionGranted()) {
                    if (coins > 5) {
                        coins = coins - 5;
                        firebaseDatabase.getReference().child("profiles").child(firebaseUser.getUid()).child("coins").setValue(coins);
                        Intent intent = new Intent(Start.this, Connecting.class);
                        intent.putExtra("profile", userModel.getProfile());
                        startActivity(intent);
                    } else {
                        Toast.makeText(Start.this, "Insufficient coin", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    askpermission();
                }
            }
        });
        binding.rewardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Start.this, reward.class);
                startActivity(intent);
            }
        });

    }

    void askpermission() {
        ActivityCompat.requestPermissions(this, permission, RequestCode);
    }


    public boolean ispermissionGranted() {
        for (String permission : permission) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}