package com.firstapp.foodorderapp.ui;

import static com.firstapp.foodorderapp.utils.Constants.UN_FILTER_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListPopupWindow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.adapter.PriceFilterMenuItemAdapter;
import com.firstapp.foodorderapp.adapter.SuggestedFoodDaily;
import com.firstapp.foodorderapp.adapter.TimeFilterMenuItemAdapter;
import com.firstapp.foodorderapp.api.FoodApi;
import com.firstapp.foodorderapp.api.UserApi;
import com.firstapp.foodorderapp.model.Food;
import com.firstapp.foodorderapp.utils.Constants;
import com.firstapp.foodorderapp.utils.Functions;
import com.firstapp.foodorderapp.view.CustomSpinner;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Response;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    FragmentContainerView loadingFrag;
    RecyclerView rcvBestFood;
    View basketButton;
    View bestFoodPlaceholder;
    CustomSpinner timeFilterView;
    CustomSpinner priceFilterView;
    ListPopupWindow timeFilterPopup;
    ListPopupWindow priceFilterPopup;
    @Inject
    FirebaseAuth firebaseAuth;
    @Inject
    String uid;
    @Inject
    FoodApi foodApi;
    @Inject
    UserApi userApi;
    MutableLiveData<Boolean> bestFoodIsLoaded = new MutableLiveData<>(false);
    List<String> timeRangeValues = new ArrayList<>();
    List<String> priceRangeValues = new ArrayList<>();
    MutableLiveData<Integer> timeFilterUpdate = new MutableLiveData<>();
    MutableLiveData<List<Food>> showingBestFoods = new MutableLiveData<>();
    MutableLiveData<Integer> priceFilterUpdate = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getColor(R.color.white_bg));
        Functions.setStatusBarTextToDark(this);

        // get views
        basketButton = findViewById(R.id.btnBasket);
        loadingFrag = findViewById(R.id.fragLoading);
        rcvBestFood = findViewById(R.id.rcvTodayBestFood);
        timeFilterView = findViewById(R.id.timeFilter);
        priceFilterView = findViewById(R.id.priceFilter);
        bestFoodPlaceholder = findViewById(R.id.bestFoodPlaceHolder);

        timeFilterPopup = new ListPopupWindow(this);
        priceFilterPopup = new ListPopupWindow(this);
        rcvBestFood.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        renderBestFood();

        // add event handlers
        basketButton.setOnClickListener(
            v -> startActivity(new Intent(this, CartActivity.class))
        );
        bestFoodIsLoaded.observe(this, isLoaded -> {
            if (isLoaded)
                bestFoodPlaceholder.setVisibility(View.GONE);
        });
        timeFilterView.setOnClickListener(v -> showTimeFilterPopupMenu());
        priceFilterView.setOnClickListener(v -> showPriceFilterPopupMenu());
        timeRangeValues.addAll(Constants.timeRangeTable.values());
        priceRangeValues.addAll(Constants.priceRangeTable.values());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // observe best foods changes
        showingBestFoods.observe(this, foods -> {
            rcvBestFood.setAdapter(new SuggestedFoodDaily(
                this, loadingFrag, bestFoodIsLoaded, foods, userApi, uid));
        });
        observeTimeFilterChanged();
        observePriceFilterChanged();
    }

    private void observePriceFilterChanged() {
        priceFilterUpdate.observe(this, priceRangeId -> {
            if (priceRangeId != UN_FILTER_CODE)
                priceFilterView.setText(Constants.priceRangeTable.get(priceRangeId));
            else
                priceFilterView.setText(getString(R.string.price));
            priceFilterPopup.dismiss();
            new Thread(() -> {
                Call<List<Food>> req = foodApi.getBestFoods();
                try {
                    Response<List<Food>> res = req.execute();
                    if (res.isSuccessful()) {
                        assert res.body() != null : "Response data is null!";
                        showingBestFoods.postValue(
                            res.body().stream().filter(v -> {
                                double price = v.getPrice();
                                if (priceRangeId == 0)
                                    return 1 <= price && price < 10;
                                else if (priceRangeId == 1)
                                    return 10 <= price && price < 30;
                                else if (priceRangeId == 2)
                                    return price >= 30;
                                return true;
                            }).collect(Collectors.toList())
                        );
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });
    }

    private void observeTimeFilterChanged() {
        timeFilterUpdate.observe(this, timeId -> {
            if (timeId != UN_FILTER_CODE) {
                timeFilterView.setText(Constants.timeRangeTable.get(timeId));
                List<Food> tmp = showingBestFoods.getValue();
                if (tmp != null) {
                    showingBestFoods.postValue(
                        tmp.stream().filter(v ->
                            v.getTimeId() == timeId).collect(Collectors.toList()));
                }
            } else {
                new Thread(() -> {
                    Call<List<Food>> req = foodApi.getBestFoods();
                    try {
                        Response<List<Food>> res = req.execute();
                        if (res.isSuccessful()) {
                            assert res.body() != null : "Response data is null!";
                            showingBestFoods.postValue(res.body());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                timeFilterView.setText(getString(R.string.time));
            }
            timeFilterPopup.dismiss();
        });
    }

    private void showTimeFilterPopupMenu() {
        timeFilterPopup.setAnchorView(timeFilterView);
        timeFilterPopup.setBackgroundDrawable(
            AppCompatResources.getDrawable(this, R.drawable.bg_sign_out_dialog)
        );
        timeFilterPopup.setDropDownGravity(Gravity.START);
        timeFilterPopup.setAdapter(new TimeFilterMenuItemAdapter(
            this, R.layout.view_time_filter_option, timeRangeValues, timeFilterUpdate));
        timeFilterPopup.show();
    }

    private void showPriceFilterPopupMenu() {
        priceFilterPopup.setAnchorView(priceFilterView);
        priceFilterPopup.setBackgroundDrawable(
            AppCompatResources.getDrawable(this, R.drawable.bg_sign_out_dialog)
        );
        priceFilterPopup.setDropDownGravity(Gravity.START);
        priceFilterPopup.setAdapter(new PriceFilterMenuItemAdapter(
            this, R.layout.view_time_filter_option, priceRangeValues, priceFilterUpdate));
        priceFilterPopup.show();
    }

    private void renderBestFood() {
        new Thread(() -> {
            Call<List<Food>> req = foodApi.getBestFoods();
            try {
                Response<List<Food>> res = req.execute();
                if (res.isSuccessful())
                    showingBestFoods.postValue(res.body());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void signOut(View v) {
        View view = LayoutInflater.from(this).inflate(
            R.layout.view_sign_out_dialog, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
            .setView(view).create();
        view.findViewById(R.id.btnCancel).setOnClickListener(btn -> dialog.dismiss());
        view.findViewById(R.id.btnConfirm).setOnClickListener(btn -> {
            firebaseAuth.signOut();
            Functions.clearCacheUID(MainActivity.this);
            dialog.dismiss();
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
            finish();
        });
        dialog.show();
    }

    public void toSearchActivity(View v) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Functions.clearInputFocusWhenClickOutside(ev, this);
        return super.dispatchTouchEvent(ev);
    }
}