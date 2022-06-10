package com.chema.ptoyecto_tfg.navigation.basic.ui.ArtistUserProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chema.ptoyecto_tfg.databinding.FragmentArtistUserProfileBinding

class ArtistUserProfileFragment : Fragment() {

    private lateinit var artistUserProfileViewModel: ArtistUserProfileViewModel
    private var _binding : FragmentArtistUserProfileBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        artistUserProfileViewModel = ViewModelProvider(this).get(ArtistUserProfileViewModel::class.java)
        _binding = FragmentArtistUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}