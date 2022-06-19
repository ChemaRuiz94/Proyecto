package com.chema.ptoyecto_tfg.activities.ui.allBasicUser

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
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.navigation.artist.ui.BasicUserProfile.BasicUserProfileFragment
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.firebase.firestore.FirebaseFirestore

class AdapterBasicUser(
    private val context: AppCompatActivity,
    private var basicUser: ArrayList<BasicUser>,
) : RecyclerView.Adapter<AdapterBasicUser.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_user_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.imgArtistaFav.setImageBitmap(Utils.StringToBitMap((basicUser[position].img)))
        holder.nombreFav.text = basicUser[position].userName



        holder.item_user.setOnLongClickListener() {
            chooseOption(basicUser[position])
            false
        }

    }

    override fun getItemCount(): Int {
        return basicUser.size
    }

    fun addUser(user: BasicUser) {
        basicUser.add(user)
        notifyDataSetChanged()
    }

    fun updateUser(user: BasicUser, pos: Int) {
        basicUser[pos] = user
        notifyDataSetChanged()
    }

    fun removeUser(user: BasicUser) {
        basicUser.remove(user)
        notifyDataSetChanged()
    }


    private fun chooseOption(user: BasicUser) {
        AlertDialog.Builder(context).setTitle(R.string.choseOption)
            .setPositiveButton(R.string.delete) { view, _ ->
                delUser(user)
                view.dismiss()
            }.setNegativeButton(R.string.update) { view, _ ->
                updateUser(user)
                view.dismiss()
            }.create().show()
    }

    private fun delUser(user: BasicUser) {
        AlertDialog.Builder(context).setTitle(R.string.delete_this_acount)
            .setPositiveButton(R.string.delete) { view, _ ->

                val db = FirebaseFirestore.getInstance()
                db.collection("${Constantes.collectionUser}").document("${user.userId}")
                    .delete()
                removeUser(user)
                view.dismiss()
            }.setNegativeButton(R.string.Cancel) { view, _ ->
                view.dismiss()
            }.create().show()
    }

    private fun updateUser(user: BasicUser) {
        VariablesCompartidas.usuarioBasicoActual = user
        val newFragment: Fragment = BasicUserProfileFragment()
        val transaction: FragmentTransaction =
            context.getSupportFragmentManager().beginTransaction()
        transaction.replace(R.id.all_basic_user, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreFav = view.findViewById<TextView>(R.id.txt_artist_userName_item)
        val imgArtistaFav = view.findViewById<ImageView>(R.id.img_artist_user_favorite)
        val item_user = view.findViewById<View>(R.id.item_user)
    }
}