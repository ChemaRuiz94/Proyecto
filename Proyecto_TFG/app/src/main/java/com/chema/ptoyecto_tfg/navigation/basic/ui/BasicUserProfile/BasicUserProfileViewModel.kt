package com.chema.ptoyecto_tfg.navigation.basic.ui.BasicUserProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BasicUserProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Basic User Profile Fragment"
    }
    val text: LiveData<String> = _text
}