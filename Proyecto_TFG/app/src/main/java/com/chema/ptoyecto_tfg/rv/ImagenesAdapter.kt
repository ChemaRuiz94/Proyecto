package com.chema.ptoyecto_tfg.rv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.Imagen

class ImagenesAdapter (
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

    fun getSelected(): Imagen {
        return imagenes[seleccionado]
    }

    fun deseleccionar() {
        if (seleccionado != -1) {
            seleccionado = -1
            notifyDataSetChanged()
        }
    }

    fun haveSelected(): Boolean {
        return seleccionado != -1
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
                //marcarSeleccionado(imagenesAdapter, pos)
                goDetails(imagen)
            }
        }

        private fun goDetails(img : Imagen){

        }
        private fun marcarSeleccionado(imagenesAdapter: ImagenesAdapter, pos: Int) {
            seleccionado = pos
            imagenesAdapter.notifyDataSetChanged()
        }
    }

}