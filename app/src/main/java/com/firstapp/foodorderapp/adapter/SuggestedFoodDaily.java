package com.firstapp.foodorderapp.adapter;

import static com.firstapp.foodorderapp.utils.Constants.FOOD_DETAIL;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_FINISH_TIME;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_NAME;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_PRICE;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_THUMBNAIL;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_VOTE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.api.UserApi;
import com.firstapp.foodorderapp.model.CartItem;
import com.firstapp.foodorderapp.model.Food;
import com.firstapp.foodorderapp.ui.DetailActivity;
import com.firstapp.foodorderapp.utils.Constants;
import com.firstapp.foodorderapp.utils.Functions;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

public class SuggestedFoodDaily extends RecyclerView.Adapter<SuggestedFoodDaily.ViewHolder> {
    List<Food> foods;
    MutableLiveData<Boolean> loadComplete;
    FragmentContainerView loadingFrag;
    UserApi userApi;
    String uid;
    Activity activity;
    Context context;

    public SuggestedFoodDaily(
        Activity activity,
        FragmentContainerView loadingFrag,
        MutableLiveData<Boolean> loadComplete,
        List<Food> foods, UserApi userApi, String uid) {
        this.activity = activity;
        this.loadingFrag = loadingFrag;
        this.loadComplete = loadComplete;
        this.foods = foods;
        this.userApi = userApi;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.view_suggested_food, parent, false)
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foods.get(position);
        holder.name.setText(food.getTitle());
        holder.price.setText(food.getPrice() + "$");
        holder.voteRate.setText(String.valueOf(food.getStar()));
        holder.time.setText(Constants.timeTable.get(food.getTimeId()));
        Glide.with(context).asDrawable().load(food.getImagePath()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(
                @NonNull Drawable resource,
                @Nullable Transition<? super Drawable> transition) {
                holder.thumbnail.setImageDrawable(resource);
            }
        });
        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(FOOD_DETAIL, food.getDescription());
            intent.putExtra(FOOD_PRICE, food.getPrice());
            intent.putExtra(FOOD_THUMBNAIL, food.getImagePath());
            intent.putExtra(FOOD_NAME, food.getTitle());
            intent.putExtra(FOOD_VOTE, food.getStar());
            intent.putExtra(FOOD_FINISH_TIME, food.getTimeId());
            context.startActivity(intent);
        });
        holder.addToCartBtn.setOnClickListener(v -> {
            if (Functions.isInternetConnected(context))
                addToCart(food);
            else Functions.notifyNoInternetConnection(context);
        });
    }

    private void addToCart(Food food) {
        loadingFrag.setVisibility(View.VISIBLE);
        new Thread(() -> {
            Call<CartItem> req = userApi.addItemToCart(uid, new CartItem(
                food.getDescription(),
                food.getPrice(),
                food.getStar(),
                food.getImagePath(),
                food.getTimeId(),
                food.getTitle(),
                1));
            try {
                if (req.execute().isSuccessful()) {
                    Functions.changeAddStateToSuccess(activity, loadingFrag);
                    SystemClock.sleep(500);
                    activity.runOnUiThread(() -> loadingFrag.setVisibility(View.GONE));
                    Functions.changeAddStateToLoading(activity, loadingFrag);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        loadComplete.postValue(true);
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail = itemView.findViewById(R.id.ivThumbnail);
        TextView name = itemView.findViewById(R.id.tvName);
        TextView voteRate = itemView.findViewById(R.id.tvVoteRate);
        TextView time = itemView.findViewById(R.id.tvTime);
        TextView price = itemView.findViewById(R.id.tvPrice);
        View addToCartBtn = itemView.findViewById(R.id.btnAddToCart);
        View container = itemView.findViewById(R.id.container);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
