package com.example.marishwaran.project01;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    final static Pattern PASSWORD_MATCH = Pattern.compile("^" +
            //"(?=.*[0-9])" +         //at least 1 digit
            //"(?=.*[a-z])" +         //at least 1 lower case letter
            //"(?=.*[A-Z])" +         //at least 1 upper case letter
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=.*[@#$%^&+=])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{4,}" +               //at least 4 characters
            "$");
    final static int CODE = 1;
    private FirebaseAuth mAuth;
    private TextInputLayout sName;
    private TextInputLayout sEmail;
    private TextInputLayout sPass;
    private Button signup_btn;
    private ProgressBar sProgress;
    FirebaseUser cUser;
    private long back_pressed;
    Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Create Your Account");
        mAuth = FirebaseAuth.getInstance();
        cUser = mAuth.getCurrentUser();
        sName = findViewById(R.id.signUpName);
        sEmail = findViewById(R.id.signUpEmail);
        sPass = findViewById(R.id.signUpPass);
        signup_btn = findViewById(R.id.signUpBtn);
        sProgress = findViewById(R.id.cProgress);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();
        if (cUser != null) {
            Intent home = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(home);
            finish();
        }
    }

    boolean validateEmail() {
        String email = sEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            sEmail.setError("Field Can't be Empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sEmail.setError("Please enter a valid email");
            return false;
        } else {
            sEmail.setError(null);
            return true;
        }
    }

    boolean validateName() {
        String name = sName.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            sName.setError("Field Can't be Empty");
            return false;
        } else if (name.length() > 15) {
            sName.setError("Name too long");
            return false;
        } else {
            sName.setError(null);
            return true;
        }
    }

    boolean validatePass() {
        String pass = sPass.getEditText().getText().toString().trim();
        if (pass.isEmpty()) {
            sPass.setError("Field Can't be Empty");
            return false;
        } else if (!PASSWORD_MATCH.matcher(pass).matches()) {
            sPass.setError("Password too weak");
            return false;
        } else {
            sPass.setError(null);
            return true;
        }
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

    public void signUp(View view) {
        if (!validateName() | !validateEmail() | !validatePass()) {
            return;
        }
        String email = sEmail.getEditText().getText().toString();
        String pass = sPass.getEditText().getText().toString();
        final String name = sName.getEditText().getText().toString();
        signup_btn.setClickable(false);
        sProgress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
                    showMessage("Successfully created");
                    signup_btn.setClickable(true);
                    sProgress.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(SignUpActivity.this, SetupActivity.class);
                    startActivity(intent);

                } else {
                    showMessage(task.getException().getMessage());
                    signup_btn.setClickable(true);
                    sProgress.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    public void signUpIn(View view) {
        Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(i);
        finish();
    }

    void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}

