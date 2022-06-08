package com.chema.ptoyecto_tfg.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.chema.ptoyecto_tfg.MainActivity
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.models.Rol
import com.chema.ptoyecto_tfg.navigation.basic.BasicUserNavDrawActivity
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class SignUpArtistActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var imgArtistSignUp : ImageView
    private lateinit var edTxtArtistUserName : EditText
    private lateinit var edTxtArtistEmail : EditText
    private lateinit var edTxtArtistPhone : EditText
    private lateinit var edTxtArtistCif : EditText
    private lateinit var edTxtArtistPwd : EditText
    private lateinit var edTxtArtistRepeatPwd : EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnSelectStudio: Button

    private var ubiActual : LatLng? = LatLng(40.416, -3.703)
    private var ubicacionCambiada = false
    private var latitudStudio : Double? = null
    private var longitudStudio: Double? = null
    private var photo: Bitmap? = null
    private var photoSt: String? = null
    private lateinit var mMap: GoogleMap

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_artist)

        imgArtistSignUp = findViewById(R.id.img_sign_up_artist)
        edTxtArtistUserName = findViewById(R.id.ed_txt_user_name_sign_up_artist)
        edTxtArtistEmail = findViewById(R.id.ed_txt_email_sign_up_artist)
        edTxtArtistPhone = findViewById(R.id.ed_txt_phone_sign_up_artist)
        edTxtArtistCif = findViewById(R.id.ed_txt_cif_sign_up_artist)
        edTxtArtistPwd = findViewById(R.id.ed_txt_pwd_sign_up_artist)
        edTxtArtistRepeatPwd = findViewById(R.id.ed_txt_repeat_pwd_sign_up_artist)
        btnSelectStudio = findViewById(R.id.btn_select_location)
        btnSignUp = findViewById(R.id.btnSignUpArtist)

        btnSelectStudio.setOnClickListener{
            val mapIntent = Intent(this, SelectStudioMapsActivity::class.java).apply {
                //putExtra("email",email)
            }
            //startActivity(mapIntent)
            startActivityForResult(mapIntent,Constantes.CODE_MAP)
        }
        imgArtistSignUp.setOnClickListener{
            cambiarFoto()
        }

        btnSignUp.setOnClickListener{
            checkSignUp()
        }

        cargarMapa()
    }

    /*
    Comprueba que todos los requisitos del signUp se cumplan
     */
    private fun checkSignUp(){
        if(checkCamposVacios()){

            if(checkPwdIguales()){

                if(checkMovil(edTxtArtistPhone.text.toString().trim())){

                    if(latitudStudio != null && longitudStudio != null){
                        //SIGN UP
                        checkFirebaseAuth()
                    }else{
                        Toast.makeText(this,R.string.select_studio_location,Toast.LENGTH_SHORT).show()
                    }

                }else{
                    Toast.makeText(this,R.string.phoneNotCorrect,Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,R.string.pwdNotCoincide,Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,R.string.emptyCamps,Toast.LENGTH_SHORT).show()
        }
    }

    /*
    Comprueba que todos los campos esten rellenos
     */
    private fun checkCamposVacios(): Boolean{
        if(edTxtArtistUserName.text.toString().trim().isEmpty()){
            return false
        }
        if(edTxtArtistEmail.text.toString().trim().isEmpty()){
            return false
        }
        if(edTxtArtistPhone.text.toString().trim().isEmpty()){
            return false
        }
        if(edTxtArtistCif.text.toString().trim().isEmpty()){
            return false
        }
        if(edTxtArtistPwd.text.toString().trim().isEmpty()){
            return false
        }
        if(edTxtArtistRepeatPwd.text.toString().trim().isEmpty()){
            return false
        }

        return true
    }

    /*
   Funcion que comprueba si las dos contraseñas son iguales
    */
    fun checkPwdIguales():Boolean{
        if(edTxtArtistPwd.text.toString().trim().equals(edTxtArtistRepeatPwd.text.toString().trim())){
            return true
        }
        return false
    }

    /*
    * Comprueba si el numero de telefono es correcto
    */
    private fun checkMovil(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            if (target.length < 6 || target.length > 13) {
                false
            } else {
                Patterns.PHONE.matcher(target).matches()
            }
        }
    }

    /*
   Comprobar si se puede crear el usuario en Firebase
    */
    private fun checkFirebaseAuth(){
        val email = edTxtArtistEmail.text.toString()
        val pwd = edTxtArtistPwd.text.toString()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pwd).addOnCompleteListener{
            if(it.isSuccessful){
                regArtistUser(email)
                //Toast.makeText(this,R.string.CORRECT , Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,R.string.ERROR , Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*
    Registra un usuario Artista
     */
    private fun regArtistUser(email: String){
        val id = UUID.randomUUID().toString()
        val rol = Rol(1,"${Constantes.rolArtistUser}")
        var listRoles : ArrayList<Rol> = ArrayList()
        listRoles.add(rol)
        var listIdFavoritos : ArrayList<String> = ArrayList()
        val cif = edTxtArtistCif.text.toString()
        var img : String? = null
        if(photo != null){
            img = photoSt
        }

        var userName = edTxtArtistUserName.text.toString()
        var phone = edTxtArtistPhone.text.toString().toInt()

        //var user = BasicUser(id,userName,email,phone,img,listRoles,listIdFavoritos)
        var user = hashMapOf(
            "userId" to id,
            "userName" to userName,
            "email" to email,
            "phone" to phone,
            "img" to img,
            "listRoles" to listRoles,
            "listIdFavoritos" to listIdFavoritos,
            "cif" to cif,
            "latitudUbicacion" to latitudStudio,
            "longitudUbicacion" to longitudStudio

        )

        var u = BasicUser(id,userName,email,phone,img,listRoles,listIdFavoritos)
        VariablesCompartidas.usuarioBasicoActual = u

        db.collection("${Constantes.collectionArtistUser}")
            .document(id)
            .set(user)
            .addOnSuccessListener {
                val myIntent = Intent(this, BasicUserNavDrawActivity::class.java)
                startActivity(myIntent)

                //Toast.makeText(this,"GO MAIN", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,R.string.ERROR , Toast.LENGTH_SHORT).show()
            }
    }


    //+++++++++++++++++++++++++++++++
    fun cambiarFoto() {
        AlertDialog.Builder(this)
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
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                this as AppCompatActivity,
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
                    imgArtistSignUp.setImageBitmap(photo)
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
                                this.contentResolver.openInputStream(
                                    it
                                )
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        val bmp = BitmapFactory.decodeStream(imageStream)
                        photo = Bitmap.createScaledBitmap(bmp, 200, 300, true)
                        imgArtistSignUp.setImageBitmap(photo)
                        photoSt = Utils.ImageToString(photo)
                    }
                }
            }
            Constantes.CODE_MAP -> {

                if(VariablesCompartidas.latitudStudioSeleccionado != null && VariablesCompartidas.longitudStudioSeleccionado != null){
                    latitudStudio = VariablesCompartidas.latitudStudioSeleccionado.toString().toDouble()
                    longitudStudio = VariablesCompartidas.longitudStudioSeleccionado.toString().toDouble()

                    ubiActual = LatLng(latitudStudio!!,longitudStudio!!)
                    ubicacionCambiada = true

                    val ubi = LatLng(VariablesCompartidas.latitudStudioSeleccionado.toString().toDouble(), VariablesCompartidas.longitudStudioSeleccionado.toString().toDouble())
                    mMap?.clear()
                    mMap?.addMarker(MarkerOptions().position(ubi).title("${edTxtArtistUserName.text}"))
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(ubi,15f))
                }
            }
        }
    }

    //+++++++++++++++++++++++++++++++
    /*
    Carga un Map en el fragment
     */
    private fun cargarMapa() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.frm_MapLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }



    override fun onMapReady(p0: GoogleMap) {
        mMap = p0!!
        mMap.mapType=GoogleMap.MAP_TYPE_HYBRID
        val Madrid = LatLng(40.416, -3.703)
        mMap?.addMarker(MarkerOptions().position(Madrid).title("SELECT STUDIO LOCATION"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(Madrid,10f))

    }


}