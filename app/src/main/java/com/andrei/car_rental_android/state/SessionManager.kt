package com.andrei.car_rental_android.state

import com.andrei.car_rental_android.DI.RepositoryScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SessionManager {

     val authenticationState:StateFlow<AuthenticationState>

     fun notifyLoginRequired()

    sealed class AuthenticationState{
        object NotAuthenticated:AuthenticationState()
        object Authenticating:AuthenticationState()
        sealed class Authenticated:AuthenticationState(){
            object AllDetailsVerified:Authenticated()
            object CannotVerifyDetails:Authenticated()
            object RequireEmailVerification:Authenticated()
            object RequireIdentityVerification:Authenticated()
        }
    }


}

class SessionManagerImpl @Inject constructor(
      private val localRepository: LocalRepository,
      @RepositoryScope private val coroutineScope: CoroutineScope

) : SessionManager{

    override val authenticationState: MutableStateFlow<SessionManager.AuthenticationState> = MutableStateFlow(SessionManager.AuthenticationState.Authenticating)

    override fun notifyLoginRequired() {
         authenticationState.tryEmit(SessionManager.AuthenticationState.NotAuthenticated)
    }

    init {
        coroutineScope.launch {
            localRepository.refreshTokenFlow.collect {
                if(!it.isNullOrBlank()){
                    //authenticated but need to check credentials
                    //assume for now that the details are verified
                    authenticationState.emit(SessionManager.AuthenticationState.Authenticated.AllDetailsVerified)
                }else{
                    authenticationState.emit(SessionManager.AuthenticationState.NotAuthenticated)
                }
            }
        }
    }

}