package com.chema.ptoyecto_tfg.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chema.ptoyecto_tfg.MainActivity
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.models.Rol
import com.chema.ptoyecto_tfg.utils.Constantes
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class SignUpActivity : AppCompatActivity() {


    private val db = FirebaseFirestore.getInstance()

    private lateinit var edTxtUserNameSignUpUser : EditText
    private lateinit var edTxtEmailSignUpUser : EditText
    private lateinit var edTxtPwdSignUpUser : EditText
    private lateinit var edTxtPwdReapaetSignUpUser : EditText
    private lateinit var edTxtPhoneSignUpUser : EditText
    private lateinit var btnSignUpUser : Button
    private lateinit var imgSignUpUser : ImageView


    private var photo: Bitmap? = null
    private var photoSt: String? = null


    //******************************************************************
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        edTxtUserNameSignUpUser = findViewById(R.id.edTxtUserNameSignUpUser)
        edTxtEmailSignUpUser = findViewById(R.id.edTxtEmailSignUpUser)
        edTxtPwdSignUpUser = findViewById(R.id.edTxtPwdSignUpUser)
        edTxtPwdReapaetSignUpUser = findViewById(R.id.edTxtPwdReapaetSignUpUser)
        edTxtPhoneSignUpUser = findViewById(R.id.edTxtPhoneSignUpUser)
        btnSignUpUser = findViewById(R.id.btnSignUpUser)
        imgSignUpUser = findViewById(R.id.imgSignUpUser)

        btnSignUpUser.setOnClickListener{
            checkSignUp()
        }

        imgSignUpUser.setOnClickListener{
            cambiarFoto()
        }
    }
    //******************************************************************

    /*
    Comprueba que se realice correctamente el SignUp
     */
    private fun checkSignUp(){
        //comprueba campos vacios
        if(checkCamposVacios()){

            //comprueba contraseñas iguales
            if(checkPwdIguales()){

                //comprueba si el formato de numero de telefono es correcto
                if(checkMovil(edTxtPhoneSignUpUser.text.trim())){

                    checkFirebaseAuth()

                }else{
                    Toast.makeText(this,R.string.phoneNotCorrect,Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this,R.string.pwdNotCoincide,Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this, R.string.emptyCamps,Toast.LENGTH_SHORT).show()
        }
    }

    /*
    Devuelve true si todos los campos estan rellenos
     */
    private fun checkCamposVacios():Boolean{
        if(edTxtUserNameSignUpUser.text.toString().trim().isEmpty()){
            return false
        }
        if(edTxtEmailSignUpUser.text.toString().trim().isEmpty()){
            return false
        }
        if(edTxtPwdSignUpUser.text.toString().trim().isEmpty()){
            return false
        }
        if(edTxtPwdReapaetSignUpUser.text.toString().trim().isEmpty()){
            return false
        }
        if(edTxtPhoneSignUpUser.text.toString().trim().isEmpty()){
            return false
        }
        return true
    }

    /*
    Funcion que comprueba si las dos contraseñas son iguales
     */
    fun checkPwdIguales():Boolean{
        if(edTxtPwdSignUpUser.text.toString().trim().equals(edTxtPwdReapaetSignUpUser.text.toString().trim())){
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
        val email = edTxtEmailSignUpUser.text.toString()
        val pwd = edTxtPwdSignUpUser.text.toString()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pwd).addOnCompleteListener{
            if(it.isSuccessful){
                regUser(email)
                //Toast.makeText(this,R.string.CORRECT , Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,R.string.ERROR , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun regUser(email: String){
        val id = UUID.randomUUID().toString()
        val rol = Rol(1,"${Constantes.rolBasicUser}")
        var listRoles : ArrayList<Rol> = ArrayList()
        var listIdFavoritos : ArrayList<String> = ArrayList()
        listRoles.add(rol)
        var img : String? = null
        if(photo != null){
            img = photoSt
        }

        var userName = edTxtUserNameSignUpUser.text.toString()
        var phone = edTxtPhoneSignUpUser.text.toString().toInt()

        //var user = BasicUser(id,userName,email,phone,img,listRoles,listIdFavoritos)
        var user = hashMapOf(
            "userId" to id,
            "userName" to userName,
            "email" to email,
            "phone" to phone,
            "img" to img,
            "listRoles" to listRoles,
            "listIdFavoritos" to listIdFavoritos
        )

        db.collection("${Constantes.collectionUser}")
            .document(id)
            .set(user)
            .addOnSuccessListener {
                val myIntent = Intent(this,MainActivity::class.java)
                startActivity(myIntent)

                Toast.makeText(this,"GO MAIN", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,R.string.ERROR , Toast.LENGTH_SHORT).show()
            }
    }

    //++++++++++++++ FOTO ++++++++++++++++++
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
                    imgSignUpUser.setImageBitmap(photo)
                    photoSt = ImageToString(photo)
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
                        imgSignUpUser.setImageBitmap(photo)
                        photoSt = ImageToString(photo)
                    }
                }
            }
        }
    }

    fun ImageToString(bitmap: Bitmap?):String?{
        val baos = ByteArrayOutputStream()
        //val bitmap : Bitmap = imgUsuarioPerfil.drawToBitmap()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        var imageString : String? = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        return imageString
    }

    fun StringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }
}