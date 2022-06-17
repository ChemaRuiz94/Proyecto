package com.chema.ptoyecto_tfg.rv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.google.firebase.firestore.FirebaseFirestore

class AdapterRvEtiquetas(
    private var context: AppCompatActivity,
    private var etiquetas: ArrayList<String>
) : RecyclerView.Adapter<AdapterRvEtiquetas.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_etiqueta, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemCount > 0) {
            holder.txt_nombreEtiqueta.text = "#${etiquetas[position]}"
        }
        holder.item.setOnLongClickListener {
            showAlert(etiquetas[position])
            false
        }
    }

    override fun getItemCount(): Int {
       return etiquetas.size
    }

    fun addTag(tag: String) {
        etiquetas.add(tag)
        notifyDataSetChanged()
    }

    private fun removeTag(tag: String){
        etiquetas.remove(tag)
        notifyDataSetChanged()
    }

    fun getTags(): ArrayList<String> {
        return etiquetas
    }

    private fun showAlert(tag: String) {
        AlertDialog.Builder(context).setTitle(R.string.remove_tag)
            .setPositiveButton(R.string.delete) { view, _ ->
                removeTag(tag)
                Toast.makeText(context, R.string.Suscesfull, Toast.LENGTH_SHORT).show()
                view.dismiss()
            }.setNegativeButton(R.string.Cancel) { view, _ ->//cancela
                view.dismiss()
            }.create().show()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txt_nombreEtiqueta = view.findViewById<TextView>(R.id.txt_name_tag)
        val item = view.findViewById<ConstraintLayout>(R.id.item_etiqueta)

    }

}