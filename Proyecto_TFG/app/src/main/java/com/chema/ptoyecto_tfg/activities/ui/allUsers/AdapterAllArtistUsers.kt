package com.chema.ptoyecto_tfg.activities.ui.allUsers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.activities.ArtistMuroConatinerActivity
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.navigation.artist.ui.ArtistUserProfile.ArtistUserProfileFragment
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.firebase.firestore.FirebaseFirestore

class AdapterAllArtistUsers(
    private val context: AppCompatActivity,
    private var artistUser: ArrayList<ArtistUser>,
) : RecyclerView.Adapter<AdapterAllArtistUsers.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_user_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.imgArtistaFav.setImageBitmap(Utils.StringToBitMap((artistUser[position].img)))
        holder.nombreFav.text = artistUser[position].userName

        holder.item_user.setOnClickListener {
            VariablesCompartidas.idUserArtistVisitMode = artistUser[position].userId.toString()
            VariablesCompartidas.usuarioArtistaVisitaMuro = artistUser[position]
            goToArtistMuroContainer()
        }

        holder.item_user.setOnLongClickListener() {
            chooseOption(artistUser[position])
            false
        }

    }

    override fun getItemCount(): Int {
        return artistUser.size
    }

    fun addUser(user: ArtistUser) {
        artistUser.add(user)
        notifyDataSetChanged()
    }

    fun updateUser(user: ArtistUser, pos: Int) {
        artistUser[pos] = user
        notifyDataSetChanged()
    }

    fun removeUser(user: ArtistUser) {
        artistUser.remove(user)
        notifyDataSetChanged()
    }

    private fun goToArtistMuroContainer() {
        val intent = Intent(context, ArtistMuroConatinerActivity::class.java)
        context.startActivity(intent)
    }

    private fun chooseOption(user: ArtistUser) {
        AlertDialog.Builder(context).setTitle(R.string.choseOption)
            .setPositiveButton(R.string.delete) { view, _ ->
                delUser(user)
                view.dismiss()
            }.setNegativeButton(R.string.update) { view, _ ->
                updateUser(user)
                view.dismiss()
            }.create().show()
    }

    private fun delUser(user: ArtistUser) {
        AlertDialog.Builder(context).setTitle(R.string.delete_this_acount)
            .setPositiveButton(R.string.delete) { view, _ ->

                val db = FirebaseFirestore.getInstance()
                db.collection("${Constantes.collectionArtistUser}").document("${user.userId}")
                    .delete()
                removeUser(user)
                view.dismiss()
            }.setNegativeButton(R.string.Cancel) { view, _ ->
                view.dismiss()
            }.create().show()
    }

    private fun updateUser(user: ArtistUser) {
        VariablesCompartidas.usuarioArtistaActual = user
        val newFragment: Fragment = ArtistUserProfileFragment()
        val transaction: FragmentTransaction =
            context.getSupportFragmentManager().beginTransaction()
        transaction.replace(R.id.all_users, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreFav = view.findViewById<TextView>(R.id.txt_artist_userName_item)
        val imgArtistaFav = view.findViewById<ImageView>(R.id.img_artist_user_favorite)
        val item_user = view.findViewById<View>(R.id.item_user)
    }
}