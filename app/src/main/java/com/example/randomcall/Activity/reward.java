package com.example.randomcall.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.randomcall.R;
import com.example.randomcall.databinding.ActivityRewardBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class reward extends AppCompatActivity {
    ActivityRewardBinding rewardBinding;
    FirebaseDatabase firebaseDatabase;
    RewardedAd rewardeAd;
    String current_id;
    int coins = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rewardBinding = ActivityRewardBinding.inflate(getLayoutInflater());
        setContentView(rewardBinding.getRoot());
        firebaseDatabase = FirebaseDatabase.getInstance();
        current_id = FirebaseAuth.getInstance().getUid();
        LoadAd();
        rewardBinding.video1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardeAd != null) {
                    Activity activity = reward.this;
                    rewardeAd.show(activity, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            LoadAd();
                            coins = coins + 200;
                            firebaseDatabase.getReference().child("profiles").child(current_id).child("coins").setValue(coins);
                            rewardBinding.video1Icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
                }
            }
        });

        rewardBinding.video2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardeAd != null) {
                    Activity activity = reward.this;
                    rewardeAd.show(activity, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            LoadAd();
                            coins = coins + 200;
                            firebaseDatabase.getReference().child("profiles").child(current_id).child("coins").setValue(coins);
                            rewardBinding.video2Icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
                }
            }
        });
        rewardBinding.video3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardeAd != null) {
                    Activity activity = reward.this;
                    rewardeAd.show(activity, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            LoadAd();
                            coins = coins + 200;
                            firebaseDatabase.getReference().child("profiles").child(current_id).child("coins").setValue(coins);
                            rewardBinding.video3Icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
                }
            }
        });
        rewardBinding.video4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardeAd != null) {
                    Activity activity = reward.this;
                    rewardeAd.show(activity, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            LoadAd();
                            coins = coins + 200;
                            firebaseDatabase.getReference().child("profiles").child(current_id).child("coins").setValue(coins);
                            rewardBinding.video4Icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
                }
            }
        });

        rewardBinding.video5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardeAd != null) {
                    Activity activity = reward.this;
                    rewardeAd.show(activity, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            LoadAd();
                            coins = coins + 200;
                            firebaseDatabase.getReference().child("profiles").child(current_id).child("coins").setValue(coins);
                            rewardBinding.video5Icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
                }
            }
        });

        firebaseDatabase.getReference().child("profiles").child(current_id).child("coins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coins = snapshot.getValue(Integer.class);
                rewardBinding.coins.setText(String.valueOf(coins));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void LoadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                rewardeAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                rewardeAd = rewardedAd;
            }
        });
    }
}