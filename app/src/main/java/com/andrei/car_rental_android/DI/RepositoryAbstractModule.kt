package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.engine.CarRepository
import com.andrei.car_rental_android.engine.CarRepositoryImpl
import com.andrei.car_rental_android.engine.LoginRepository
import com.andrei.car_rental_android.engine.LoginRepositoryImpl
import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.configuration.RequestExecutorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryAbstractModule {

    @Binds
    abstract fun bindRequestExecutor(requestExecutorImpl: RequestExecutorImpl):RequestExecutor

    @Binds
    abstract fun bindCarRepository(carRepositoryImpl: CarRepositoryImpl):CarRepository

    @Binds
    abstract fun bindLoginRepository(loginRepository: LoginRepositoryImpl):LoginRepository
}