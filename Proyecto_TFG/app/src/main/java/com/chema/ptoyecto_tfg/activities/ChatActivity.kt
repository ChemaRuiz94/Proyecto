package com.chema.ptoyecto_tfg.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.Comentario
import com.chema.ptoyecto_tfg.rv.AdapterRvComentarios
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatActivity : AppCompatActivity() {


    private lateinit var ed_txt_comentario : EditText
    private lateinit var flt_btn_sendComentario : FloatingActionButton
    private lateinit var txt_userName : TextView

    private var comentariosList : ArrayList<Comentario> = ArrayList()
    private lateinit var rv : RecyclerView
    private lateinit var miAdapter: AdapterRvComentarios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
    }
}