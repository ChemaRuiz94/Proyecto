package com.chema.ptoyecto_tfg.activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.models.Rol
import com.chema.ptoyecto_tfg.navigation.basic.BasicUserNavDrawActivity
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    //Firestore
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    //Components
    private lateinit var btnLogin : Button
    private lateinit var btnGoogle : Button
    private lateinit var edTxtEmailLogin : EditText
    private lateinit var edTxtPwdLogin : EditText
    private lateinit var txtSignUp : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnGoogle = findViewById(R.id.btnGoogle)
        btnLogin = findViewById(R.id.btnLogin)
        edTxtEmailLogin = findViewById(R.id.edTxtEmailLogin)
        edTxtPwdLogin = findViewById(R.id.edTxtPwdLogin)
        txtSignUp = findViewById(R.id.txtSignUp)

        auth = Firebase.auth

        //buttons
        btnLogin.setOnClickListener{
            checkLogin()
        }

        btnGoogle.setOnClickListener{

        }

        txtSignUp.setOnClickListener{
            checkSignUp()
        }
    }

    /*
    Comprueba que el login se haga correctamente
     */
    private fun checkLogin(){

        if(checkCamposVacios()){
            val email = edTxtEmailLogin.text.toString()
            val pwd = edTxtPwdLogin.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pwd).addOnCompleteListener {
                if (it.isSuccessful){

                    VariablesCompartidas.emailUsuarioActual = (it.result?.user?.email?:"")
                    findUserByEmail(email)

                } else {
                    Toast.makeText(this,R.string.LoginERROR , Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this,R.string.emptyCamps , Toast.LENGTH_SHORT).show()
        }
    }

    /*
    Comprueba que los campos esten rellenos
     */
    private fun checkCamposVacios():Boolean{

        if(edTxtEmailLogin.text.toString().trim().isEmpty()){
            return false
        }
        if(edTxtPwdLogin.text.toString().trim().isEmpty()){
            return false
        }
        return true
    }

    /*
    Pregunta al usuario con que tipo de cuenta
    desea registrarse
     */
    fun checkSignUp(){
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.user_type))
            .setMessage(getString(R.string.str_user_type))
            .setPositiveButton(getString(R.string.basic_user)) { view, _ ->
                val myIntent = Intent(this, SignUpBasicActivity::class.java)
                startActivity(myIntent)
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.artist_user)) { view, _ ->
                val myIntent = Intent(this, SignUpArtistActivity::class.java)
                startActivity(myIntent)
                view.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    /*
    find basic user
     */
    private fun findUserByEmail(email: String){

        Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
        db.collection("${Constantes.collectionUser}")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { users ->
                //Existe
                Log.d("login", "existen usuarios")
                for (user in users) {
                    var phone = 0;
                    if (user.get("phone").toString() != ""){
                        phone = user.get("phone").toString().toInt()
                    }
                    var us = BasicUser(
                        user.get("userId").toString(),
                        user.get("userName").toString(),
                        user.get("email").toString(),
                        phone,
                        user.get("img").toString(),
                        user.get("rol") as ArrayList<Rol>?,
                        user.get("idFavoritos") as ArrayList<String>?

                    )
                    VariablesCompartidas.usuarioBasicoActual = us
                    var myIntent = Intent(this, BasicUserNavDrawActivity::class.java)
                    startActivity(myIntent)
                }
            }
            .addOnFailureListener { exception ->
                //No existe
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}