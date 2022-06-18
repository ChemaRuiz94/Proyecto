package com.chema.ptoyecto_tfg.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.Comentario
import com.chema.ptoyecto_tfg.utils.Constantes
import com.google.firebase.firestore.FirebaseFirestore

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
        val idComent = comentario.idComentario
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

        holder.ed_txt_multiline_comentario.setOnLongClickListener{
            delComentario(comentario)
            false
        }

        holder.item_opinion.setOnLongClickListener{
            delComentario(comentario)
            false
        }
    }

    fun addComentario(comentario: Comentario){
        opiniones.add(comentario)
        notifyDataSetChanged()
    }

    fun delComentario(comentario: Comentario){
        delComent(comentario)
        opiniones.remove(comentario)
        notifyDataSetChanged()
    }

    private fun delComent(comentario: Comentario){
        AlertDialog.Builder(context).setTitle(R.string.del_coment)
            .setPositiveButton(R.string.delete) { view, _ ->

                val db = FirebaseFirestore.getInstance()
                db.collection("${Constantes.collectionComentario}").document("${comentario.idComentario.toString()}").delete()

                view.dismiss()
            }.setNegativeButton(R.string.Cancel) { view, _ ->
                view.dismiss()
            }.create().show()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val ed_txt_multiline_comentario = view.findViewById<EditText>(R.id.ed_txt_multiline_comentario)
        val txt_hora_comentario = view.findViewById<TextView>(R.id.txt_hora_comentario)
        val txt_nombreUser_comentario = view.findViewById<TextView>(R.id.txt_nombreUser_comentario)
        val item_opinion = view.findViewById<View>(R.id.item_opinion)

    }
}