package com.andrei.car_rental_android.state

import com.andrei.car_rental_android.DI.RepositoryScope
import com.andrei.car_rental_android.engine.repositories.UserRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.utils.JwtUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SessionManager {

     val authenticationState:StateFlow<AuthenticationState>
     val sessionUserState:StateFlow<SessionUserState>

     fun notifyLoginRequired()
     fun signOut()

    sealed class AuthenticationState{
        object NotAuthenticated:AuthenticationState()
        object Authenticating:AuthenticationState()
        sealed class Authenticated(val sessionUserState: StateFlow<SessionUserState>): AuthenticationState(){
            class IdentifyNotVerified(userState: StateFlow<SessionUserState>):Authenticated(userState)
            class IdentityVerified(
                 sessionUserState: StateFlow<SessionUserState>
              ): Authenticated(sessionUserState)
        }

    }


}

class SessionManagerImpl @Inject constructor(
      private val localRepository: LocalRepository,
      private val userRepository: UserRepository,
      @RepositoryScope private val coroutineScope: CoroutineScope,
      private val jwtUtils: JwtUtils

) : SessionManager{

    override val authenticationState: MutableStateFlow<SessionManager.AuthenticationState> = MutableStateFlow(SessionManager.AuthenticationState.Authenticating)
    override val sessionUserState: MutableStateFlow<SessionUserState> = MutableStateFlow(SessionUserState.Default)

    override fun notifyLoginRequired() {
        coroutineScope.launch {
            authenticationState.emit(SessionManager.AuthenticationState.NotAuthenticated)
        }
    }

    override fun signOut() {
        coroutineScope.launch {
            localRepository.clear()
        }
    }

    private fun getSessionUser(){

        coroutineScope.launch {
              userRepository.getCurrentUser().collect{requestState->
                  when(requestState){
                      is RequestState.Success -> sessionUserState.emit(SessionUserState.Loaded(
                          requestState.data
                      ))
                      is RequestState.Loading -> {
                          sessionUserState.emit(
                              SessionUserState.LoadingUser
                          )
                      }
                      else ->{
                          sessionUserState.emit(
                              SessionUserState.ErrorLoadingUser
                          )
                      }
                  }
              }
        }
    }

    init {
        coroutineScope.launch {
            combine(
                localRepository.refreshTokenFlow,
                localRepository.identityVerifiedFlow
            ) { refreshToken, identityVerified ->
                when {
                    refreshToken.isNullOrBlank() || !jwtUtils.isTokenValid(refreshToken) ->{
                        localRepository.clear()
                        SessionManager.AuthenticationState.NotAuthenticated
                    }
                    identityVerified == false -> SessionManager.AuthenticationState.Authenticated.IdentifyNotVerified(sessionUserState)
                    else -> SessionManager.AuthenticationState.Authenticated.IdentityVerified(
                        sessionUserState
                    )
                }
            }.distinctUntilChanged().collect {
                authenticationState.emit(it)
                if (it is SessionManager.AuthenticationState.Authenticated) {
                    getSessionUser()
                }

            }

        }
    }

}