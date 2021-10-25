package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.engine.CarRepository
import com.andrei.car_rental_android.engine.CarRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryAbstractModule {
    @Binds
    abstract fun bindCarRepository(carRepositoryImpl: CarRepositoryImpl):CarRepository
}