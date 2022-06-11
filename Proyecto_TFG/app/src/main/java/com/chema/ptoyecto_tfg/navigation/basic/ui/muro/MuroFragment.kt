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
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MuroFragment : Fragment() {

    private lateinit var muroViewModel: MuroViewModel
    private var _binding: FragmentMuroBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var favorite : Boolean = false
    private  var userMuro : ArtistUser? = null
    private  var userArtistActual : ArtistUser? = null
    private  var userBasicActual : BasicUser? = null

    private lateinit var fltBtnFav : FloatingActionButton
    private lateinit var txtUserNane : TextView
    private lateinit var txtEmail : TextView
    private lateinit var txtUbi : TextView
    private lateinit var txtWeb : TextView
    private lateinit var btnContactEdit : Button
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
        btnContactEdit = view.findViewById(R.id.btn_contact_edit)
        txtUserNane = view.findViewById(R.id.txt_artist_user_name_muro)

        fltBtnFav.setOnClickListener{
            //checkFav()
            changeFav()
        }

        cargarDatosArtist()
        checkFav()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        VariablesCompartidas.userArtistVisitMode = false
        VariablesCompartidas.idUserArtistVisitMode = null
    }

    override fun onDestroy() {
        super.onDestroy()
        VariablesCompartidas.userArtistVisitMode = false
        VariablesCompartidas.idUserArtistVisitMode = null
    }

    //++++++++++++++++++++++++++++++++++++++++
    private fun changeFav() {
       fltBtnFav.setImageResource(R.drawable.ic_favorite)
    }
    private fun checkFav() {
        if(VariablesCompartidas.usuarioBasicoActual != null){
            userBasicActual = VariablesCompartidas.usuarioBasicoActual
            if(userBasicActual!!.idFavoritos!!.contains(userMuro!!.userId)){
                favorite = true
                fltBtnFav.setImageResource(R.drawable.ic_favorite)
            }else{
                favorite = false
                fltBtnFav.setImageResource(R.drawable.ic_unfavorite)
            }
        }else {
            userArtistActual = VariablesCompartidas.usuarioArtistaActual
            if(userArtistActual!!.idFavoritos!!.contains(userMuro!!.userId)){
                favorite = true
                fltBtnFav.setImageResource(R.drawable.ic_favorite)
            }else{
                favorite = false
                fltBtnFav.setImageResource(R.drawable.ic_unfavorite)
            }
        }
    }

    //++++++++++++++++++++++++++++++++++++++++
    private fun cargarDatosArtist() {
        if(VariablesCompartidas.idUserArtistVisitMode != null){
            userMuro = VariablesCompartidas.usuarioArtistaVisitaMuro

            //imgArtist.setImageBitmap(Utils.StringToBitMap(userArtMuro!!.img.toString()))
            txtUserNane.text = (userMuro!!.userName.toString())
            btnContactEdit.setText(R.string.contact)
            fltBtnFav.visibility = View.VISIBLE
            if(VariablesCompartidas.usuarioArtistaActual != null && userMuro!!.userId!!.equals(VariablesCompartidas!!.usuarioArtistaActual!!.userId) ){
                btnContactEdit.visibility = View.INVISIBLE
                fltBtnFav.visibility = View.INVISIBLE
            }

        }
        if(VariablesCompartidas.idUserArtistVisitMode == null && VariablesCompartidas.usuarioArtistaActual != null){
            userMuro = VariablesCompartidas.usuarioArtistaActual
            txtUserNane.setText(userMuro!!.userName.toString())
            btnContactEdit.setText(R.string.edit_muro)
            fltBtnFav.visibility = View.INVISIBLE
            //imgArtist.setImageBitmap(Utils.StringToBitMap(userArtMuro!!.img.toString()))
        }
    }

}