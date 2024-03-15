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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.api.UserApi;
import com.firstapp.foodorderapp.model.CartItem;
import com.firstapp.foodorderapp.ui.DetailActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    Context context;
    UserApi userApi;
    Activity activity;
    List<CartItem> cartItems;
    List<Integer> tmp = new ArrayList<>();
    MutableLiveData<Double> subTotalPriceUpdate;
    String uid;

    public CartItemAdapter(
        Activity activity,
        MutableLiveData<Double> subTotalPriceUpdate,
        UserApi userApi, String uid, List<CartItem> cartItems) {
        this.uid = uid;
        this.subTotalPriceUpdate = subTotalPriceUpdate;
        this.activity = activity;
        this.userApi = userApi;
        this.cartItems = cartItems;
        for (int i = 0; i < cartItems.size(); ++i)
            tmp.add(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.view_cart_item, parent, false)
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.name.setText(item.getTitle());
        holder.quality.setText(String.valueOf(item.getAmount()));
        holder.pricePerOne.setText(item.getPrice() + "$");
        holder.totalPrice.setText(item.getPrice() * item.getAmount() + "$");
        Glide.with(context).asDrawable().load(item.getImagePath()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(
                @NonNull Drawable resource,
                @Nullable Transition<? super Drawable> transition) {
                holder.thumbnail.setImageDrawable(resource);
            }
        });
        holder.thumbnail.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(FOOD_DETAIL, item.getDescription());
            intent.putExtra(FOOD_PRICE, item.getPrice());
            intent.putExtra(FOOD_THUMBNAIL, item.getImagePath());
            intent.putExtra(FOOD_NAME, item.getTitle());
            intent.putExtra(FOOD_VOTE, item.getStar());
            intent.putExtra(FOOD_FINISH_TIME, item.getTimeId());
            context.startActivity(intent);
        });
        tmp.set(position, item.getAmount());
        holder.addQualityBtn.setOnClickListener(v -> {
            tmp.set(position, tmp.get(position) + 1);
            subTotalPriceUpdate.postValue(item.getPrice());
            holder.totalPrice.setText("$" + tmp.get(position) * item.getPrice());
            updateCartItemQuality(holder, item.getTitle(), tmp.get(position));
        });
        holder.subQualityBtn.setOnClickListener(v -> {
            tmp.set(position, tmp.get(position) - 1);
            subTotalPriceUpdate.postValue(-item.getPrice());
            holder.totalPrice.setText("$" + tmp.get(position) * item.getPrice());
            updateCartItemQuality(holder, item.getTitle(), tmp.get(position));
        });
    }

    private void updateCartItemQuality(ViewHolder holder, String title, int quality) {
        if (quality > 0)
            new Thread(() -> {
                activity.runOnUiThread(() -> holder.quality.setText(String.valueOf(quality)));
                try {
                    userApi.updateItemsQuality(uid, title, quality).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail = itemView.findViewById(R.id.ivThumbnail);
        TextView name = itemView.findViewById(R.id.tvName);
        TextView totalPrice = itemView.findViewById(R.id.tvTotalPrice);
        TextView pricePerOne = itemView.findViewById(R.id.tvPricePerOne);
        TextView quality = itemView.findViewById(R.id.tvQuality);
        ImageView subQualityBtn = itemView.findViewById(R.id.btnSubQuality);
        ImageView addQualityBtn = itemView.findViewById(R.id.btnAddQuality);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
