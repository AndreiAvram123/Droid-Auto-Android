package com.andrei.car_rental_android.screens.Settings

import androidx.lifecycle.ViewModel
import com.andrei.car_rental_android.state.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

abstract class SettingsViewModel : ViewModel() {
    abstract fun signOut()
}

@HiltViewModel
class SettingsViewModelImpl @Inject constructor(
    private val sessionManager: SessionManager
):SettingsViewModel(){

    override fun signOut() {
       sessionManager.signOut()
    }

}