package com.firstapp.foodorderapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.firstapp.foodorderapp.R;

public class AddedToCartNotificationFragment extends Fragment {
    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(
            R.layout.fragment_added_to_cart_notification, container, false);
    }
}