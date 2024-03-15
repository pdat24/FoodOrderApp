package com.firstapp.foodorderapp.api;

import com.firstapp.foodorderapp.model.Food;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FoodApi {
    @GET("food/best")
    Call<List<Food>> getBestFoods();

    @GET("food/{id}")
    Call<Food> getFoodById(@Path("id") String id);

    @GET("food/category/{id}")
    Call<List<Food>> getFoodsByCategory(@Path("id") int id);
}
