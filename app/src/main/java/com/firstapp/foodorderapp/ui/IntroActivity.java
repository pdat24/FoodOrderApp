package com.firstapp.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getWindow().setStatusBarColor(getColor(R.color.intro_status_bar));
        WindowInsetsControllerCompat insetsAnimationCompat =
            ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (insetsAnimationCompat != null)
            insetsAnimationCompat.setAppearanceLightStatusBars(true);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void toSignUp(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setAction(Constants.ACTION_SIGNUP);
        startActivity(intent);
    }

    public void tpLogin(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setAction(Constants.ACTION_LOGIN);
        startActivity(intent);
    }
}