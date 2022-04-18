package com.andrei.car_rental_android.state

import com.andrei.car_rental_android.DI.RepositoryScope
import com.andrei.car_rental_android.engine.repositories.UserRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.utils.JwtUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        data class Authenticated(
            val sessionUserState: StateFlow<SessionUserState>
            ) : AuthenticationState()
    }


}

class SessionManagerImpl @Inject constructor(
      private val localRepository: LocalRepository,
      private val userRepository: UserRepository,
      @RepositoryScope private val coroutineScope: CoroutineScope,
      private val jwtUtils: JwtUtils

) : SessionManager{

    override val authenticationState: MutableStateFlow<SessionManager.AuthenticationState> = MutableStateFlow(SessionManager.AuthenticationState.Authenticating)
    override val sessionUserState: MutableStateFlow<SessionUserState> = MutableStateFlow(SessionUserState.LoadingUser)

    override fun notifyLoginRequired() {
        coroutineScope.launch {
            authenticationState.emit(SessionManager.AuthenticationState.NotAuthenticated)
        }
    }

    override fun signOut() {
        coroutineScope.launch {
            localRepository.clearRefreshToken()
            localRepository.clearAccessToken()
        }
    }

    private fun getSessionUser(){
        coroutineScope.launch {
              userRepository.getCurrentUser().collect{requestState->
                  when(requestState){
                      is RequestState.Success -> sessionUserState.emit(SessionUserState.Loaded(
                          requestState.data
                      )
                      )
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
            localRepository.refreshTokenFlow.collect {
                if(!it.isNullOrBlank() && jwtUtils.isTokenValid(it)){
                    //authenticated but need to check credentials
                    //assume for now that the details are verified
                    authenticationState.emit(SessionManager.AuthenticationState.Authenticated(
                        sessionUserState.asStateFlow()
                    ))
                    getSessionUser()
                }else{
                    authenticationState.emit(SessionManager.AuthenticationState.NotAuthenticated)
                }
            }
        }
    }

}