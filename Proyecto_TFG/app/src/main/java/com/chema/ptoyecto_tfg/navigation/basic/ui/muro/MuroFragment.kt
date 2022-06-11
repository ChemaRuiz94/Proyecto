package com.chema.ptoyecto_tfg.navigation.basic.ui.muro

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.databinding.FragmentMuroBinding
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MuroFragment : Fragment() {

    private lateinit var muroViewModel: MuroViewModel
    private var _binding: FragmentMuroBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var userMuro : ArtistUser

    private lateinit var fltBtnFav : FloatingActionButton
    private lateinit var txtUserNane : TextView
    private lateinit var txtEmail : TextView
    private lateinit var txtUbi : TextView
    private lateinit var txtWeb : TextView
    private lateinit var btnContact : Button
    private lateinit var imgArtist : ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        muroViewModel =
            ViewModelProvider(this).get(MuroViewModel::class.java)

        _binding = FragmentMuroBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fltBtnFav = view.findViewById(R.id.fl_btn_fav_artist_muro)

        fltBtnFav.setOnClickListener{
            changeFav()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //++++++++++++++++++++++++++++++++++++++++
    private fun changeFav() {
       fltBtnFav.setImageResource(R.drawable.ic_favorite)
    }
}