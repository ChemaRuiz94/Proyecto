package com.chema.ptoyecto_tfg.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.Comentario
import com.chema.ptoyecto_tfg.rv.AdapterRvComentarios
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.DatePickerFragment
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private var idChat : String? = null
    private var userName : String? = null
    private var date : String? = null

    private var dateStPropuesta : String? = ""

    private lateinit var ed_txt_comentario : EditText
    private lateinit var flt_btn_sendComentario : FloatingActionButton
    private lateinit var flt_btn_send_date : FloatingActionButton
    private lateinit var txt_userName : TextView
    private lateinit var txt_fecha_chat : TextView

    private var comentariosList : ArrayList<Comentario> = ArrayList()
    private var comentariosOrdenados : ArrayList<Comentario> = ArrayList()
    private lateinit var rv : RecyclerView
    private lateinit var miAdapter: AdapterRvComentarios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        txt_userName = findViewById(R.id.txt_userName)
        txt_fecha_chat = findViewById(R.id.txt_fecha_chat)
        ed_txt_comentario = findViewById(R.id.ed_txt_comentario)
        flt_btn_sendComentario = findViewById(R.id.flt_btn_sendComentario)
        flt_btn_send_date = findViewById(R.id.flt_btn_send_date)

        cargarRV()

        if(VariablesCompartidas.usuarioArtistaActual != null){
            flt_btn_send_date.visibility = View.VISIBLE
        }

        val extras = intent.extras
        if (extras == null) {
            idChat = null
        } else {
            idChat = extras.getString("idChat")
            userName = extras.getString("userName")
            date = extras.getString("date")
            txt_userName.text = userName
            if(date!!.isNotEmpty()) {
                txt_fecha_chat.text = date
            }
        }

        getDataFromFireStore()

        flt_btn_sendComentario.setOnClickListener{
            val text  = ed_txt_comentario.text.toString()
            if(text.trim().isNotEmpty()){
                //guarda el comentario a firebase
                saveComentarioFirebase(crearComentario(text))
            }
        }

        flt_btn_send_date.setOnClickListener{
            addDateToChat()
        }
        refreshRV()
    }


    fun getDataFromFireStore()  {
        try{
            db.collection("${Constantes.collectionComentario}")
                .whereEqualTo("idChat",idChat)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Toast.makeText(this, R.string.ERROR, Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }
                    obtenerDatos(snapshots)
                }
        }catch (e : Exception){
            Toast.makeText(this, R.string.ERROR, Toast.LENGTH_SHORT).show()
            throw e
        }
    }

    private fun obtenerDatos(datos: QuerySnapshot?) {
        comentariosList.clear()
        for(dc: DocumentChange in datos?.documentChanges!!){
            if (dc.type == DocumentChange.Type.ADDED){

                var idComentario : String? = null
                if(dc.document.get("idComentario") != null){
                    idComentario = dc.document.get("idComentario").toString()
                }
                var idChat : String? = null
                if(dc.document.get("idChat") != null){
                    idChat = dc.document.get("idChat").toString()
                }

                var idUser : String? = null
                if(dc.document.get("idUser") != null){
                    idUser = dc.document.get("idUser").toString()
                }
                var comentario : String? = null
                if(dc.document.get("comentario") != null){
                    comentario = dc.document.get("comentario").toString()
                }
                var userNameAutor : String? = null
                if(dc.document.get("userNameAutor") != null){
                    userNameAutor = dc.document.get("userNameAutor").toString()
                }

                var com = Comentario(
                    idComentario,
                    idChat,
                    idUser,
                    userNameAutor,
                    comentario,
                    dc.document.get("horaComentario").toString().toInt(),
                    dc.document.get("minComentario").toString().toInt(),
                    dc.document.get("segComentario").toString().toInt(),
                    dc.document.get("diaComentario").toString().toInt(),
                    dc.document.get("mesComentario").toString().toInt(),
                    dc.document.get("yearComentario").toString().toInt()
                )
//                comentariosList.add(com)
                miAdapter.addComentario(com)
                rv.scrollToPosition(miAdapter.itemCount - 1)

            }
        }
    }

    fun saveComentarioFirebase(coment: Comentario){
        //guardamos la opinion en firebase
        db.collection("${Constantes.collectionComentario}")
            .document(coment.idComentario.toString()) //Será la clave del documento.
            .set(coment).addOnSuccessListener {
                if(VariablesCompartidas.stDatePropuesta != null){
                    dateStPropuesta = VariablesCompartidas.stDatePropuesta
                    if(coment.comentario.equals(VariablesCompartidas.stDatePropuesta.toString())){
                        sendDate()
                    }
                }
//                miAdapter.addComentario(coment)
                ed_txt_comentario.setText("")
            }.addOnFailureListener{
                Toast.makeText(this, getString(R.string.ERROR), Toast.LENGTH_SHORT).show()
            }
    }

    private fun addDateToChat(){
        VariablesCompartidas.stDatePropuesta = null
        dateStPropuesta = ""
        val newFragment = DatePickerFragment(ed_txt_comentario)
        newFragment.show(supportFragmentManager, "datePicker")

    }

    private fun sendDate(){
        db.collection("${Constantes.collectionChat}")
            .document("${idChat.toString()}")
            .update("date","${dateStPropuesta.toString()}").addOnFailureListener{
                Toast.makeText(this, getString(R.string.ERROR), Toast.LENGTH_SHORT).show()
            }

    }
    private fun crearComentario(txt : String):Comentario{
        val idComentario : String = UUID.randomUUID().toString()
        val idChat  = idChat
        var idUser : String? = null
        var userNameAutor : String? = null
        if(VariablesCompartidas.usuarioBasicoActual != null){
            idUser = VariablesCompartidas.usuarioBasicoActual!!.userId.toString()
            userNameAutor = VariablesCompartidas.usuarioBasicoActual!!.userName.toString()
        }else{

            idUser = VariablesCompartidas.usuarioArtistaActual!!.userId.toString()
            userNameAutor = VariablesCompartidas.usuarioArtistaActual!!.userName.toString()
        }
        val coment = txt
        val fecha = Calendar.getInstance()
        val hora = fecha.get(Calendar.HOUR)
        val min = fecha.get(Calendar.MINUTE)
        val seg = fecha.get(Calendar.SECOND)
        val dia = fecha.get(Calendar.DAY_OF_MONTH)
        val mes = fecha.get(Calendar.MONTH)
        val year = fecha.get(Calendar.YEAR)
        return Comentario(idComentario,idChat,idUser,userNameAutor,coment,hora,min,seg,dia,mes,year)
    }


    private fun cargarRV(){
        rv = findViewById(R.id.rv_chat)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        miAdapter = AdapterRvComentarios(this, comentariosOrdenados)
        rv.adapter = miAdapter
        //scroll to bottom
    }

    private fun refreshRV(){
        comentariosOrdenados.clear()
        comentariosOrdenados =  ordenarComentarios()
        cargarRV()
    }

    private fun ordenarComentarios() : ArrayList<Comentario>{
        var coments = ArrayList<Comentario>()
        val opiniOrdenadas = comentariosList.sortedWith(compareBy({ it.yearComentario }, { it.mesComentario },{it.diaComentario},{ it.horaComentario },{it.minComentario},{it.segComentario}))

        for (opi in opiniOrdenadas){
            coments.add(opi)
        }
        return coments
    }
}