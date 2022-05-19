package com.chema.ptoyecto_tfg.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.chema.ptoyecto_tfg.MainActivity
import com.chema.ptoyecto_tfg.R
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

        }

        btnGoogle.setOnClickListener{

        }

        txtSignUp.setOnClickListener{
            val myIntent = Intent(this, SignUpActivity::class.java)
            startActivity(myIntent)
        }
    }
}