package com.firstapp.foodorderapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.adapter.SearchResultAdapter;
import com.firstapp.foodorderapp.api.FoodApi;
import com.firstapp.foodorderapp.model.Food;
import com.firstapp.foodorderapp.utils.Functions;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Response;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity {
    RecyclerView rcvSearchSuggestions;
    TextInputEditText searchInput;
    InputMethodManager inputMethodManager;
    TextView noResults;
    @Inject
    FoodApi foodApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setStatusBarColor(getColor(R.color.white_bg));
        Functions.setStatusBarTextToDark(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        rcvSearchSuggestions = findViewById(R.id.rcvSearchSuggestions);
        searchInput = findViewById(R.id.ipSearch);
        noResults = findViewById(R.id.tvNoResult);

        rcvSearchSuggestions.setLayoutManager(new LinearLayoutManager(this));
        // focus on search input
        searchInput.requestFocus();
        inputMethodManager.showSoftInput(searchInput, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Functions.isInternetConnected(SearchActivity.this)) {
                    handleSearchMovie(s.toString().toLowerCase().trim());
                } else Functions.notifyNoInternetConnection(SearchActivity.this);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    void handleSearchMovie(String query) {
        if (!query.isEmpty())
            new Thread(() -> {
                Call<List<Food>> req = foodApi.searchFoodByKeyword(query);
                try {
                    Response<List<Food>> res = req.execute();
                    if (res.isSuccessful()) {
                        assert res.body() != null;
                        runOnUiThread(() -> {
                            List<Food> results = res.body();
                            if (results.isEmpty()) noResults.setVisibility(View.VISIBLE);
                            else noResults.setVisibility(View.GONE);
                            rcvSearchSuggestions.setAdapter(new SearchResultAdapter(results));
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        else noResults.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Functions.clearInputFocusWhenClickOutside(ev, this);
        return super.dispatchTouchEvent(ev);
    }
}