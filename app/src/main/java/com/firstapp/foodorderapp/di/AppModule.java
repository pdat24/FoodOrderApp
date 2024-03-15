package com.firstapp.foodorderapp.di;

import com.firstapp.foodorderapp.api.FoodApi;
import com.firstapp.foodorderapp.api.UserApi;
import com.firstapp.foodorderapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    UserApi provideUserApi() {
        return new Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(UserApi.class);
    }

    @Provides
    @Singleton
    FoodApi provideFoodApi() {
        return new Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(FoodApi.class);
    }

    @Provides
    @Singleton
    FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    String provideCurrentUserUID(
        FirebaseAuth firebaseAuth
    ) {
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }
}
