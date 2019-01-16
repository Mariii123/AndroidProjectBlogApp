package com.example.marishwaran.project01;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {
    static final int CODE = 1;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    CircleImageView sImg;
    EditText sName;
    Button sSave;
    Uri imgUri = null;
    Uri fImguri;
    StorageReference mStorage;
    ProgressBar sProgress;
    String uid;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getSupportActionBar().setTitle("Settings");
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        sName = findViewById(R.id.setup_name);
        sImg = findViewById(R.id.setup_pic);
        sSave = findViewById(R.id.setup_save);
        sProgress = findViewById(R.id.setup_progress);
        mStorage = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        showMessage("Update your profile here");
        sImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openImgChooser();
            }
        });
        sSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = sName.getText().toString();
                if (!TextUtils.isEmpty(user_name) && imgUri != null) {
                    sProgress.setVisibility(View.VISIBLE);
                    final StorageReference filePath = mStorage.child("Profile_Images").child(uid + ".jpg");
                    filePath.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("Username", user_name);
                                    userMap.put("Userimg", uri.toString());
                                    firestore.collection("Users").document(uid).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showMessage("Profile Updated");
                                                Intent main = new Intent(SetupActivity.this, HomeActivity.class);
                                                startActivity(main);
                                                finish();
                                            } else {
                                                showMessage("" + task.getException().getMessage());
                                            }
                                            sProgress.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            });

                            sProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else {
                    showMessage("Please select image and write description");
                }
            }
        });
        sProgress.setVisibility(View.VISIBLE);
        sSave.setEnabled(false);
        firestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                sProgress.setVisibility(View.VISIBLE);
                sSave.setEnabled(false);
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String user_name = task.getResult().getString("Username");
                        String user_img = task.getResult().getString("Userimg");
                        sName.setText(user_name);
                        RequestOptions placeHolder = new RequestOptions();
                        placeHolder.placeholder(R.drawable.userphoto);
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeHolder).load(user_img).into(sImg);
                    }
                } else {
                    showMessage("" + task.getException().getMessage());
                }
                sProgress.setVisibility(View.INVISIBLE);
                sSave.setEnabled(true);
            }
        });
    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            Glide.with(SetupActivity.this).load(imgUri).into(sImg);
        } else {
            showMessage("Please select a image");
        }
    }

    private void openImgChooser() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, CODE);
    }


}
