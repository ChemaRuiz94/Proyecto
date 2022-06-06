package com.chema.ptoyecto_tfg.navigation.basic.ui.BasicUserSearch

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chema.ptoyecto_tfg.databinding.FragmentBasicUserSearchBinding

class BasicUserSearchFragment : Fragment() {

    private lateinit var basicUserSearchViewModel: BasicUserSearchViewModel
    private var _binding : FragmentBasicUserSearchBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        basicUserSearchViewModel = ViewModelProvider(this).get(BasicUserSearchViewModel::class.java)


        _binding = FragmentBasicUserSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}