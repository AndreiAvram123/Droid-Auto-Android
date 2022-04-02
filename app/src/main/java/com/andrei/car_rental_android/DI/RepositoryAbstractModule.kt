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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryAbstractModule {

    @Binds
    @Singleton
    abstract fun bindRequestExecutor(requestExecutorImpl: RequestExecutorImpl):RequestExecutor

    @Binds
    @Singleton
    abstract fun bindCarRepository(carRepositoryImpl: CarRepositoryImpl): CarRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(loginRepository: LoginRepositoryImpl): LoginRepository

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(registerRepository: RegisterRepositoryImpl): RegisterRepository

    @Binds
    @Singleton
    abstract fun bindLocalRepository(localRepository: LocalRepositoryImpl):LocalRepository

    @Binds
    @Singleton
    abstract fun bindTokenRepository(tokenRepositoryImpl: TokenRepositoryImpl):TokenRepository

    @Binds
    @Singleton
    abstract fun bindSessionManager(sessionManagerImpl: SessionManagerImpl): SessionManager

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(paymentRepositoryImpl: PaymentRepositoryImpl):PaymentRepository

}