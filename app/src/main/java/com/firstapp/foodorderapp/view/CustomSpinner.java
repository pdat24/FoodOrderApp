package com.firstapp.foodorderapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firstapp.foodorderapp.R;

public class CustomSpinner extends RelativeLayout {
    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_spinner, this);
        TypedArray array = getContext().getTheme().obtainStyledAttributes(
            attrs, R.styleable.CustomSpinner, 0, 0
        );
        try {
            String text = array.getString(R.styleable.CustomSpinner_text);
            int iconStart = array.getResourceId(R.styleable.CustomSpinner_iconStart, 0);
            ((TextView) findViewById(R.id.text)).setText(text);
            ((ImageView) findViewById(R.id.ivIconStart)).setImageResource(iconStart);
        } finally {
            array.recycle();
        }
    }
}
