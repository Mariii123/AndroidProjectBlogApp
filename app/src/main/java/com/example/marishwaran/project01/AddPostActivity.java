package com.example.marishwaran.project01;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static com.example.marishwaran.project01.SetupActivity.CODE;

public class AddPostActivity extends AppCompatActivity {
    static final int PCODE = 1;
    ImageView img;
    EditText desc;
    Button post;
    StorageReference mStorage;
    private Uri imgUri = null;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    String user_id;
    ProgressBar pProgress;
    Bitmap compressedBitmap;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img = findViewById(R.id.add_post_img);
        post = findViewById(R.id.add_post_btn);
        desc = findViewById(R.id.add_post_desc);
        pProgress = findViewById(R.id.pProgress);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mStorage = FirebaseStorage.getInstance().getReference().child("Post_Images");
        firestore = FirebaseFirestore.getInstance();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImgChooser();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String des = desc.getText().toString();
                if (imgUri != null && !TextUtils.isEmpty(des)) {
                    post.setClickable(false);
                    pProgress.setVisibility(View.VISIBLE);
                    final String random = UUID.randomUUID().toString();
                    final StorageReference filePath = mStorage.child(random + ".jpg");
                    filePath.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {


                                        final Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("image_url", uri.toString());
                                        postMap.put("desc", desc.getText().toString());
                                        postMap.put("user_id", user_id);
                                        postMap.put("Timestamp", FieldValue.serverTimestamp());

                                        firestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {
                                                    showMessage("Post updated");
                                                    Intent home = new Intent(AddPostActivity.this, HomeActivity.class);
                                                    startActivity(home);
                                                    finish();

                                                } else {
                                                    showMessage("" + task.getException().getMessage());
                                                }
                                                post.setClickable(true);
                                                pProgress.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                                });
                            } else {
                                showMessage("" + task.getException().getMessage());
                                post.setClickable(true);
                                pProgress.setVisibility(View.INVISIBLE);
                            } }
                    });
                }
                else {
                    showMessage("Please select image and write description");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PCODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            Glide.with(AddPostActivity.this).load(imgUri).into(img);
        }
    }

    void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void openImgChooser() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("*/*");
        startActivityForResult(i, PCODE);
    }
}
