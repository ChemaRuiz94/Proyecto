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
//import android.R
import android.content.Intent
import com.chema.ptoyecto_tfg.activities.ArtistMuroConatinerActivity
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas


class AdapterRvFavorites (
    private val context: AppCompatActivity,
    private val usuariosFav: ArrayList<ArtistUser>?,
) : RecyclerView.Adapter<AdapterRvFavorites.ViewHolder>()  {

    override fun getItemCount(): Int {
        return usuariosFav!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_user_layout, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder?.item.text = this.valores!![position].toString()
        var usuario: ArtistUser = usuariosFav!![position]
        holder.nombreFav.text = usuario.userName
        holder.imgArtistaFav.setImageBitmap(Utils.StringToBitMap(usuario.img))

        holder.nombreFav.setOnClickListener{
            VariablesCompartidas.userArtistVisitMode = true
            VariablesCompartidas.idUserArtistVisitMode = usuario.userId.toString()
            VariablesCompartidas.usuarioArtistaVisitaMuro = usuario
            goToArtistMuroContainer()
        }

        holder.imgArtistaFav.setOnClickListener{
            VariablesCompartidas.userArtistVisitMode = true
            VariablesCompartidas.idUserArtistVisitMode = usuario.userId.toString()
            VariablesCompartidas.usuarioArtistaVisitaMuro = usuario
            goToArtistMuroContainer()
        }
    }

//    val nextFrag = MuroFragment()
//    val fragmentManager = context.supportFragmentManager
//    val fragmentTransaction = fragmentManager.beginTransaction()
//
//    fragmentTransaction.replace(R.id.fragment_favorites, nextFrag, null)
//    fragmentTransaction.addToBackStack(android.R.attr.tag.toString())
//    fragmentTransaction.commitAllowingStateLoss()
    private fun goToArtistMuroContainer(){
        val intent = Intent(context, ArtistMuroConatinerActivity::class.java)
        context.startActivity(intent)
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nombreFav = view.findViewById<TextView>(R.id.txt_artist_userName_item)
        val imgArtistaFav = view.findViewById<ImageView>(R.id.img_artist_user_favorite)
    }
}
