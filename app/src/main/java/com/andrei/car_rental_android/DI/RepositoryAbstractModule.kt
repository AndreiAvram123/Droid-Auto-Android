package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.engine.CarRepository
import com.andrei.car_rental_android.engine.CarRepositoryImpl
import com.andrei.car_rental_android.engine.LoginRepository
import com.andrei.car_rental_android.engine.LoginRepositoryImpl
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

    @Binds
    abstract fun bindLoginRepository(loginRepository: LoginRepositoryImpl):LoginRepository
}