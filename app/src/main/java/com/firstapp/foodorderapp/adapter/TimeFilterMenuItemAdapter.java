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

public class TimeFilterMenuItemAdapter extends ArrayAdapter<String> {
    List<String> items;
    int itemView;
    MutableLiveData<Integer> timeFilterUpdate;

    public TimeFilterMenuItemAdapter(
        @NonNull Context context, int resource, List<String> items,
        MutableLiveData<Integer> timeFilterUpdate) {
        super(context, resource, items);
        this.items = items;
        itemView = resource;
        this.timeFilterUpdate = timeFilterUpdate;
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
        if (timeFilterUpdate.getValue() != null && timeFilterUpdate.getValue() == position)
            itemBgColorId = R.color.white;
        else itemBgColorId = android.R.color.transparent;
        textView.setBackgroundTintList(
            ColorStateList.valueOf(getContext().getColor(itemBgColorId)));
        /// handle click event
        view.setOnClickListener(v -> {
            if (timeFilterUpdate.getValue() == null || timeFilterUpdate.getValue() != position) {
                textView.setBackgroundTintList(
                    ColorStateList.valueOf(getContext().getColor(R.color.white)));
                timeFilterUpdate.postValue(position);
            } else if (timeFilterUpdate.getValue() == position) {
                textView.setBackgroundTintList(
                    ColorStateList.valueOf(getContext().getColor(android.R.color.transparent)));
                timeFilterUpdate.postValue(UN_FILTER_CODE);
            }
        });
        return view;
    }
}
