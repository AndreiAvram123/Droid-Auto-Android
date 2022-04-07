package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.engine.services.*
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
    fun providePaymentService(retrofit: Retrofit):PaymentService{
        return retrofit.create(PaymentService::class.java)
    }

    @Singleton
    @Provides
    fun provideCarService(retrofit: Retrofit):CarService = retrofit.create(CarService::class.java)

    @Singleton
    @Provides
    fun provideDirectionsService(retrofit: Retrofit):DirectionsService = retrofit.create(DirectionsService::class.java)

    @Singleton
    @Provides
    fun provideReservationsService(retrofit: Retrofit):ReservationService = retrofit.create(ReservationService::class.java)


}