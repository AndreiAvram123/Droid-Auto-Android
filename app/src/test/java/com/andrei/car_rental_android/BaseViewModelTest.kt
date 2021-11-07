package com.andrei.car_rental_android

import com.andrei.car_rental_android.engine.configuration.RequestState
import io.mockk.MockKMatcherScope
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.ExperimentalTime


typealias RepositoryCall<DataType> = MockKMatcherScope.() -> Flow<RequestState<DataType>>


@ExperimentalTime
@ExperimentalCoroutinesApi
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
    protected inline fun <reified DataType> returnFailure(errorCode: Int, crossinline repositoryCall: RepositoryCall<DataType>) {
        val response = RequestState.Error(code = errorCode, message = "")
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
