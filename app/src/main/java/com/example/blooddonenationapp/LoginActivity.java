package com.example.blooddonenationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView backButton;
    private Button loinButton;
    private TextInputEditText loginemail,loginpassword;
    private TextView forgotPassword;

    private ProgressDialog loader;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user !=null){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        backButton=findViewById(R.id.backButton);
        loinButton=findViewById(R.id.loinButton);
        loginemail=findViewById(R.id.loginemail);
        loginpassword=findViewById(R.id.loginpassword);
        forgotPassword=findViewById(R.id.forgotPassword);

        loader = new ProgressDialog(this);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,SelectRegistractionMainActivity.class);
                startActivity(intent);
            }
        });

        loinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              final String email = loginemail.getText().toString().trim();
              final String password = loginpassword.getText().toString().trim();

              if (TextUtils.isEmpty(email)){
                  loginemail.setError("Email is required...");
              }
              if (TextUtils.isEmpty(password)){
                  loginpassword.setError("Password is required...");
              }

              else {
                  loader.setMessage("Log in in progress...");
                  loader.setCanceledOnTouchOutside(false);
                  loader.show();

                  mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()){
                             Toast.makeText(LoginActivity.this, "Login Successful...", Toast.LENGTH_SHORT).show();
                             Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                             startActivity(intent);
                             finish();
                         }else {
                             Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                         }

                         loader.dismiss();
                      }
                  });

              }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}