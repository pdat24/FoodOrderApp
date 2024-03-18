package com.firstapp.foodorderapp.ui;

import static com.firstapp.foodorderapp.utils.Constants.FOOD_DETAIL;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_FINISH_TIME;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_NAME;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_PRICE;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_THUMBNAIL;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_VOTE;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.api.UserApi;
import com.firstapp.foodorderapp.model.CartItem;
import com.firstapp.foodorderapp.utils.Constants;
import com.firstapp.foodorderapp.utils.Functions;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;

@AndroidEntryPoint
public class DetailActivity extends AppCompatActivity {
    @Inject
    UserApi userApi;
    @Inject
    String uid;
    ImageView thumbnail;
    TextView name;
    TextView voteRate;
    TextView priceView;
    TextView totalPrice;
    TextView time;
    TextView details;
    TextView qualityView;
    int quality = 1;
    double price;
    String imagePath;
    double star;
    String title;
    String description;
    FragmentContainerView loadingFrag;
    int timeId;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().setStatusBarColor(getColor(R.color.text));
        // get views
        name = findViewById(R.id.name);
        voteRate = findViewById(R.id.tvVoteRate);
        thumbnail = findViewById(R.id.ivThumbnail);
        priceView = findViewById(R.id.tvPrice);
        qualityView = findViewById(R.id.tvQuality);
        totalPrice = findViewById(R.id.tvTotalPrice);
        time = findViewById(R.id.tvTime);
        details = findViewById(R.id.tvDetail);
        loadingFrag = findViewById(R.id.fragLoading);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        price = extras.getDouble(FOOD_PRICE, 0);
        title = extras.getString(FOOD_NAME);
        star = extras.getDouble(FOOD_VOTE);
        timeId = extras.getInt(FOOD_FINISH_TIME);
        imagePath = extras.getString(FOOD_THUMBNAIL);
        description = extras.getString(FOOD_DETAIL);
        name.setText(title);
        details.setText(description);
        voteRate.setText(star + " Rating");
        priceView.setText("$" + price);
        time.setText(Constants.timeRangeTable.get(timeId));
        Glide.with(thumbnail).asDrawable()
            .load(imagePath).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(
                    @NonNull Drawable resource,
                    @Nullable Transition<? super Drawable> transition) {
                    thumbnail.setImageDrawable(resource);
                }
            });
        totalPrice.setText("$" + price);
    }

    public void addToCart(View view) {
        if (Functions.isInternetConnected(this)) {
            loadingFrag.setVisibility(View.VISIBLE);
            new Thread(() -> {
                Call<CartItem> req = userApi.addItemToCart(
                    uid, new CartItem(
                        description, price, star, imagePath, timeId, title, quality));
                try {
                    if (req.execute().isSuccessful()) {
                        Functions.changeAddStateToSuccess(DetailActivity.this, loadingFrag);
                        SystemClock.sleep(500);
                        runOnUiThread(() -> loadingFrag.setVisibility(View.GONE));
                        Functions.changeAddStateToLoading(DetailActivity.this, loadingFrag);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } else Functions.notifyNoInternetConnection(this);
    }

    @SuppressLint("SetTextI18n")
    public void addQuality(View view) {
        quality++;
        qualityView.setText(String.valueOf(quality));
        totalPrice.setText("$" + quality * price);
    }

    @SuppressLint("SetTextI18n")
    public void subtractQuality(View view) {
        if (quality > 1) {
            quality--;
            qualityView.setText(String.valueOf(quality));
            totalPrice.setText("$" + quality * price);
        }
    }
}