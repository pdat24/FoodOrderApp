package com.firstapp.foodorderapp.adapter;

import static com.firstapp.foodorderapp.utils.Constants.UN_FILTER_CODE;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.firstapp.foodorderapp.R;

import java.util.List;

public class PriceFilterMenuItemAdapter extends ArrayAdapter<String> {
    List<String> items;
    int itemView;
    MutableLiveData<Integer> priceFilterUpdate;

    public PriceFilterMenuItemAdapter(
        @NonNull Context context, int resource, List<String> items,
        MutableLiveData<Integer> priceFilterUpdate) {
        super(context, resource, items);
        this.items = items;
        itemView = resource;
        this.priceFilterUpdate = priceFilterUpdate;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView != null) view = convertView;
        else view = LayoutInflater.from(parent.getContext()).inflate(itemView, parent, false);
        TextView textView = view.findViewById(R.id.tvContent);
        // change item background if it's checked
        int itemBgColorId;
        textView.setText(items.get(position));
        if (priceFilterUpdate.getValue() != null && priceFilterUpdate.getValue() == position)
            itemBgColorId = R.color.white;
        else itemBgColorId = android.R.color.transparent;
        textView.setBackgroundTintList(
            ColorStateList.valueOf(getContext().getColor(itemBgColorId)));
        /// handle click event
        view.setOnClickListener(v -> {
            if (priceFilterUpdate.getValue() == null || priceFilterUpdate.getValue() != position) {
                textView.setBackgroundTintList(
                    ColorStateList.valueOf(getContext().getColor(R.color.white)));
                priceFilterUpdate.postValue(position);
            } else if (priceFilterUpdate.getValue() == position) {
                textView.setBackgroundTintList(
                    ColorStateList.valueOf(getContext().getColor(android.R.color.transparent)));
                priceFilterUpdate.postValue(UN_FILTER_CODE);
            }
        });
        return view;
    }
}
