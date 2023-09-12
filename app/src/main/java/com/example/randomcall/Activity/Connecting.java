package com.example.randomcall.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;

import com.bumptech.glide.Glide;
import com.example.randomcall.R;
import com.example.randomcall.databinding.ActivityConnectingBinding;
import com.example.randomcall.databinding.ActivityRewardBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Connecting extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ActivityConnectingBinding binding;

    Boolean isokay=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityConnectingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        String profile=getIntent().getStringExtra("profile");
        Glide.with(this).load(profile).into(binding.profile);
        String user_id=firebaseAuth.getUid();
        firebaseDatabase.getReference().child("users").orderByChild("status").equalTo(0).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    isokay=true;
                    //room available
                    for (DataSnapshot dtasnapshot:snapshot.getChildren()){
                        firebaseDatabase.getReference().child("users").child(dtasnapshot.getKey()).child("incoming").setValue(user_id);
                        firebaseDatabase.getReference().child("users").child(dtasnapshot.getKey()).child("status").setValue(1);
                        Intent intent=new Intent(Connecting.this, Call.class);
                        String incoming=dtasnapshot.child("incoming").getValue(String.class);
                        String createdby=dtasnapshot.child("createdby").getValue(String.class);
                        boolean isavailable= Boolean.TRUE.equals(dtasnapshot.child("isavailable").getValue(Boolean.class));
                        intent.putExtra("userid",user_id);
                        intent.putExtra("incoming",incoming);
                        intent.putExtra("createdby",createdby);
                        intent.putExtra("isavailable",isavailable);
                        startActivity(intent);

                    }

                }else {
                    //not available
                    HashMap<String,Object> room=new HashMap<>();
                    room.put("incoming",user_id);
                    room.put("createdby",user_id);
                    room.put("isavailable",true);
                    room.put("status",0);
                    firebaseDatabase.getReference().child("users").child(user_id).setValue(room).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            firebaseDatabase.getReference().child("users").child(user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.child("status").exists()){
                                        if (snapshot.child("status").getValue(Integer.class)==1){
                                            if (isokay)
                                                return;
                                            isokay =true;
                                            Intent intent=new Intent(Connecting.this, Call.class);
                                            String Incoming=snapshot.child("incoming").getValue(String.class);
                                            String Createdby=snapshot.child("createdby").getValue(String.class);
                                            boolean isavailabe=snapshot.child("isavailable").getValue(Boolean.class);
                                            intent.putExtra("userid",user_id);
                                            intent.putExtra("incoming",Incoming);
                                            intent.putExtra("createdby",Createdby);
                                            intent.putExtra("isavailable",isavailabe);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}