package com.andrei.car_rental_android

import com.andrei.car_rental_android.engine.request.RequestState
import io.mockk.MockKMatcherScope
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


typealias RepositoryCall<DataType> = MockKMatcherScope.() -> Flow<RequestState<DataType>>

abstract class BaseViewModelTest : BaseTest() {

    protected inline fun <reified DataType> returnSuccess(crossinline repositoryCall: RepositoryCall<DataType>) {
        val successfulResponse = RequestState.Success<DataType>(mockk(relaxed = true))
        coEvery {
            repositoryCall()
        } returns flow {
            emit(RequestState.Loading)
            emit(successfulResponse)
        }
    }
    protected inline fun <reified DataType> returnLoading(crossinline repositoryCall: RepositoryCall<DataType>) {
        coEvery {
            repositoryCall()
        } returns flow {
            emit(RequestState.Loading)
        }
    }
    protected inline fun <reified DataType> returnError(
        errorCode: Int,
        message :String ? = null,
        crossinline repositoryCall: RepositoryCall<DataType>) {
        val response = RequestState.Error(code = errorCode, message = message ?: "")
        coEvery {
            repositoryCall()
        } returns flow {
            emit(RequestState.Loading)
            emit(response)
        }
    }
    protected inline fun <reified DataType> returnConnectionException(crossinline repositoryCall: RepositoryCall<DataType>) {
        val response = RequestState.ConnectionError
        coEvery {
            repositoryCall()
        } returns flow {
            emit(RequestState.Loading)
            emit(response)
        }
    }
}
