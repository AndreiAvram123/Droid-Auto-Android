package com.andrei.car_rental_android.screens.Home.states

sealed class HomeNavigationState{
    object Default:HomeNavigationState()
    object NavigateToRideScreen:HomeNavigationState()
}
