package com.andrei.car_rental_android.state

import com.andrei.car_rental_android.DI.RepositoryScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SessionManager {

     val authenticationState:StateFlow<AuthenticationState>

    sealed class AuthenticationState{
        object NotAuthenticated:AuthenticationState()
        object Authenticating:AuthenticationState()
        sealed class Authenticated:AuthenticationState(){
            object AllDetailsVerified:Authenticated()
            object NoDetailsVerified:Authenticated()
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

    init {
        coroutineScope.launch {
            localRepository.refreshToken
        }
    }

}