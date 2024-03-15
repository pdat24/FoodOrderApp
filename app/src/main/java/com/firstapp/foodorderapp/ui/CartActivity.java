package com.firstapp.foodorderapp.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.foodorderapp.R;
import com.firstapp.foodorderapp.adapter.CartItemAdapter;
import com.firstapp.foodorderapp.api.UserApi;
import com.firstapp.foodorderapp.model.CartItem;
import com.firstapp.foodorderapp.utils.Functions;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Response;

@AndroidEntryPoint
public class CartActivity extends AppCompatActivity {
    RecyclerView rcvItem;
    TextView emptyCart;
    TextView deliveryFeeView;
    TextView subTotal;
    TextView totalPriceView;
    CircularProgressIndicator loading;
    @Inject
    UserApi userApi;
    @Inject
    String uid;
    int deliveryFee = 5;
    double totalPriceValue;
    CartItemAdapter cartItemAdapter;
    List<CartItem> currentItemList;
    DecimalFormat decimalFormat = new DecimalFormat("##.00");
    MutableLiveData<Double> subTotalPriceUpdates = new MutableLiveData<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getWindow().setStatusBarColor(getColor(R.color.white_bg));
        Functions.setStatusBarTextToDark(this);

        rcvItem = findViewById(R.id.rcvItem);
        subTotal = findViewById(R.id.tvSubtotal);
        totalPriceView = findViewById(R.id.tvTotal);
        deliveryFeeView = findViewById(R.id.tvDeliveryFee);
        emptyCart = findViewById(R.id.tvEmpty);
        loading = findViewById(R.id.loading);
        rcvItem.setLayoutManager(new LinearLayoutManager(this));

        // observe total price change
        subTotalPriceUpdates.observe(this, v -> {
            totalPriceValue += v;
            subTotal.setText("$" + decimalFormat.format(totalPriceValue));
            totalPriceView.setText("$" + decimalFormat.format(totalPriceValue + deliveryFee));
        });
        fetchData();
        attachSwipeToDeleteAction();
    }

    private void attachSwipeToDeleteAction() {
        ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            @Override
            public boolean onMove(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder,
                @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new Thread(() -> {
                    try {
                        String title =
                            currentItemList.get(viewHolder.getAdapterPosition()).getTitle();
                        currentItemList.removeIf(i -> i.getTitle().equals(title));
                        runOnUiThread(() ->
                            rcvItem.setAdapter(cartItemAdapter)
                        );
                        userApi.removeCartItem(uid, title).execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        };
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rcvItem);
    }

    private void fetchData() {
        new Thread(() -> {
            Call<List<CartItem>> req = userApi.getItemsInCart(uid);
            try {
                Response<List<CartItem>> res = req.execute();
                if (res.isSuccessful()) {
                    List<CartItem> items = res.body();
                    currentItemList = items;
                    runOnUiThread(() -> {
                        assert items != null;
                        if (items.isEmpty())
                            emptyCart.setVisibility(View.VISIBLE);
                        else {
                            cartItemAdapter =
                                new CartItemAdapter(this, subTotalPriceUpdates, userApi, uid, items);
                            rcvItem.setAdapter(cartItemAdapter);
                            emptyCart.setVisibility(View.GONE);
                        }
                        loading.setVisibility(View.GONE);
                        renderSubtotalPrice(items);
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void renderSubtotalPrice(List<CartItem> items) {
        double sum = 0;
        for (CartItem i : items) {
            sum += i.getPrice();
        }
        subTotal.setText("$" + sum);
        if (sum != 0) {
            totalPriceValue = sum + deliveryFee;
            totalPriceView.setText("$" + totalPriceValue);
        } else deliveryFeeView.setText("$0.0");
    }

    public void placeOrder(View v) {

    }

    public void back(View v) {
        finish();
    }
}