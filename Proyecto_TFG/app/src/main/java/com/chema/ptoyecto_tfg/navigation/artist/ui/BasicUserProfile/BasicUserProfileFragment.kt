package com.chema.ptoyecto_tfg.navigation.artist.ui.BasicUserProfile

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
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.activities.LoginActivity
import com.chema.ptoyecto_tfg.databinding.FragmentBasicUserProfileBinding
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.FileNotFoundException
import java.io.InputStream

class BasicUserProfileFragment  : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private val db = FirebaseFirestore.getInstance()

    lateinit var basciUserActual : BasicUser
    private var photo: Bitmap? = null
    private var photoSt: String? = null

    private var _binding: FragmentBasicUserProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    //componentes
    lateinit var imgUsuarioPerfil : ImageView
    lateinit var edTxtBasicUserName : EditText
    lateinit var edTxtBasicUserEmail : EditText
    lateinit var edTxtBasicUserPhone : EditText
    lateinit var txt_del_basic_user : TextView
    lateinit var btn_enable_edit : FloatingActionButton
    lateinit var btn_change_pwd : Button

    //to change edit mode
    private var editMode : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
        txt_del_basic_user = view.findViewById(R.id.txt_del_basic_user)
        btn_enable_edit = view.findViewById(R.id.flt_btn_edit_basic_user_profile)
        btn_change_pwd = view.findViewById(R.id.btn_change_password_basic_profile)

        imgUsuarioPerfil.setOnClickListener{
            cambiarFoto()
        }

        btn_enable_edit.setOnClickListener{
            changeEditMode()
        }

        btn_change_pwd.setOnClickListener{
            changePwd()
        }

        txt_del_basic_user.setOnClickListener{
            checkEliminar(VariablesCompartidas.usuarioBasicoActual!!)
        }


        cargarDatosUser()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        cargarDatosUser()
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
    Habilita la edicion de los campos
     */
    fun changeEditMode(){
        if(editMode){
            checkSave()
            btn_enable_edit.setImageResource(R.drawable.ic_edit)
            imgUsuarioPerfil.isClickable = false
            edTxtBasicUserName.isEnabled = false
            edTxtBasicUserPhone.isEnabled = false
            edTxtBasicUserEmail.isEnabled = false
            btn_change_pwd.visibility = View.INVISIBLE
            txt_del_basic_user.visibility = View.INVISIBLE
            editMode = false
        }else{
            btn_enable_edit.setImageResource(R.drawable.ic_save)
            imgUsuarioPerfil.isClickable = true
            edTxtBasicUserName.isEnabled = true
            edTxtBasicUserPhone.isEnabled = true
            edTxtBasicUserEmail.isEnabled = true
            btn_change_pwd.visibility = View.VISIBLE
            txt_del_basic_user.visibility = View.VISIBLE
            editMode = true
        }
    }

    /*
    Pregunta al usuario si desea guardar los cambios editados
     */
    fun checkSave(){
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.saveEdit))
            .setMessage(getString(R.string.strMensajeSaveEdit))
            .setPositiveButton(getString(R.string.CONFIRM)) { view, _ ->
                editar()
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.Cancel)) { view, _ ->

                view.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }


    private fun checkEliminar(usuario: BasicUser) {
        AlertDialog.Builder(requireContext()).setTitle(R.string.del_acount)
            .setPositiveButton(R.string.delete) { view, _ ->
                val db = FirebaseFirestore.getInstance()
                db.collection("${Constantes.collectionUser}").document("${usuario.userId}").delete()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                VariablesCompartidas.usuarioBasicoActual = null
                Toast.makeText(context, R.string.Suscesfull, Toast.LENGTH_SHORT).show()
                view.dismiss()
            }.setNegativeButton(R.string.Cancel) { view, _ ->//cancela
                view.dismiss()
            }.create().show()
    }

    private fun editar(){

        if(edTxtBasicUserEmail.text.trim().isNotEmpty() && edTxtBasicUserPhone.text.trim().isNotEmpty() && edTxtBasicUserName.text.trim().isNotEmpty() && Utils.checkMovil(edTxtBasicUserPhone.text.toString().trim())){

            var email_mod = edTxtBasicUserEmail.text.toString().trim()
            var userName_mod = edTxtBasicUserName.text.toString().trim()
            var phone_mod = edTxtBasicUserPhone.text.toString().trim().toInt()

            photo = imgUsuarioPerfil.drawToBitmap()
            val imgST = Utils.ImageToString(photo!!)
            var basicUser = BasicUser(basciUserActual.userId,userName_mod,email_mod,phone_mod,imgST,basciUserActual.rol,basciUserActual.idFavoritos)


            db.collection("${Constantes.collectionUser}")
                .document(VariablesCompartidas.usuarioBasicoActual!!.userId.toString()) //Será la clave del documento.
                .set(basicUser).addOnSuccessListener {

                    VariablesCompartidas.usuarioBasicoActual = basicUser
                    currentUser!!.updateEmail(basicUser.email.toString())

                }.addOnFailureListener{
                    Toast.makeText(requireContext(), R.string.ERROR, Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(requireContext(),R.string.emptyCamps,Toast.LENGTH_SHORT).show()
        }
    }


    /*
    Cambiar contraseña
     */
    fun changePwd() {
        val dialog = layoutInflater.inflate(R.layout.password_changer, null)
        val pass1 = dialog.findViewById<EditText>(R.id.edPassChanger)
        val pass2 = dialog.findViewById<EditText>(R.id.edPass2Changer)
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.changePassword))
            .setView(dialog)
            .setPositiveButton("OK") { view, _ ->
                val p1 = pass1.text.toString()
                val p2 = pass2.text.toString()
                if (p1 == p2) {
                    currentUser.updatePassword(p1)
                    Toast.makeText(
                        context,
                        getString(R.string.Suscesfull),
                        Toast.LENGTH_SHORT
                    ).show()
                } else Toast.makeText(
                    context,
                    getString(R.string.ERROR),
                    Toast.LENGTH_SHORT
                ).show()
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.Cancel)) { view, _ ->
                view.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
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

    /*
    Permite elegir una imagen de la galeria
     */
    private fun elegirDeGaleria() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Seleccione una imagen"),
            Constantes.CODE_GALLERY
        )
    }

    /*
    Permite realizar una foto con la camara
     */
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