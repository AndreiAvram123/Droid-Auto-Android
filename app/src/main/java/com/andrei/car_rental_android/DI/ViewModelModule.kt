package com.andrei.car_rental_android.DI

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineScope

@InstallIn(ViewModelComponent::class)
@Module
class ViewModelModule {
    @Provides
    fun provideCoroutineScope(): CoroutineScope? {
        return null
    }

}