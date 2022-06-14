package com.chema.ptoyecto_tfg.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.Comentario

class AdapterRvComentarios  (
    private val context: AppCompatActivity,
    private val opiniones: ArrayList<Comentario>
) : RecyclerView.Adapter<AdapterRvComentarios.ViewHolder>() {

    override fun getItemCount(): Int {
        return opiniones.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRvComentarios.ViewHolder {

        return AdapterRvComentarios.ViewHolder(

            LayoutInflater.from(context).inflate(R.layout.item_comentario_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AdapterRvComentarios.ViewHolder, position: Int) {

        var comentario: Comentario = opiniones[position]

        val autor = comentario.userNameAutor

        //AQUI PONEMOS LA FECHA
        val hora = comentario.horaComentario
        val min = comentario.minComentario
        val dia = comentario.diaComentario
        val mon = comentario.yearComentario
        val fechaST = "${dia}/${mon} ${hora}:${min}"

        holder.txt_hora_comentario.text = (fechaST)
        holder.txt_nombreUser_comentario.text = (autor)

        if(comentario.comentario != null){

            holder.ed_txt_multiline_comentario.setText(comentario.comentario)

        }

        Toast.makeText(context,"ID CHAT -> ${comentario.idComentario.toString()}", Toast.LENGTH_SHORT).show()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val ed_txt_multiline_comentario = view.findViewById<EditText>(R.id.ed_txt_multiline_comentario)
        val txt_hora_comentario = view.findViewById<TextView>(R.id.txt_hora_comentario)
        val txt_nombreUser_comentario = view.findViewById<TextView>(R.id.txt_nombreUser_comentario)

    }
}