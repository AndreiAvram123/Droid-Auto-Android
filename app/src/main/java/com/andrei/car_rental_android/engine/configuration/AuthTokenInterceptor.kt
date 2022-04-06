package com.andrei.car_rental_android.engine.configuration

import com.andrei.car_rental_android.engine.repositories.TokenRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.utils.JwtUtils
import com.andrei.car_rental_android.state.LocalRepository
import dagger.Lazy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenInterceptor @Inject constructor(
    private val localRepository: LocalRepository,
    private val tokenRepository: Lazy<TokenRepository>,
    private val jwtUtils: JwtUtils
 ) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val interceptedBuilder = chain.request().newBuilder()

        if(chain.shouldNotIntercept()){
           return chain.proceed(chain.request())
        }

        val token = getToken()

        interceptedBuilder.header(HEADER_AUTHORIZATION, "$HEADER_AUTHORIZATION_VALUE_PREFIX $token")

        return chain.proceed(interceptedBuilder.build())
    }

    private fun Interceptor.Chain.shouldNotIntercept():Boolean{
         return request().url.isTokenEndpoint()
    }

    private fun getToken(): String? = runBlocking {
        val currentToken = localRepository.accessTokenFlow.firstOrNull()
        //check whether the token is valid even before making the request
        //This could be left out but it is better to save one request


         //check if token is null or expired
        // Check if token has expired
         if (currentToken != null) {
             if(jwtUtils.isTokenValid(currentToken)){
                 return@runBlocking  currentToken
             }else{
                 //clear the old token from storage
                 localRepository.clearAccessToken()
             }
        }

            val result = tokenRepository.get().getNewAccessToken().firstOrNull { state ->
                state !is RequestState.Loading
            }

            if (result is RequestState.Success) {
                localRepository.setAccessToken(result.data.accessToken)
                return@runBlocking result.data.accessToken
            } else {
                return@runBlocking null
            }

    }
    private fun HttpUrl.isTokenEndpoint(): Boolean {
        return this.toString().endsWith("/token")
    }
}
