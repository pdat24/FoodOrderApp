package com.firstapp.foodorderapp.adapter;

import static com.firstapp.foodorderapp.utils.Constants.FOOD_DETAIL;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_FINISH_TIME;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_NAME;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_PRICE;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_THUMBNAIL;
import static com.firstapp.foodorderapp.utils.Constants.FOOD_VOTE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.model.Food;
import com.firstapp.foodorderapp.ui.DetailActivity;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    List<Food> mResults;
    Context context;

    public SearchResultAdapter(List<Food> results) {
        mResults = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.view_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food result = mResults.get(position);
        holder.name.setText(result.getTitle());
        holder.container.setOnClickListener((v) -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(FOOD_DETAIL, result.getDescription());
            intent.putExtra(FOOD_PRICE, result.getPrice());
            intent.putExtra(FOOD_THUMBNAIL, result.getImagePath());
            intent.putExtra(FOOD_NAME, result.getTitle());
            intent.putExtra(FOOD_VOTE, result.getStar());
            intent.putExtra(FOOD_FINISH_TIME, result.getTimeId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        TextView name = itemView.findViewById(R.id.tvTitle);
        View container = itemView.findViewById(R.id.container);
    }
}
