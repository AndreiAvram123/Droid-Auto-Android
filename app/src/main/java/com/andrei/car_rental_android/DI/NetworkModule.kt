package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.BuildConfig
import com.andrei.car_rental_android.engine.configuration.AuthTokenInterceptor
import com.andrei.car_rental_android.engine.configuration.RefreshTokenAuthenticator
import com.andrei.car_rental_android.engine.services.LoginService
import com.andrei.car_rental_android.engine.services.RegisterService
import com.andrei.car_rental_android.engine.services.TokenService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {



    @Singleton
    @Provides
    fun provideRetrofit(
        httpClient: OkHttpClient
    ):Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
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
    @Singleton
    @Provides
    fun provideTokenService(retrofit: Retrofit):TokenService{
        return retrofit.create(TokenService::class.java)
    }

    @Singleton
    @Provides
    fun provideHttpClient(
        refreshTokenAuthenticator: RefreshTokenAuthenticator,
        authTokenInterceptor: AuthTokenInterceptor
    ):OkHttpClient{
        return OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10))
            .addNetworkInterceptor(authTokenInterceptor)
            .authenticator(refreshTokenAuthenticator)
            .build()

    }


}