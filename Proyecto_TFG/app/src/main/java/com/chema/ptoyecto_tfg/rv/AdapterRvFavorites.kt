package com.chema.ptoyecto_tfg.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.utils.Utils

class AdapterRvFavorites (
    private val context: AppCompatActivity,
    private val usuariosFav: ArrayList<ArtistUser>,
    private val editMode: Boolean
) : RecyclerView.Adapter<AdapterRvFavorites.ViewHolder>()  {

    override fun getItemCount(): Int {
        return usuariosFav.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_user_favorite_layout, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder?.item.text = this.valores!![position].toString()
        var usuario: ArtistUser = usuariosFav[position]
        holder.nombreFav.text = usuario.userName
        holder.imgArtistaFav.setImageBitmap(Utils.StringToBitMap(usuario.img))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nombreFav = view.findViewById<TextView>(R.id.txt_artist_userName_item)
        val imgArtistaFav = view.findViewById<ImageView>(R.id.img_artist_user_favorite)
    }
}
