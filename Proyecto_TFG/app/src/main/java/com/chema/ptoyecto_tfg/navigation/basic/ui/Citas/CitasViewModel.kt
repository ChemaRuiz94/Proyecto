package com.chema.ptoyecto_tfg.navigation.basic.ui.Citas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CitasViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Citas Fragment"
    }
    val text: LiveData<String> = _text
}