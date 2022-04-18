package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.BuildConfig
import com.andrei.car_rental_android.engine.configuration.AuthTokenInterceptor
import com.andrei.car_rental_android.engine.configuration.RefreshTokenAuthenticator
import com.andrei.car_rental_android.engine.converters.LocalDateTimeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RetrofitComponentsModule {

    @Singleton
    @Provides
    fun provideHttpClient(
        refreshTokenAuthenticator: RefreshTokenAuthenticator,
        authTokenInterceptor: AuthTokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(15))
            .readTimeout(Duration.ofSeconds(15))
            .addNetworkInterceptor(authTokenInterceptor)
            .authenticator(refreshTokenAuthenticator)
            .build()

    }



    @Singleton
    @Provides
    fun provideGson():Gson = GsonBuilder().
                 registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter()).create()

    @Singleton
    @Provides
    fun provideRetrofit(
        httpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}