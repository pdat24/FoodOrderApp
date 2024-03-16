package com.firstapp.foodorderapp.utils;

import static com.firstapp.foodorderapp.utils.Constants.MAIN_SHARED_PREFERENCE;
import static com.firstapp.foodorderapp.utils.Constants.UID;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentContainerView;

import com.firstapp.foodorderapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Functions {
    static public void setStatusBarTextToDark(Activity activity) {
        WindowInsetsControllerCompat insetsAnimationCompat =
            ViewCompat.getWindowInsetsController(activity.getWindow().getDecorView());
        if (insetsAnimationCompat != null)
            insetsAnimationCompat.setAppearanceLightStatusBars(true);
    }

    static public boolean isInternetConnected(Context context) {
        ConnectivityManager manager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    static public void clearInputFocusWhenClickOutside(MotionEvent ev, Activity activity) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View focusedView = activity.getWindow().getCurrentFocus();
            if (focusedView instanceof AppCompatEditText) {
                Rect inputRect = new Rect();
                focusedView.getGlobalVisibleRect(inputRect);
                if (!inputRect.contains((int) ev.getX(), (int) ev.getY())) {
                    focusedView.clearFocus();
                    inputMethodManager.hideSoftInputFromWindow(
                        focusedView.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN
                    );
                }
            }
        }
    }

    static public void cacheUID(Context context, String uid) {
        context.getSharedPreferences(MAIN_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .edit().putString(UID, uid).apply();
    }

    static public void clearCacheUID(Context context) {
        context.getSharedPreferences(MAIN_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .edit().putString(UID, null).apply();
    }

    static public void changeAddStateToSuccess(Activity activity, FragmentContainerView loadingFrag) {
        activity.runOnUiThread(() -> {
            loadingFrag.findViewById(R.id.loadingIndicator).setVisibility(View.GONE);
            loadingFrag.findViewById(R.id.tvSuccessNotification).setVisibility(View.VISIBLE);
        });
    }

    static public void changeAddStateToLoading(Activity activity, FragmentContainerView loadingFrag) {
        activity.runOnUiThread(() -> {
            loadingFrag.findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
            loadingFrag.findViewById(R.id.tvSuccessNotification).setVisibility(View.GONE);
        });
    }

    static public void notifyNoInternetConnection(Context context) {
        View view =
            LayoutInflater.from(context).inflate(R.layout.view_no_connection, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(context).setView(view).create();
        view.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
