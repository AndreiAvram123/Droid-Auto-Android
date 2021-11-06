package com.andrei.car_rental_android.DI

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    @Singleton
    @Provides
    @NetworkDispatcher
    fun provideNetworkDispatcher():CoroutineDispatcher = Dispatchers.IO

}

