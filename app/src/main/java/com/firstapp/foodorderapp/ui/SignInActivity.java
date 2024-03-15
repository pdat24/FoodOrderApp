package com.firstapp.foodorderapp.ui;

import static com.firstapp.foodorderapp.utils.Constants.CONFLICT_CODE;
import static com.firstapp.foodorderapp.utils.Constants.MAIN_SHARED_PREFERENCE;
import static com.firstapp.foodorderapp.utils.Constants.OK_CODE;
import static com.firstapp.foodorderapp.utils.Constants.UID;
import static com.firstapp.foodorderapp.utils.Constants.UNAUTHORIZED_CODE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.api.UserApi;
import com.firstapp.foodorderapp.model.User;
import com.firstapp.foodorderapp.utils.Functions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Response;

@AndroidEntryPoint
public class SignInActivity extends AppCompatActivity {
    MaterialButton submitButton;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextView warning;
    ImageView googleSignInButton;
    InputMethodManager inputMethodManager;
    SharedPreferences sharedPreferences;
    View loading;
    ActivityResultLauncher<Intent> googleSignInLauncher;
    @Inject
    FirebaseAuth firebaseAuth;
    @Inject
    UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().setStatusBarColor(getColor(R.color.white_bg));
        Functions.setStatusBarTextToDark(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        sharedPreferences = getSharedPreferences(MAIN_SHARED_PREFERENCE, MODE_PRIVATE);

        // get views
        loading = findViewById(R.id.viewLoading);
        emailInput = findViewById(R.id.ipEmail);
        passwordInput = findViewById(R.id.ipPassword);
        submitButton = findViewById(R.id.btnSubmit);
        googleSignInButton = findViewById(R.id.btnGoogleSignIn);
        warning = findViewById(R.id.tvWarning);

        // add event listeners
        submitButton.setOnClickListener((v) -> {
            warning.setVisibility(View.GONE);
            String email = String.valueOf(emailInput.getText());
            String password = String.valueOf(passwordInput.getText());
            if (
                Functions.isInternetConnected(this) &&
                    isCredentialsValid(email, password)
            )
                signInWithEmailAndPassword(email, password);
        });
        googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleSignInWithGoogle
        );
        googleSignInButton.setOnClickListener((v) -> {
                if (Functions.isInternetConnected(this))
                    requestGoogleSignIn();
            }
        );
    }

    private void requestGoogleSignIn() {
        GoogleSignInOptions options = new GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_auth_id_token))
            .requestEmail()
            .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, options);
        client.revokeAccess();
        googleSignInLauncher.launch(client.getSignInIntent());
    }

    private boolean isCredentialsValid(String email, String password) {
        boolean valid = true;
        String promptText = null;
        String emailPattern = "^[0-9A-Za-z.+-_%]+@[a-z0-9A-Z-.]+\\.[a-zA-Z0-9]{2,}$";
        String passwordPattern = "^[a-zA-Z0-9]{6,}$";
        // validate email
        if (!email.matches(emailPattern)) {
            valid = false;
            promptText = "The email is invalid!";
        }
        // validate password
        if (password.length() < 6) {
            valid = false;
            promptText = "The length of password must be at least 8";
        } else if (!password.matches(passwordPattern)) {
            valid = false;
            promptText = "The password must only contains letters, numbers";
        }
        if (!valid) {
            createVibration();
            warning.setVisibility(View.VISIBLE);
            warning.setText(promptText);
        } else {
            warning.setVisibility(View.INVISIBLE);
        }
        return valid;
    }

    void createVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(
            VibrationEffect.createOneShot(75, VibrationEffect.DEFAULT_AMPLITUDE)
        );
    }

    void signInWithEmailAndPassword(String email, String password) {
        showLoading();
        new Thread(() -> {
            Call<User> task = userApi.signUserInWithEmailAndPassword(email, password);
            try {
                Response<User> res = task.execute();
                if (res.code() == OK_CODE) {
                    // login success
                    startSignUserIn(email, password);
                } else if (res.code() == CONFLICT_CODE) {
                    // invalid password
                    hideLoading();
                    promptInvalidPassword();
                } else if (res.code() == UNAUTHORIZED_CODE) {
                    // email is not existing
                    createUserWithEmailAndPassword(email, password);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void createUserWithEmailAndPassword(String email, String password) {
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    assert firebaseUser != null : "Firebase user is null";
                    Call<User> req = userApi.createUser(
                        new User(firebaseUser.getUid(), email, password)
                    );
                    new Thread(() -> {
                        try {
                            req.execute();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                    startSignUserIn(email, password);
                }
                hideLoading();
            });
    }

    @SuppressLint("SetTextI18n")
    private void promptInvalidPassword() {
        runOnUiThread(() -> {
            createVibration();
            warning.setVisibility(View.VISIBLE);
            warning.setText("Invalid password");
        });
    }

    private void startSignUserIn(String email, String password) {
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                        handleAuthenticationOK(task.getResult());
                    hideLoading();
                }
            });
    }

    private void showLoading() {
        runOnUiThread(() -> {
            loading.setVisibility(View.VISIBLE);
        });
    }

    private void hideLoading() {
        runOnUiThread(() -> {
            loading.setVisibility(View.GONE);
        });
    }

    private void handleSignInWithGoogle(ActivityResult activityResult) {
        showLoading();
        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(
            activityResult.getData()
        ).getResult();
        AuthCredential credential =
            GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                new Thread(() -> {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    assert firebaseUser != null : "Firebase user is null";
                    Call<User> req = userApi.signUserInWithProvider(firebaseUser.getUid());
                    try {
                        Response<User> res = req.execute();
                        if (res.code() != OK_CODE)
                            userApi.createUser(
                                new User(firebaseUser.getUid(), firebaseUser.getEmail(), null)
                            ).execute();
                        handleAuthenticationOK(task.getResult());
                        hideLoading();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            } else {
                hideLoading();
                Toast.makeText(
                    SignInActivity.this,
                    "Sign in failed",
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAuthenticationOK(AuthResult authResult) {
        FirebaseUser firebaseUser = authResult.getUser();
        assert firebaseUser != null : "Firebase user is null!";
        Functions.cacheUID(this, firebaseUser.getUid());
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Functions.clearInputFocusWhenClickOutside(ev, this);
        return super.dispatchTouchEvent(ev);
    }
}