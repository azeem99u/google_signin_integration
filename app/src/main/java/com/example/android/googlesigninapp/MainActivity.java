package com.example.android.googlesigninapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private static final int SING_IN_REQUEST_CODE = 1232;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mOutputTxt;
    private Button mSignOutBtn;
    private SignInButton mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOutputTxt = findViewById(R.id.textView);
        mSignOutBtn = findViewById(R.id.signOut);
        mSignInButton = findViewById(R.id.signInBtn);

        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder()
                        .requestEmail()
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        mSignInButton.setOnClickListener(this::signIn);
        mSignOutBtn.setOnClickListener(this::signOut);
    }

    private void signIn(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,SING_IN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SING_IN_REQUEST_CODE){
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignIn(accountTask);
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleGoogleSignIn(Task<GoogleSignInAccount> accountTask) {

        try {
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            mOutputTxt.setText(GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()) +"\n"+ e.getStatusCode());
        }

    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            mSignOutBtn.setVisibility(View.VISIBLE);
            mSignInButton.setVisibility(View.GONE);
            mOutputTxt.setText(account.getEmail());
        } else {
            mSignOutBtn.setVisibility(View.GONE);
            mSignInButton.setVisibility(View.VISIBLE);
            mOutputTxt.setText("User is not Login");
        }
    }

    public void signOut(View view) {

         mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "User logged out", Toast.LENGTH_SHORT).show();
                    updateUI(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
                }
                else {
                    Toast.makeText(MainActivity.this, "Some error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "User logged out", Toast.LENGTH_SHORT).show();
                    updateUI(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
                }
                else {
                    Toast.makeText(MainActivity.this, "Some error", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


    }
}