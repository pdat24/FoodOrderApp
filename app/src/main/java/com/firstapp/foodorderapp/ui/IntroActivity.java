package com.firstapp.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.utils.Constants;
import com.firstapp.foodorderapp.utils.Functions;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class IntroActivity extends AppCompatActivity {

    @Inject
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getWindow().setStatusBarColor(getColor(R.color.intro_status_bar));
        Functions.setStatusBarTextToDark(this);
        if (Functions.isInternetConnected(this)) {
            if (firebaseAuth.getCurrentUser() != null) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        };
    }

    public void toSignUp(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}