package com.chema.ptoyecto_tfg.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.rv.AdapterRvFavorites
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.Intent




class ListResutlActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private lateinit var rv : RecyclerView
    private var resultados : ArrayList<ArtistUser>? = ArrayList()
    private lateinit var miAdapter: AdapterRvFavorites

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_resutl)

        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        resultados = args!!.getSerializable("USER_LIST") as ArrayList<ArtistUser>?
        cargarRV()
    }

    private fun cargarRV(){

        rv = findViewById(R.id.rv_list_result)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        miAdapter = AdapterRvFavorites(this,  resultados)
        rv.adapter = miAdapter

    }
}