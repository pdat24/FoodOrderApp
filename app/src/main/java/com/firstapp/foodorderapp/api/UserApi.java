package com.firstapp.foodorderapp.api;

import com.firstapp.foodorderapp.model.CartItem;
import com.firstapp.foodorderapp.model.Food;
import com.firstapp.foodorderapp.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApi {
    @FormUrlEncoded
    @POST("user/login/credentials")
    Call<User> signUserInWithEmailAndPassword(
        @Field("email") String email,
        @Field("password") String password
    );

    @FormUrlEncoded
    @POST("user/login/provider")
    Call<User> signUserInWithProvider(@Field("uid") String uid);

    @POST("user/signup")
    Call<User> createUser(@Body User user);

    @POST("user/{uid}/cart/add")
    Call<CartItem> addItemToCart(@Path("uid") String uid, @Body CartItem item);

    @GET("user/{uid}/cart/get")
    Call<List<CartItem>> getItemsInCart(@Path("uid") String uid);

    @FormUrlEncoded
    @PATCH("user/{uid}/cart/update-quality")
    Call<CartItem> updateItemsQuality(
        @Path("uid") String uid,
        @Field("title") String title,
        @Field("quality") int quality);

    @FormUrlEncoded
    @PATCH("user/{uid}/cart/remove")
    Call<CartItem> removeCartItem(
        @Path("uid") String uid,
        @Field("title") String title);
}
