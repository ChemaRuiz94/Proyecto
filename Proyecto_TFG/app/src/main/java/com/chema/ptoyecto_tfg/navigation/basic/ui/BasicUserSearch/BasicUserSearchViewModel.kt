package com.chema.ptoyecto_tfg.navigation.basic.ui.BasicUserSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BasicUserSearchViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Basic User Search Fragment"
    }
    val text: LiveData<String> = _text
}