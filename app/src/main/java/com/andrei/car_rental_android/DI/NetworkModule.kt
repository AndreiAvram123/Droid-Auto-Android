package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.BuildConfig
import com.andrei.car_rental_android.engine.services.LoginService
import com.andrei.car_rental_android.engine.services.RegisterService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit():Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideLoginService(retrofit: Retrofit):LoginService{
        return retrofit.create(LoginService::class.java)
    }
    @Singleton
    @Provides
    fun provideRegisterService(retrofit: Retrofit):RegisterService{
        return retrofit.create(RegisterService::class.java)
    }

}