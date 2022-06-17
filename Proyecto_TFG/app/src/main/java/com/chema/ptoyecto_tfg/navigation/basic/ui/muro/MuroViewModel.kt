package com.chema.ptoyecto_tfg.navigation.basic.ui.muro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MuroViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is muro Fragment"
    }
    val text: LiveData<String> = _text
}