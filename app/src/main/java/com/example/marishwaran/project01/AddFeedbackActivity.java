package com.example.marishwaran.project01;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import android.content.Intent;
public class AddFeedbackActivity extends AppCompatActivity {
    Button add_feed;
    EditText add_feed_desc;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    String name, img, desc;
    ProgressBar add_feed_progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);
        getSupportActionBar().setTitle("Add Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        add_feed = findViewById(R.id.add_feed_btn);
        add_feed_desc = findViewById(R.id.add_feedback_des);
        add_feed_progress = findViewById(R.id.add_feed_progress);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        add_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_feed.setClickable(false);
                add_feed_progress.setVisibility(View.VISIBLE);
                String des = add_feed_desc.getText().toString();
                if (!TextUtils.isEmpty(des)){
                    Map<String, Object> feedMap = new HashMap<>();
                    feedMap.put("fname", name);
                    feedMap.put("fimg", img);
                    feedMap.put("fdesc", des.trim());
                    feedMap.put("ftimestamp", FieldValue.serverTimestamp());
                    firestore.collection("Feedbacks").document().
                            set(feedMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessage("Feedback Added");
                            add_feed_progress.setVisibility(View.INVISIBLE);
                            add_feed.setClickable(true);
                            Intent addFeed = new Intent(AddFeedbackActivity.this, FeedbackActivity.class);
                            startActivity(addFeed);
                        }
                    });
                }
                else {
                    showMessage("Please write some description");
                }
            }
        });
    firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()){
                name = task.getResult().getString("Username");
                img = task.getResult().getString("Userimg");
            }
            else {
                showMessage(""+task.getException().getMessage());
            }
        }
    });

    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
