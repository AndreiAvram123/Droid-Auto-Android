package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.engine.configuration.ResponseWrapper
import retrofit2.Response

typealias ApiResponse<DataType> = Response<ResponseWrapper<DataType>>