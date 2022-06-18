package com.chema.ptoyecto_tfg.rv

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.activities.ChatActivity
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.Chat
import com.chema.ptoyecto_tfg.models.Comentario
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class AdapterRvCitas (
    private val context: AppCompatActivity,
    private val allChats: ArrayList<Chat>?,
) : RecyclerView.Adapter<AdapterRvCitas.ViewHolder>() {


    override fun getItemCount(): Int {
        return allChats!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRvCitas.ViewHolder {

        return AdapterRvCitas.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_cita_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AdapterRvCitas.ViewHolder, position: Int) {
        //holder?.item.text = this.valores!![position].toString()
        val chat : Chat = allChats!![position]
        var name : String? = null
        val idChat = chat.idChat
        val date = chat.date

        if(VariablesCompartidas.usuarioArtistaActual != null){
            /*
            if(name.equals(VariablesCompartidas.usuarioArtistaActual!!.userName)){
                name = chat.userNameOther
            }
             */
            if(chat.idUserOther.toString().equals(VariablesCompartidas.idUsuarioActual)){
                name = chat.userNameArtist
            }else{
                name = chat.userNameOther
            }
        }else{
            name = chat.userNameArtist
        }

        holder.txt_userName.text = name

        if(chat.date != null){
            holder.txt_date.text = date
        }else{
            holder.txt_date.text = ""
        }

        holder.txt_userName.setOnClickListener{
           goToChat(idChat,name,chat.idUserOther!!, date)
        }

        holder.txt_userName.setOnLongClickListener{
            delChat(idChat)
            this.notifyDataSetChanged()
            false
        }

    }

    private fun goToChat(idChat: String?, userName : String?, idOther : String , date : String?){
        val myIntent = Intent(context, ChatActivity::class.java)
        myIntent.putExtra("idChat",idChat)
        myIntent.putExtra("userName",userName)
        myIntent.putExtra("idOther",idOther)
        myIntent.putExtra("date",date)
        context.startActivity(myIntent)
    }

    private fun delChat(idChat: String?){
        AlertDialog.Builder(context).setTitle(R.string.del_chat)
            .setPositiveButton(R.string.delete) { view, _ ->

                val db = FirebaseFirestore.getInstance()

                checkEliminarComentarios(idChat)
                db.collection("${Constantes.collectionChat}").document("${idChat}").delete()

                view.dismiss()
            }.setNegativeButton(R.string.Cancel) { view, _ ->
                view.dismiss()
            }.create().show()
    }

    private fun checkEliminarComentarios(idChat: String?) {
        runBlocking {
            val job : Job = launch(context = Dispatchers.Default) {
                val datos : QuerySnapshot = getDataFromFireStore(idChat) as QuerySnapshot
                delComents(datos as QuerySnapshot?)
            }
            job.join()
        }
    }


    suspend fun getDataFromFireStore(idChat: String?)  : QuerySnapshot? {

        val db = FirebaseFirestore.getInstance()
        return try{
            val data = db.collection("${Constantes.collectionComentario}")
                .whereEqualTo("idChat",idChat)
                .get()
                .await()
            data
        }catch (e : Exception){
            null
        }
    }

    private fun delComents(datos: QuerySnapshot?) {
        var coments = ArrayList<Comentario>()
        val db = FirebaseFirestore.getInstance()
        for(dc: DocumentChange in datos?.documentChanges!!){
            if (dc.type == DocumentChange.Type.ADDED){

                var come = Comentario(
                    dc.document.get("idComentario").toString(),
                    dc.document.get("idChat").toString(),
                    dc.document.get("idUser").toString(),
                    dc.document.get("userNameAutor").toString(),
                    dc.document.get("comentario").toString(),
                    dc.document.get("horaComentario").toString().toInt(),
                    dc.document.get("minComentario").toString().toInt(),
                    dc.document.get("segComentario").toString().toInt(),
                    dc.document.get("diaComentario").toString().toInt(),
                    dc.document.get("yearComentario").toString().toInt()
                )
                coments.add(come)
            }
        }
        for (c in coments){
            db.collection("${Constantes.collectionComentario}").document("${c.idComentario.toString()}").delete()
        }
    }

    //+++++++++++++++++++++++++++++++++++++++++++
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txt_userName = view.findViewById<TextView>(R.id.txt_userName_date)
        val txt_date = view.findViewById<TextView>(R.id.txt_date)

    }

}