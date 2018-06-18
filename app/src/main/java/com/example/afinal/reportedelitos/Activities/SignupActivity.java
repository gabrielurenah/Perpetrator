package com.example.afinal.reportedelitos.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.reportedelitos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity
        extends AppCompatActivity
        implements View.OnClickListener {

    private TextInputEditText mPasswordRegistration, mEmailRegistration;
    private Button mButtonRegistration;
    private TextView mLogintv;
    private ProgressBar mPb;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        mEmailRegistration = (TextInputEditText) findViewById(R.id.my_et_email);
        mPasswordRegistration = (TextInputEditText) findViewById(R.id.my_et_password);
        mButtonRegistration = (Button) findViewById(R.id.my_signup_button);
        mLogintv = (TextView) findViewById(R.id.my_login_textview);
        mPb = (ProgressBar) findViewById(R.id.my_progress_bar);

        mPasswordRegistration.setTransformationMethod(new PasswordTransformationMethod());

        mLogintv.setOnClickListener(this);
        mButtonRegistration.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.my_signup_button) {
            registerUser();
        } else if (v.getId() == R.id.my_login_textview) {
            finish();
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        }
    }

    private void registerUser() {
        String email = mEmailRegistration.getText().toString().trim();
        String password = mPasswordRegistration.getText().toString();

        //Check if email or password are empty
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        mPb.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendEmailVerification();
                            mPb.setVisibility(View.INVISIBLE);
                        } else if (!task.isSuccessful()) {
                            if (task.getException() != null) {
                                Toast.makeText(SignupActivity.this,
                                        "User registration Failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                                mPb.setVisibility(View.INVISIBLE);
                            }
                        }else {
                            Toast.makeText(SignupActivity.this,
                                            "Could not register, please try again",
                                            Toast.LENGTH_SHORT).show();

                            mPb.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void sendEmailVerification() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentUser.
                    sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this,
                                "An email verification has been sent",
                                Toast.LENGTH_SHORT).show();

                        mAuth.signOut();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            });
        }
    }
}