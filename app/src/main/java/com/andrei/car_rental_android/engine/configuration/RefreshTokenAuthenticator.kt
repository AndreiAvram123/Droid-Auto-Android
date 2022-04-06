package com.andrei.car_rental_android.engine.configuration

import android.webkit.URLUtil
import com.andrei.car_rental_android.BuildConfig
import com.andrei.car_rental_android.engine.repositories.TokenRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.utils.JwtUtils
import com.andrei.car_rental_android.state.LocalRepository
import com.andrei.car_rental_android.state.SessionManager
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

class RefreshTokenAuthenticator @Inject constructor(
    private val localRepository: LocalRepository,
    private val tokenRepository: Lazy<TokenRepository>,
    private val sessionManager: Lazy<SessionManager>,
    private val jwtUtils: JwtUtils
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (requestChainCount(response) > maxRetryAttempts) {
            return null
        }

        if (!response.request.url.toString().isURlValid()) {

            return null
        }

        var successfullyRefreshedToken = false
        synchronized(lock) {
            runBlocking {
                val request = response.networkResponse?.request
                if (request != null && request.hasAuthorizationHeader()) {
                    val refreshToken = localRepository.refreshTokenFlow.firstOrNull()
                    if (!refreshToken.isNullOrBlank() && jwtUtils.isTokenValid(refreshToken)) {
                        //attempt to get new access token
                        val result = tokenRepository.get().getNewAccessToken().first { state ->
                            state !is RequestState.Loading
                        }
                        successfullyRefreshedToken = result is RequestState.Success
                    }
                } else {
                    // If the current access token is different from the one in the request that means
                    // its already been refreshed and so it should retry with the new one instead of
                    // refreshing
                    successfullyRefreshedToken = true
                }
                if(!successfullyRefreshedToken){
                    sessionManager.get().notifyLoginRequired()
                }
            }
        }


        return if (successfullyRefreshedToken) {
            response.request
        } else {
            null
        }
    }

    /**
     * Use this method to check if the request returned to the refresh token
     * authenticator has the  access token that we currently have
     */
    private suspend fun Request.hasAuthorizationHeader(): Boolean {
        return this.header(HEADER_AUTHORIZATION) == "$HEADER_AUTHORIZATION_VALUE_PREFIX ${localRepository.accessTokenFlow.firstOrNull()}"
    }

    private fun requestChainCount(currentResponse: Response): Int {
        var response: Response? = currentResponse.priorResponse
        var result = 1
        while (response != null) {
            response = response.priorResponse
            result++
        }
        return result
    }

    private fun String.isURlValid(): Boolean {
        return URLUtil.isValidUrl(this) && this.startsWith(BuildConfig.BASE_URL)
    }

    companion object {
        private val lock = ReentrantLock()
        private const val maxRetryAttempts = 10
    }
}
