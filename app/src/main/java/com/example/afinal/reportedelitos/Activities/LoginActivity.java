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

public class LoginActivity
        extends AppCompatActivity
        implements View.OnClickListener {

    private TextInputEditText mPasswordLogin, mEmailLogin;
    private Button mButtonLogin;
    private TextView mSignUptv;
    private ProgressBar mPb;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmailLogin = (TextInputEditText) findViewById(R.id.my_et_email);
        mPasswordLogin = (TextInputEditText) findViewById(R.id.my_et_password);
        mButtonLogin = (Button) findViewById(R.id.my_login_button);
        mSignUptv = (TextView) findViewById(R.id.my_tv_to_signup);
        mPb = (ProgressBar) findViewById(R.id.my_login_pb);
        mPasswordLogin.setTransformationMethod(new PasswordTransformationMethod());

        mButtonLogin.setOnClickListener(this);
        mSignUptv.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        loginUser();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.my_login_button) {
            loginUser();
        }
        else if (v.getId() == R.id.my_tv_to_signup) {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        }
    }

    private void loginUser() {
        String email = mEmailLogin.getText().toString().trim();
        String password = mPasswordLogin.getText().toString();

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

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mPb.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()) {
                                checkEmailVerification();
                            }
                        } else if (!task.isSuccessful()) {
                            if (task.getException() != null) {
                                Toast.makeText(LoginActivity.this,
                                                "User login Failed: " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();

                                mPb.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this,
                                            "Could not login, please try again",
                                            Toast.LENGTH_SHORT).show();

                            mPb.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void checkEmailVerification() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                Toast.makeText(LoginActivity.this,
                        "Welcome",
                        Toast.LENGTH_SHORT).show();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                mAuth.signOut();
                Toast.makeText(this,
                        "Error, user not verified",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
