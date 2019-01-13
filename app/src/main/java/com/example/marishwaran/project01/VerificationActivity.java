package com.example.marishwaran.project01;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {
    FirebaseUser mUser;
    Button verify;
    TextView mail, status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mail = findViewById(R.id.mail_text);
        status = findViewById(R.id.ever_text);
        mail.setText(mUser.getEmail());
        boolean stat = mUser.isEmailVerified();
        String statText = stat ? "Verified" : "Not Verified";
        status.setText(statText);
    }

    public void sendVerification(View view) {
        if (!mUser.isEmailVerified()) {
            mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Verifiation send to " + mUser.getEmail(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
        else {
            verify.setEnabled(false);
        }
    }
}
