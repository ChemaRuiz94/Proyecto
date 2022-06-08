package com.chema.ptoyecto_tfg.activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.chema.ptoyecto_tfg.MainActivity
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.models.Rol
import com.chema.ptoyecto_tfg.navigation.basic.BasicUserNavDrawActivity
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class LoginActivity : AppCompatActivity() {

    //Firestore
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private var isBasicUser: Boolean = false
    private var RC_SIGN_IN = 1

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
            check_login_google()
        }

        txtSignUp.setOnClickListener{
            showSignUpMode()
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
                    isBasicUser = false
                    findUserByEmail(email)
                    if(!isBasicUser){
                        findArtistUserByEmail(email)
                    }

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
    fun showSignUpMode(){
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

        db.collection("${Constantes.collectionUser}")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { users ->
                //Existe
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
                    isBasicUser = true
                    var myIntent = Intent(this, BasicUserNavDrawActivity::class.java)
                    startActivity(myIntent)
                }
            }
            .addOnFailureListener { exception ->
                //No existe
                isBasicUser = false
                //Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
    /*
    find artist user
     */
    private fun findArtistUserByEmail(email: String){

        Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
        db.collection("${Constantes.collectionArtistUser}")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { users ->

                for (user in users) {
                    var phone = 0;
                    if (user.get("phone").toString() != ""){
                        phone = user.get("phone").toString().toInt()
                    }
                    var us = ArtistUser(
                        user.get("userId").toString(),
                        user.get("userName").toString(),
                        user.get("email").toString(),
                        phone,
                        user.get("img").toString(),
                        user.get("rol") as ArrayList<Rol>?,
                        user.get("idFavoritos") as ArrayList<String>?,
                        user.get("cif").toString(),
                        user.get("latitudUbicacion").toString().toDouble(),
                        user.get("longitudUbicacion").toString().toDouble(),

                    )
                    VariablesCompartidas.usuarioArtistaActual = us
                    isBasicUser = true
                    var myIntent = Intent(this, MainActivity::class.java)
                    startActivity(myIntent)
                }
            }
            .addOnFailureListener { exception ->
                //No existe
                isBasicUser = false
                //Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun check_login_google(){
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.request_id_token)) //Esto se encuentra en el archivo google-services.json: client->oauth_client -> client_id
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(this,googleConf) //Este será el cliente de autenticación de Google.
//        googleClient.signOut() //Con esto salimos de la posible cuenta  de Google que se encuentre logueada.
        val signInIntent = googleClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Si la respuesta de esta activity se corresponde con la inicializada es que viene de la autenticación de Google.
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!


                //Ya tenemos la id de la cuenta. Ahora nos autenticamos con FireBase.
                if (account != null) {
                    val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){

                            val user = auth.currentUser!!
                            VariablesCompartidas.emailUsuarioActual = user.email!!
                            regUser(account)
                            findUserByEmail(user.email!!)
                        } else {
                            Utils.showAlert(this)
                        }
                    }
                }
                //firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately

                Utils.showAlert(this)
            }
        }
    }

    private fun regUser(acount: GoogleSignInAccount){
        val id = UUID.randomUUID().toString()
        val rol = Rol(1,"${Constantes.rolBasicUser}")
        var listRoles : ArrayList<Rol> = ArrayList()
        var listIdFavoritos : ArrayList<String> = ArrayList()
        listRoles.add(rol)
        var imgBM : Bitmap? = null
        var imgSt : String? = null


        var userName = acount.displayName
        var email = acount.email
        var phone = 0

        //var user = BasicUser(id,userName,email,phone,img,listRoles,listIdFavoritos)
        var user = hashMapOf(
            "userId" to id,
            "userName" to userName,
            "email" to email,
            "phone" to phone,
            "img" to imgSt,
            "listRoles" to listRoles,
            "listIdFavoritos" to listIdFavoritos
        )

        var u = BasicUser(id,userName,email,phone,imgSt,listRoles,listIdFavoritos)
        VariablesCompartidas.usuarioBasicoActual = u

        db.collection("${Constantes.collectionUser}")
            .document(id)
            .set(user)
            .addOnSuccessListener {
                //val myIntent = Intent(this,BasicUserNavDrawActivity::class.java)
                //startActivity(myIntent)

                //Toast.makeText(this,"GO MAIN", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                //Toast.makeText(this,R.string.ERROR , Toast.LENGTH_SHORT).show()
            }
    }

}