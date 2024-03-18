package com.firstapp.foodorderapp.adapter;

import static com.firstapp.foodorderapp.utils.Constants.FOOD_DETAIL;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_FINISH_TIME;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_NAME;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_PRICE;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_THUMBNAIL;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_VOTE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.model.Food;
import com.firstapp.foodorderapp.ui.DetailActivity;
import com.firstapp.foodorderapp.utils.Constants;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    Context context;
    List<Food> foods;

    public FoodAdapter(List<Food> foods) {
        this.foods = foods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.view_food, parent, false)
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foods.get(position);
        holder.name.setText(food.getTitle());
        holder.price.setText(food.getPrice() + "$");
        holder.voteRate.setText(String.valueOf(food.getStar()));
        holder.time.setText(Constants.timeRangeTable.get(food.getTimeId()));
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
        View container = itemView.findViewById(R.id.container);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
