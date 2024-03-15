package com.firstapp.foodorderapp.view;

import static com.firstapp.foodorderapp.utils.Constants.CATEGORY_ID;
import static com.firstapp.foodorderapp.utils.Constants.CATEGORY_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.ui.FoodCategoryActivity;

public class CategoryItem extends FrameLayout {
    public CategoryItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public CategoryItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_category, this);
        TypedArray ta = getContext().getTheme().obtainStyledAttributes(
            attrs, R.styleable.CategoryItem, 0, 0
        );
        try {
            String label = ta.getString(R.styleable.CategoryItem_label);
            int categoryId = ta.getInt(R.styleable.CategoryItem_category_id, 0);
            int thumbnail = ta.getResourceId(R.styleable.CategoryItem_thumbnail, 0);
            int thumbnailBg = ta.getColor(R.styleable.CategoryItem_thumbnail_background, 0);
            ((TextView) findViewById(R.id.tvLabel)).setText(label);
            ImageView thumbnailView = findViewById(R.id.ivThumbnail);
            thumbnailView.setImageResource(thumbnail);
            thumbnailView.setBackgroundColor(thumbnailBg);
            findViewById(R.id.container).setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), FoodCategoryActivity.class);
                intent.putExtra(CATEGORY_NAME, label);
                intent.putExtra(CATEGORY_ID, categoryId);
                getContext().startActivity(intent);
            });
        } finally {
            ta.recycle();
        }
    }
}
