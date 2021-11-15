package com.andrei.car_rental_android.engine.configuration

import com.andrei.car_rental_android.DI.NetworkDispatcher
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

//the service call alias is a suspend function that returns a response of type response wrapper
typealias ServiceCall<DataType> = suspend () -> Response<ResponseWrapper<DataType>>

interface RequestExecutor{
    fun <DataType> performRequest(
        serviceCall: ServiceCall<DataType>
    ): Flow<RequestState<DataType>>

}
@Singleton
class RequestExecutorImpl @Inject constructor(
    @NetworkDispatcher private val networkDispatcher: CoroutineDispatcher
):RequestExecutor {

    override fun <DataType> performRequest(serviceCall: ServiceCall<DataType>) = flow {
        emit(RequestState.Loading)
        try{
            val response = serviceCall()
            val body = response.body()
            if(response.isSuccessful && body != null){
                emit(RequestState.Success(body.data))
            }else if (response.code() == 401) {
                // Should not reach this
                emit(RequestState.Error(response.message(), response.code()))
            } else {
                val gson = Gson()
                val errorResponse = gson.fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                emit(RequestState.Error(errorResponse.error, response.code()))
            }
        } catch (e: Exception) {
            when (e) {
                is ConnectException, is UnknownHostException, is InterruptedIOException -> {
                    emit(RequestState.ConnectionError)
                }
                else -> throw e
            }
        }
    }.flowOn(networkDispatcher)

}