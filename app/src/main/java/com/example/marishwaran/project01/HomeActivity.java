package com.example.marishwaran.project01;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class HomeActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {
    private android.support.v7.widget.Toolbar mToolbar;
    Button signOut;
    FirebaseUser mUser;
    private NavigationView navView;
    private FirebaseAuth mAuth;
    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    private RecyclerView post_view;
    private List<BlogPost> blog_list;
    private MyAdapter blogAdapter;
    private DocumentSnapshot lastVisibe;
    private Boolean isPageFirstLoaded = true;
    private long back_pressed;
    Toast backToast;
    //NavDrawer Contents
    private TextView nav_name;
    private TextView nav_mail;
    private ImageView nav_img;
    private String uid;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    private String name, img, mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawer = findViewById(R.id.main_drawer);
        navView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uid = mAuth.getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();
        FloatingActionButton fadd = findViewById(R.id.home_post_btn);
        fadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post_intent = new Intent(HomeActivity.this, AddPostActivity.class);
                startActivity(post_intent);
            }
        });
        View header_view = navView.getHeaderView(0);
        nav_img = header_view.findViewById(R.id.nav_pic);
        nav_name = header_view.findViewById(R.id.nav_name);
        nav_mail = header_view.findViewById(R.id.nav_mail);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.setting:
                        Intent setup = new Intent(HomeActivity.this, SetupActivity.class);
                        startActivity(setup);
                        return true;
                    case R.id.feedback:
                        Intent feed = new Intent(HomeActivity.this, FeedbackActivity.class);
                        startActivity(feed);
                        return true;
                    case R.id.logout:
                        mAuth.signOut();

                        Intent signIn = new Intent(HomeActivity.this, SignInActivity.class);
                        startActivity(signIn);
                        finish();
                        return true;

                }
                return false;
            }
        });
        blog_list = new ArrayList<>();
        post_view = findViewById(R.id.blog_post_list);
        blogAdapter = new MyAdapter(blog_list);
        blogAdapter.setOnItemClickListener(this);
        post_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        post_view.setAdapter(blogAdapter);
        post_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if (reachedBottom){
                    loadMorePost();
                }
            }
        });
        firestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    name = task.getResult().getString("Username");
                    img = task.getResult().getString("Userimg");
                    mail = mAuth.getCurrentUser().getEmail();
                    nav_name.setText(name);
                    Glide.with(getApplicationContext()).setDefaultRequestOptions
                            (new RequestOptions().placeholder(R.drawable.userphoto))
                            .load(img).into(nav_img);
                    nav_mail.setText(mail);

                }
            }
        });
        nav_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, DisplayImageActivity.class);
                i.putExtra("Id", uid);
                i.putExtra("Img", img);
                startActivity(i);
            }
        });
        Query firstQuery = firestore.collection("Posts").orderBy("Timestamp", Query.Direction.DESCENDING).limit(3);
        firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {
                        if (isPageFirstLoaded) {
                            lastVisibe = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        }
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                                if (isPageFirstLoaded) {
                                    blog_list.add(blogPost);
                                } else {
                                    blog_list.add(0, blogPost);

                                }
                                blogAdapter.notifyDataSetChanged();
                            }
                        }
                        isPageFirstLoaded = false;
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (back_pressed + 2000 > System.currentTimeMillis()){
                backToast.cancel();
                super.onBackPressed();
            }
            back_pressed = System.currentTimeMillis();
            backToast = Toast.makeText(HomeActivity.this, "Press again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser == null) {
            updateUI();
        }
    }
    public void loadMorePost(){
        Query nextQuery = firestore.collection("Posts")
                .orderBy("Timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisibe)
                .limit(3);
        nextQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    lastVisibe = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);

                            blog_list.add(blogPost);
                            blogAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)){
            return true;
        }
         return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        Intent i = new Intent(HomeActivity.this, SignInActivity.class);
        startActivity(i);
        finish();
    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(int position) {
        BlogPost post = blog_list.get(position);
        Intent img = new Intent(HomeActivity.this, DisplayImageActivity.class);
        img.putExtra("Id", post.getUser_id());
        img.putExtra("Img", post.getImage_url());
        startActivity(img);
    }
}
