package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.BuildConfig
import com.andrei.car_rental_android.engine.configuration.AuthTokenInterceptor
import com.andrei.car_rental_android.engine.configuration.RefreshTokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
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
            .callTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10))
            .addNetworkInterceptor(authTokenInterceptor)
            .authenticator(refreshTokenAuthenticator)
            .build()

    }



    @Singleton
    @Provides
    fun provideRetrofit(
        httpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}