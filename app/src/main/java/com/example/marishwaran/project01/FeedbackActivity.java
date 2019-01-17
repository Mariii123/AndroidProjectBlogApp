package com.example.marishwaran.project01;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FeedbackActivity extends AppCompatActivity {
    RecyclerView feedbackView;
    FloatingActionButton feedBtn;
    FirebaseFirestore firestore;
    List<FeedbackList> feedbackLists;
    FeedbackAdapter feedbackAdapter;
    private long back_pressed;
    Toast backToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSupportActionBar().setTitle("Feedbacks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        feedbackLists = new ArrayList<>();
        feedbackView = findViewById(R.id.feedback_list);
        feedbackView.setLayoutManager(new LinearLayoutManager(this));
        feedbackAdapter = new FeedbackAdapter(feedbackLists);
        feedbackView.setAdapter(feedbackAdapter);
        feedBtn = findViewById(R.id.add_feedback);


        firestore = FirebaseFirestore.getInstance();
        feedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addFeed = new Intent(FeedbackActivity.this, AddFeedbackActivity.class);
                startActivity(addFeed);
            }
        });
        firestore.collection("Feedbacks").orderBy("ftimestamp", Query.Direction.DESCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshots != null){
                    if (!documentSnapshots.isEmpty()) {
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                FeedbackList list = doc.getDocument().toObject(FeedbackList.class);
                                feedbackLists.add(list);
                                feedbackAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
        }
        back_pressed = System.currentTimeMillis();
        backToast = Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_LONG);
        backToast.show();
    }
}
