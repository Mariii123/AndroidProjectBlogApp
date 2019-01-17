package com.example.marishwaran.project01;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DisplayImageActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ImageView dimg = findViewById(R.id.dis_img);
        Toolbar dToolbar = findViewById(R.id.dis_toolbar);
        setSupportActionBar(dToolbar);
        Intent i = getIntent();
        String id = i.getStringExtra("Id");
        String img = i.getStringExtra("Img");
        firestore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String name = task.getResult().getString("Username");
                getSupportActionBar().setTitle(name);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Glide.with(this).load(img).into(dimg);
    }
}


