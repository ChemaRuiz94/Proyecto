package com.chema.ptoyecto_tfg.rv

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.activities.ChatActivity
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.Chat
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas

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
           goToChat(idChat,name,date)
        }

    }

    private fun goToChat(idChat: String?, userName : String?, date : String?){
        val myIntent = Intent(context, ChatActivity::class.java)
        myIntent.putExtra("idChat",idChat)
        myIntent.putExtra("userName",userName)
        myIntent.putExtra("date",date)
        context.startActivity(myIntent)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txt_userName = view.findViewById<TextView>(R.id.txt_userName_date)
        val txt_date = view.findViewById<TextView>(R.id.txt_date)

    }
}