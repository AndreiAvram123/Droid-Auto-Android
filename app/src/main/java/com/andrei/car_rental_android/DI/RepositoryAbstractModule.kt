package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.configuration.RequestExecutorImpl
import com.andrei.car_rental_android.engine.repositories.*
import com.andrei.car_rental_android.state.LocalRepository
import com.andrei.car_rental_android.state.LocalRepositoryImpl
import com.andrei.car_rental_android.state.SessionManager
import com.andrei.car_rental_android.state.SessionManagerImpl
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
    abstract fun bindCarRepository(carRepositoryImpl: CarRepositoryImpl): CarRepository

    @Binds
    abstract fun bindLoginRepository(loginRepository: LoginRepositoryImpl): LoginRepository

    @Binds
    abstract fun bindRegisterRepository(registerRepository: RegisterRepositoryImpl): RegisterRepository

    @Binds
    abstract fun bindLocalRepository(localRepository: LocalRepositoryImpl):LocalRepository

    @Binds
    abstract fun bindSessionManager(sessionManagerImpl: SessionManagerImpl): SessionManager

}