package com.firstapp.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.adapter.SuggestedFoodDaily;
import com.firstapp.foodorderapp.api.FoodApi;
import com.firstapp.foodorderapp.api.UserApi;
import com.firstapp.foodorderapp.model.Food;
import com.firstapp.foodorderapp.utils.Functions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Response;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    RecyclerView rcvBestFood;
    View basketButton;
    TextInputEditText searchInput;
    View bestFoodPlaceholder;
    @Inject
    FirebaseAuth firebaseAuth;
    @Inject
    String uid;
    @Inject
    FoodApi foodApi;
    @Inject
    UserApi userApi;
    MutableLiveData<Boolean> bestFoodIsLoaded = new MutableLiveData<>(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getColor(R.color.white_bg));
        Functions.setStatusBarTextToDark(this);

        // get views
        basketButton = findViewById(R.id.btnBasket);
        searchInput = findViewById(R.id.ipSearch);
        rcvBestFood = findViewById(R.id.rcvTodayBestFood);
        bestFoodPlaceholder = findViewById(R.id.bestFoodPlaceHolder);

        rcvBestFood.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        renderBestFood();

        // add event handlers
        basketButton.setOnClickListener(
            v -> startActivity(new Intent(this, CartActivity.class))
        );
        searchInput.setOnClickListener(v -> {
            searchInput.setGravity(Gravity.START);
        });
        bestFoodIsLoaded.observe(this, isLoaded -> {
            if (isLoaded) {
                bestFoodPlaceholder.setVisibility(View.GONE);
            }
        });
    }

    private void renderBestFood() {
        new Thread(() -> {
            Call<List<Food>> req = foodApi.getBestFoods();
            try {
                Response<List<Food>> res = req.execute();
                if (res.isSuccessful()) {
                    runOnUiThread(() -> rcvBestFood.setAdapter(new SuggestedFoodDaily(
                        MainActivity.this,
                        bestFoodIsLoaded, res.body(), userApi, uid
                    )));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void signOut(View view) {
        firebaseAuth.signOut();
        Functions.clearCacheUID(this);
        startActivity(new Intent(this, IntroActivity.class));
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Functions.clearInputFocusWhenClickOutside(ev, this);
        return super.dispatchTouchEvent(ev);
    }
}