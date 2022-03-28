package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.engine.utils.JwtParser
import com.andrei.car_rental_android.engine.utils.JwtParserImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilAbstractModule {

    @Binds
    @Singleton
    abstract fun bindJwtParser(jwtParser: JwtParserImpl):JwtParser
}