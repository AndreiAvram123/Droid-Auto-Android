package com.andrei.car_rental_android.baseConfig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

interface Navigator


abstract class BaseFragment<VM: BaseViewModel> : Fragment() {

    abstract val viewModel:VM
    abstract val navigator:Navigator
}