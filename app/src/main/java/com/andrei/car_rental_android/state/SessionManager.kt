package com.andrei.car_rental_android.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface SessionManager {

     val authenticationState:StateFlow<AuthenticationState>

    sealed class AuthenticationState{
        object NotAuthenticated:AuthenticationState()
        object Authenticating:AuthenticationState()
        sealed class Authenticated:AuthenticationState(){
            object AllDetailsVerified:Authenticated()
            object RequireEmailVerification:Authenticated()
            object RequireIdentityVerification:Authenticated()
        }
    }


}

class SessionManagerImpl @Inject constructor(

) : SessionManager{

    override val authenticationState: MutableStateFlow<SessionManager.AuthenticationState> = MutableStateFlow(SessionManager.AuthenticationState.Authenticating)



}