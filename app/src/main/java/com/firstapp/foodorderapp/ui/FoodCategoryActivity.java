package com.firstapp.foodorderapp.ui;

import static com.firstapp.foodorderapp.utils.Constants.CATEGORY_ID;
import static com.firstapp.foodorderapp.utils.Constants.CATEGORY_NAME;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.adapter.FoodAdapter;
import com.firstapp.foodorderapp.api.FoodApi;
import com.firstapp.foodorderapp.model.Food;
import com.firstapp.foodorderapp.utils.Functions;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Response;

@AndroidEntryPoint
public class FoodCategoryActivity extends AppCompatActivity {
    TextView title;
    RecyclerView rcvItem;
    CircularProgressIndicator loading;
    @Inject
    FoodApi foodApi;
    int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_category);
        getWindow().setStatusBarColor(getColor(R.color.white_bg));
        Functions.setStatusBarTextToDark(this);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        categoryId = extras.getInt(CATEGORY_ID, 0);
        title = findViewById(R.id.title);
        loading = findViewById(R.id.loading);
        rcvItem = findViewById(R.id.rcvItem);
        rcvItem.setLayoutManager(
            new GridLayoutManager(this, 2)
        );
        title.setText(extras.getString(CATEGORY_NAME));
        fetchData();
    }

    private void fetchData() {
        new Thread(() -> {
            Call<List<Food>> req = foodApi.getFoodsByCategory(categoryId);
            try {
                Response<List<Food>> res = req.execute();
                if (res.isSuccessful()) {
                    runOnUiThread(() -> {
                        rcvItem.setAdapter(new FoodAdapter(res.body()));
                        loading.setVisibility(View.GONE);
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void back(View view) {
        finish();
    }
}