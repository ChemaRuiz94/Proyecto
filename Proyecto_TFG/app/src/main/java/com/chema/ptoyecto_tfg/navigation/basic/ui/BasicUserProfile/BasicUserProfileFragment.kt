package com.chema.ptoyecto_tfg.navigation.basic.ui.BasicUserProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.databinding.FragmentBasicUserProfileBinding
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.FileNotFoundException
import java.io.InputStream

class BasicUserProfileFragment  : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private lateinit var basicUserProfileViewModel: BasicUserProfileViewModel
    private var _binding: FragmentBasicUserProfileBinding? = null

    private var photo: Bitmap? = null
    lateinit var basciUserActual : BasicUser

    //componentes
    lateinit var imgUsuarioPerfil : ImageView
    lateinit var edTxtBasicUserName : EditText
    lateinit var edTxtBasicUserEmail : EditText
    lateinit var edTxtBasicUserPhone : EditText

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        basicUserProfileViewModel = ViewModelProvider(this).get(BasicUserProfileViewModel::class.java)

        _binding = FragmentBasicUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = Firebase.auth
        currentUser = auth.currentUser!!

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgUsuarioPerfil = view.findViewById(R.id.img_basic_user_profile)
        edTxtBasicUserName = view.findViewById(R.id.ed_txt_userName_basic_profile)
        edTxtBasicUserEmail = view.findViewById(R.id.ed_txt_email_basic_profile)
        edTxtBasicUserPhone = view.findViewById(R.id.ed_txt_phone_basic_profile)

        imgUsuarioPerfil.setOnClickListener{
            cambiarFoto()
        }

        cargarDatosUser()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++
    /*
    Cargar los datos del usuario
     */
    fun cargarDatosUser(){
        basciUserActual = VariablesCompartidas.usuarioBasicoActual as BasicUser

        edTxtBasicUserName.apply {
            text.clear()
            append(basciUserActual.userName)
        }
        edTxtBasicUserEmail.apply {
            text.clear()
            append(basciUserActual.email)
        }
        edTxtBasicUserPhone.apply {
            text.clear()
            append(basciUserActual.phone.toString())
        }

        if(basciUserActual.img != null){
            var bm : Bitmap? = Utils.StringToBitMap(basciUserActual.img)
            imgUsuarioPerfil.setImageBitmap(bm)
        }
    }

    /*
    Cambiar foto dando a escoger entre galeria o camara
     */
    fun cambiarFoto() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.chosePhoto))
            .setMessage(getString(R.string.strMensajeElegirFoto))
            .setPositiveButton(getString(R.string.strCamara)) { view, _ ->
                hacerFoto()
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strGaleria)) { view, _ ->
                elegirDeGaleria()
                view.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    private fun elegirDeGaleria() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Seleccione una imagen"),
            Constantes.CODE_GALLERY
        )
    }

    private fun hacerFoto() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                context as AppCompatActivity,
                arrayOf(Manifest.permission.CAMERA),
                Constantes.CODE_CAMERA
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, Constantes.CODE_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constantes.CODE_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    photo = data?.extras?.get("data") as Bitmap
                    imgUsuarioPerfil.setImageBitmap(photo)
                }
            }
            Constantes.CODE_GALLERY -> {
                if (resultCode === Activity.RESULT_OK) {
                    val selectedImage = data?.data
                    val selectedPath: String? = selectedImage?.path
                    if (selectedPath != null) {
                        var imageStream: InputStream? = null
                        try {
                            imageStream = selectedImage.let {
                                requireContext().contentResolver.openInputStream(
                                    it
                                )
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        val bmp = BitmapFactory.decodeStream(imageStream)
                        photo = Bitmap.createScaledBitmap(bmp, 200, 300, true)
                        imgUsuarioPerfil.setImageBitmap(photo)
                    }
                }
            }
        }
    }
}