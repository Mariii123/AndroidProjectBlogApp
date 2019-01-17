package com.example.marishwaran.project01;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout sEmail, sPass;
    private ProgressBar sprogress;
    private long back_pressed;
    Button signin_btn;
    Toast backToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().setTitle("Login Here");
        mAuth = FirebaseAuth.getInstance();
        sEmail = findViewById(R.id.signInEmail);
        sPass = findViewById(R.id.signInPass);
        sprogress = findViewById(R.id.signInProgress);
        signin_btn = findViewById(R.id.signInBtn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            Intent main = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(main);
            finish();
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

    boolean validatePass() {
        String pass = sPass.getEditText().getText().toString().trim();
        if (pass.isEmpty()) {
            sPass.setError("Field Can't be Empty");
            return false;
        } else {
            sPass.setError(null);
            return true;
        }
    }

    public void signIn(View view) {
        if (validateEmail() | validatePass()) {
            signin_btn.setClickable(false);
            sprogress.setVisibility(View.VISIBLE);
            String email = sEmail.getEditText().getText().toString();
            String pass = sPass.getEditText().getText().toString();
            mAuth.signInWithEmailAndPassword(email, pass.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        signin_btn.setClickable(true);
                        sprogress.setVisibility(View.INVISIBLE);
                        Intent i = new Intent(SignInActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                        Toast.makeText(SignInActivity.this, "Logged In", Toast.LENGTH_LONG).show();
                    } else {
                        signin_btn.setClickable(true);
                        sprogress.setVisibility(View.INVISIBLE);
                        Toast.makeText(SignInActivity.this, " " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(SignInActivity.this, "Please enter valid data", Toast.LENGTH_LONG).show();
        }
    }

    public void signInUp(View view) {
        Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(i);
        finish();
    }
}
