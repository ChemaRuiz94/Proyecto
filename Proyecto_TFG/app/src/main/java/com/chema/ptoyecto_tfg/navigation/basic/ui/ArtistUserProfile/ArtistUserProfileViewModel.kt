package com.chema.ptoyecto_tfg.navigation.basic.ui.ArtistUserProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArtistUserProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Artist User Search Fragment"
    }
    val text: LiveData<String> = _text
}