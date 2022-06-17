package com.chema.ptoyecto_tfg.navigation.basic.ui.ArtistUserProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.chema.ptoyecto_tfg.activities.SelectStudioMapsActivity
import com.chema.ptoyecto_tfg.databinding.FragmentArtistUserProfileBinding
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.Exception

class ArtistUserProfileFragment : Fragment(), OnMapReadyCallback {

    private lateinit var artistUserProfileViewModel: ArtistUserProfileViewModel
    private var _binding : FragmentArtistUserProfileBinding? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private val db = FirebaseFirestore.getInstance()

    private lateinit var mMap: GoogleMap
    lateinit var artistUserActual : ArtistUser
    private var photo: Bitmap? = null
    private var photoSt: String? = null

    private var ubiActual : LatLng? = LatLng(40.416, -3.703)
    private var latitudStudio : Double? = null
    private var longitudStudio: Double? = null

    private var editMode : Boolean = false

    lateinit var imgUsuarioPerfil : ImageView
    lateinit var edTxtArtistUserName : EditText
    lateinit var edTxtArtistUserEmail : EditText
    lateinit var edTxtArtistUserPhone : EditText
    lateinit var edTxtArtistUserCif : EditText
    lateinit var txt_del_artist_user : TextView
    lateinit var btn_enable_edit : FloatingActionButton
    lateinit var btn_change_location: Button
    lateinit var btn_change_pwd : Button

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        artistUserProfileViewModel = ViewModelProvider(this).get(ArtistUserProfileViewModel::class.java)
        _binding = FragmentArtistUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = Firebase.auth
        currentUser = auth.currentUser!!

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgUsuarioPerfil = view.findViewById(R.id.img_artist_user_profile)
        edTxtArtistUserName = view.findViewById(R.id.ed_txt_userName_artist_profile)
        edTxtArtistUserEmail = view.findViewById(R.id.ed_txt_email_artist_profile)
        edTxtArtistUserPhone = view.findViewById(R.id.ed_txt_phone_artist_profile)
        edTxtArtistUserCif = view.findViewById(R.id.ed_txt_cif_profile_artist)
        btn_enable_edit = view.findViewById(R.id.flt_btn_edit_artist_user_profile)
        btn_change_pwd = view.findViewById(R.id.btn_change_password_artist_profile)
        btn_change_location = view.findViewById(R.id.btn_select_location_profile)
        txt_del_artist_user = view.findViewById(R.id.txt_del_artist_user)
        imgUsuarioPerfil.isClickable = false
        imgUsuarioPerfil.isEnabled = false
        imgUsuarioPerfil.setOnClickListener{
            cambiarFoto()
        }

        btn_enable_edit.setOnClickListener{
            changeEditMode()
        }

        btn_change_location.setOnClickListener{
            val mapIntent = Intent(requireContext(), SelectStudioMapsActivity::class.java).apply {

            }
                startActivityForResult(mapIntent as Intent?,Constantes.CODE_MAP)
        }

        btn_change_pwd.setOnClickListener{
            changePwd()
        }

        txt_del_artist_user.setOnClickListener{
            checkEliminar(VariablesCompartidas.usuarioArtistaActual!!)
        }
        cargarDatosUser()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++
    /*
    Cargar los datos del usuario
    */
    fun cargarDatosUser(){
        artistUserActual = VariablesCompartidas.usuarioArtistaActual as ArtistUser

        edTxtArtistUserName.apply {
            text.clear()
            append(artistUserActual.userName)
        }
        edTxtArtistUserEmail.apply {
            text.clear()
            append(artistUserActual.email)
        }
        edTxtArtistUserPhone.apply {
            text.clear()
            append(artistUserActual.phone.toString())
        }

        edTxtArtistUserCif.apply {
            text.clear()
            append(artistUserActual.cif.toString())
        }

        if(artistUserActual.img != null){
            var bm : Bitmap? = Utils.StringToBitMap(artistUserActual.img)
            imgUsuarioPerfil.setImageBitmap(bm)
        }

        latitudStudio = artistUserActual.latitudUbicacion
        longitudStudio = artistUserActual.longitudUbicacion
        ubiActual = LatLng(latitudStudio!!,longitudStudio!!)
        cargarMapa()
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++
    /*
    Habilita la edicion de los campos
     */
    fun changeEditMode(){
        if(editMode){
            checkSave()
            btn_enable_edit.setImageResource(R.drawable.ic_edit)
            imgUsuarioPerfil.isClickable = false
            imgUsuarioPerfil.isEnabled = false
            edTxtArtistUserName.isEnabled = false
            edTxtArtistUserPhone.isEnabled = false
            edTxtArtistUserEmail.isEnabled = false
            edTxtArtistUserCif.isEnabled = false
            btn_change_pwd.visibility = View.INVISIBLE
            btn_change_location.visibility = View.INVISIBLE
            txt_del_artist_user.visibility = View.INVISIBLE
            editMode = false
        }else{
            btn_enable_edit.setImageResource(R.drawable.ic_save)
            imgUsuarioPerfil.isClickable = true
            imgUsuarioPerfil.isEnabled = true
            edTxtArtistUserName.isEnabled = true
            edTxtArtistUserPhone.isEnabled = true
            edTxtArtistUserEmail.isEnabled = true
            edTxtArtistUserCif.isEnabled = true
            btn_change_pwd.visibility = View.VISIBLE
            btn_change_location.visibility = View.VISIBLE
            txt_del_artist_user.visibility = View.VISIBLE
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

    private fun checkEliminar(usuario: ArtistUser) {
        AlertDialog.Builder(requireContext()).setTitle(R.string.del_acount)
            .setPositiveButton(R.string.delete) { view, _ ->
                val db = FirebaseFirestore.getInstance()
                db.collection("${Constantes.collectionArtistUser}").document("${usuario.userId}").delete()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                VariablesCompartidas.usuarioArtistaActual = null
                Toast.makeText(context, R.string.Suscesfull, Toast.LENGTH_SHORT).show()
                view.dismiss()
            }.setNegativeButton(R.string.Cancel) { view, _ ->//cancela
                view.dismiss()
            }.create().show()
    }

    private fun editar(){

        if(edTxtArtistUserEmail.text.trim().isNotEmpty() && edTxtArtistUserPhone.text.trim().isNotEmpty() && edTxtArtistUserName.text.trim().isNotEmpty()  && edTxtArtistUserCif.text.trim().isNotEmpty() && Utils.checkMovil(edTxtArtistUserPhone.text.toString().trim())){

            val email_mod = edTxtArtistUserEmail.text.toString().trim()
            val userName_mod = edTxtArtistUserName.text.toString().trim()
            val phone_mod = edTxtArtistUserPhone.text.toString().trim().toInt()
            val cif = edTxtArtistUserCif.text.toString().trim()
            var lat = 0.0
            var lon = 0.0
            if(VariablesCompartidas.latitudStudioSeleccionado != null){
                lat = VariablesCompartidas.latitudStudioSeleccionado.toString().toDouble()
                lon = VariablesCompartidas.longitudStudioSeleccionado.toString().toDouble()
            }else{
                lat = VariablesCompartidas.usuarioArtistaActual!!.latitudUbicacion!!
                lon = VariablesCompartidas.usuarioArtistaActual!!.longitudUbicacion!!
            }

            photo = imgUsuarioPerfil.drawToBitmap()
            val imgST = Utils.ImageToString(photo!!)
            val artistUser = ArtistUser(artistUserActual.userId,userName_mod,email_mod,phone_mod,imgST,artistUserActual.rol,artistUserActual.idFavoritos,VariablesCompartidas.usuarioArtistaActual!!.prices,VariablesCompartidas.usuarioArtistaActual!!.sizes,cif,lat,lon)

            db.collection("${Constantes.collectionArtistUser}")
                .document(VariablesCompartidas.usuarioArtistaActual!!.userId.toString()) //Será la clave del documento.
                .set(artistUser).addOnSuccessListener {

                    //val us : User = user as User

                    //Log.i("profile", currentUser.email.toString())
                    VariablesCompartidas.usuarioArtistaActual = artistUser

                    currentUser!!.updateEmail(artistUser.email.toString())

                    val navigationView: NavigationView =
                        (context as AppCompatActivity).findViewById(R.id.nav_view)
                    val header: View = navigationView.getHeaderView(0)
                    val imgHe = header.findViewById<ImageView>(R.id.image_basic_user_header)
                    val nameHead = header.findViewById<TextView>(R.id.txt_userName_header)
                    val emailHead = header.findViewById<TextView>(R.id.txt_userEmail_header)

                    imgHe.setImageBitmap(photo)
                    nameHead.text = artistUser.userName
                    emailHead.text = artistUser.email

                    Toast.makeText( requireContext(), R.string.Suscesfull, Toast.LENGTH_SHORT).show()

                }.addOnFailureListener{
                    Toast.makeText(requireContext(), R.string.ERROR, Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(requireContext(),R.string.emptyCamps,Toast.LENGTH_SHORT).show()
        }
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++
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


    /*+
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

    //+++++++++++++++++++++++++++++++
    /*
    Carga un Map en el fragment
     */
    private fun cargarMapa() {
        try{
            val mapFragment = childFragmentManager.findFragmentById(R.id.frm_MapLocation2) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }catch(e : Exception){
            Log.d("CHE_TAG","${e.toString()}")
        }
    }



    override fun onMapReady(p0: GoogleMap) {
        mMap = p0!!
        mMap.mapType=GoogleMap.MAP_TYPE_HYBRID
        //val Madrid = LatLng(40.416, -3.703)
        mMap?.addMarker(MarkerOptions().position(ubiActual!!).title("${artistUserActual.userName.toString()}"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(ubiActual!!,10f))

    }

    //+++++++++++++++++++++++++++++++

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constantes.CODE_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    photo = data?.extras?.get("data") as Bitmap
                    imgUsuarioPerfil.setImageBitmap(photo)
                    photoSt = Utils.ImageToString(photo)
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
                                requireActivity().contentResolver.openInputStream(
                                    it
                                )
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        val bmp = BitmapFactory.decodeStream(imageStream)
                        photo = Bitmap.createScaledBitmap(bmp, 200, 300, true)
                        imgUsuarioPerfil.setImageBitmap(photo)
                        photoSt = Utils.ImageToString(photo)
                    }
                }
            }
            Constantes.CODE_MAP -> {

                if(VariablesCompartidas.latitudStudioSeleccionado != null && VariablesCompartidas.longitudStudioSeleccionado != null){
                    latitudStudio = VariablesCompartidas.latitudStudioSeleccionado.toString().toDouble()
                    longitudStudio = VariablesCompartidas.longitudStudioSeleccionado.toString().toDouble()

                    ubiActual = LatLng(latitudStudio!!,longitudStudio!!)

                    val ubi = LatLng(VariablesCompartidas.latitudStudioSeleccionado.toString().toDouble(), VariablesCompartidas.longitudStudioSeleccionado.toString().toDouble())
                    mMap?.clear()
                    mMap?.addMarker(MarkerOptions().position(ubi).title("${edTxtArtistUserName.text}"))
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(ubi,15f))
                }
            }
        }
    }
}