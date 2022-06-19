package com.chema.ptoyecto_tfg.rv

import android.content.Intent
import android.graphics.Color
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
import com.chema.ptoyecto_tfg.activities.DetailActivity
import com.chema.ptoyecto_tfg.models.Imagen
import com.chema.ptoyecto_tfg.models.Post
import com.chema.ptoyecto_tfg.utils.Constantes
import com.google.firebase.firestore.FirebaseFirestore

class ImagenesAdapter(
    var context: AppCompatActivity,
    var imagenes: ArrayList<Imagen>,
) :
    RecyclerView.Adapter<ImagenesAdapter.ViewHolder>() {

    companion object {
        var seleccionado: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.imagenes_item, parent, false), context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = imagenes[position]
        holder.bind(item, context, position, this)
    }

    override fun getItemCount(): Int {
        return imagenes.size
    }


    fun deseleccionar() {
        if (seleccionado != -1) {
            seleccionado = -1
            notifyDataSetChanged()
        }
    }

    fun removeImg(img: Imagen) {
        imagenes.remove(img)
        notifyDataSetChanged()
    }


    class ViewHolder(view: View, val ventana: AppCompatActivity) :
        RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgItem)

        fun bind(
            imagen: Imagen,
            context: AppCompatActivity,
            pos: Int,
            imagenesAdapter: ImagenesAdapter
        ) {
            img.setImageBitmap(imagen.img)
            if (pos == seleccionado) {
                with(itemView) { setBackgroundColor(Color.GRAY) }
            } else {
                with(itemView) { setBackgroundColor(Color.WHITE) }
            }
            itemView.setOnClickListener {
                //go details
                if (imagen.nombre != null) {
                    val nombre = imagen.nombre.toString()
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("stImage", nombre.toString())

                    itemView.context.startActivity(intent)
                }
            }

            itemView.setOnLongClickListener {
                delPost(context, imagen, imagenesAdapter)
                false
            }
        }

        private fun marcarSeleccionado(imagenesAdapter: ImagenesAdapter, pos: Int) {
            seleccionado = pos
            imagenesAdapter.notifyDataSetChanged()
        }

        private fun delPost(
            context: AppCompatActivity,
            img: Imagen,
            imagenesAdapter: ImagenesAdapter
        ) {
            AlertDialog.Builder(context).setTitle(R.string.delete_this_post)
                .setPositiveButton(R.string.delete) { view, _ ->

                    val db = FirebaseFirestore.getInstance()
                    db.collection("${Constantes.collectionPost}")
                        .whereEqualTo("imgId", "${img.nombre}").get()
                        .addOnSuccessListener { posts ->
                            var postId = ""
                            for (p in posts) {
                                if (p.get("postId").toString() != "") {
                                    postId = p.get("postId").toString()
                                }
                            }
                            if (postId != "") {
                                db.collection("${Constantes.collectionPost}")
                                    .document("${postId.toString()}").delete()
                                imagenesAdapter.removeImg(img)
                            }
                        }
                    //removePost(post)
                    view.dismiss()
                }.setNegativeButton(R.string.Cancel) { view, _ ->
                    view.dismiss()
                }.create().show()
        }
    }

}