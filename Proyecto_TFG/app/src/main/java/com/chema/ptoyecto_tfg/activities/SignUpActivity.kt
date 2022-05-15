package com.chema.ptoyecto_tfg.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.chema.ptoyecto_tfg.R
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var edTxtUserNameSignUpUser : EditText
    private lateinit var edTxtEmailSignUpUser : EditText
    private lateinit var edTxtPwdSignUpUser : EditText
    private lateinit var edTxtPwdReapaetSignUpUser : EditText
    private lateinit var edTxtPhoneSignUpUser : EditText
    private lateinit var btnSignUpUser : Button


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

        btnSignUpUser.setOnClickListener{
            checkSignUp()
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

    private fun checkFirebaseAuth(){
        val email = edTxtEmailSignUpUser.text.toString()
        val pwd = edTxtPwdSignUpUser.text.toString()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pwd).addOnCompleteListener{
            if(it.isSuccessful){
                //
            }
        }
    }
}