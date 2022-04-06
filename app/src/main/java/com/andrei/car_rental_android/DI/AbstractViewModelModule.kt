package com.andrei.car_rental_android.DI

import com.andrei.car_rental_android.helpers.LocationHelper
import com.andrei.car_rental_android.helpers.LocationHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
abstract class AbstractViewModelModule {

   @ViewModelScoped
   @Binds
   abstract fun bindLocationHelper(locationHelperImpl: LocationHelperImpl): LocationHelper
}