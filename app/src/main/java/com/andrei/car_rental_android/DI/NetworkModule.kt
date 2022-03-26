package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.engine.services.CarService
import com.andrei.car_rental_android.engine.services.LoginService
import com.andrei.car_rental_android.engine.services.RegisterService
import com.andrei.car_rental_android.engine.services.TokenService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

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
    @Singleton
    @Provides
    fun provideTokenService(retrofit: Retrofit):TokenService{
        return retrofit.create(TokenService::class.java)
    }

    @Singleton
    @Provides
    fun provideCarService(retrofit: Retrofit):CarService = retrofit.create(CarService::class.java)




}