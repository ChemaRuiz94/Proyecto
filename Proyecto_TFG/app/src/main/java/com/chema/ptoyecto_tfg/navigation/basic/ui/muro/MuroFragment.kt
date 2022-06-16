package com.chema.ptoyecto_tfg.navigation.basic.ui.muro

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.TabBasicUserActivity
import com.chema.ptoyecto_tfg.activities.ArtistMuroConatinerActivity
import com.chema.ptoyecto_tfg.activities.ChatActivity
import com.chema.ptoyecto_tfg.activities.DetailActivity
import com.chema.ptoyecto_tfg.activities.SignUpBasicActivity
import com.chema.ptoyecto_tfg.databinding.FragmentMuroBinding
import com.chema.ptoyecto_tfg.models.*
import com.chema.ptoyecto_tfg.rv.AdapterRvFavorites
import com.chema.ptoyecto_tfg.rv.AdapterRvPostAritstMuro
import com.chema.ptoyecto_tfg.rv.ImagenesAdapter
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sign
import kotlin.random.Random

class MuroFragment : Fragment() {

    private lateinit var muroViewModel: MuroViewModel
    private var _binding: FragmentMuroBinding? = null

    private val binding get() = _binding!!

    private var favorite: Boolean = false
    private var userMuro: ArtistUser? = null
    private var userArtistActual: ArtistUser? = null
    private var userBasicActual: BasicUser? = null

    private val db = Firebase.firestore
    private lateinit var myStorage: StorageReference
    var stId = ""
    lateinit var viewAux: View
    var storage = Firebase.storage
    var storageRef = storage.reference
    private var photo: Bitmap? = null
    var postList: ArrayList<Post> = ArrayList<Post>()
    var postIdList: ArrayList<String> = ArrayList<String>()
    var images: ArrayList<Imagen> = ArrayList()
    lateinit var adaptador: ImagenesAdapter

    private lateinit var fltBtnFavCamera: FloatingActionButton
    private lateinit var txtUserName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var ed_txt_prizes_sizes: EditText
    private lateinit var txtUbi: TextView
    private lateinit var txtWeb: TextView
    private lateinit var btnContactEdit: Button
    private lateinit var imgArtist: ImageView


    private var editMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        muroViewModel =
            ViewModelProvider(this).get(MuroViewModel::class.java)

        _binding = FragmentMuroBinding.inflate(inflater, container, false)
        val root: View = binding.root

        storage = Firebase.storage("gs://proyecto-tfg-e2f22.appspot.com")
        myStorage = storage.getReference()
        //myStorage = FirebaseStorage.getInstance().getReference()

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewAux = view
        fltBtnFavCamera = view.findViewById(R.id.fl_btn_fav_artist_muro)
        btnContactEdit = view.findViewById(R.id.btn_contact_edit)
        txtUserName = view.findViewById(R.id.txt_artist_user_name_muro)
        txtEmail = view.findViewById(R.id.txt_email_artist_muro)
        imgArtist = view.findViewById(R.id.img_user_artist_muro)
        ed_txt_prizes_sizes = view.findViewById(R.id.ed_txt_prizes_sizes)

        storage = Firebase.storage("gs://proyecto-tfg-e2f22.appspot.com")
        myStorage = storage.getReference()
        //myStorage = FirebaseStorage.getInstance().getReference()
        adaptador = ImagenesAdapter(view.context as AppCompatActivity, images)


        fltBtnFavCamera.setOnClickListener {
            changeFavCamera()
        }

        btnContactEdit.setOnClickListener {
            changeContactEdit()
        }

        if (userMuro == null) {
            cargarDatosArtist()
            getPost(view)
            getImgStorage()
            //VariablesCompartidas.usuarioArtistaVisitaMuro = userMuro
        }

    }

    override fun onResume() {
        super.onResume()
        cargarDatosArtist()
        getPost(viewAux)
        getImgStorage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        VariablesCompartidas.userArtistVisitMode = false
        VariablesCompartidas.idUserArtistVisitMode = null
        userMuro = null
        VariablesCompartidas.usuarioArtistaVisitaMuro = null
        ArtistMuroConatinerActivity.fa!!.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        VariablesCompartidas.userArtistVisitMode = false
        VariablesCompartidas.idUserArtistVisitMode = null
        userMuro = null
        VariablesCompartidas.usuarioArtistaVisitaMuro = null
        ArtistMuroConatinerActivity.fa!!.finish()
    }

    //++++++++++++++++++++++++++++++++++++++++++++++

    private fun cargarDatosArtist() {
        if (VariablesCompartidas.usuarioArtistaVisitaMuro != null) {
            userMuro = VariablesCompartidas.usuarioArtistaVisitaMuro

            if (userMuro!!.img != null) {
                imgArtist.setImageBitmap(Utils.StringToBitMap(userMuro!!.img.toString()))
            }

            if (userMuro!!.prices != null && userMuro!!.sizes != null) {
                for (price in userMuro!!.prices!!) {
                    val n = userMuro!!.prices!!.indexOf(price)
                    val size = userMuro!!.sizes!![n]
                    val st =
                        " [ ${price.toString()}€ <> ${size.toString()}x${size.toString()}cm ] \n"
                    ed_txt_prizes_sizes.text.append(st)
                }
            }


            txtUserName.text = (userMuro!!.userName.toString())
            txtEmail.text = (userMuro!!.email.toString())
            btnContactEdit.setText(R.string.contact)
            fltBtnFavCamera.visibility = View.VISIBLE
            checkFav()

            /*
            if (VariablesCompartidas.usuarioArtistaActual != null && userMuro!!.userId!!.equals(
                    VariablesCompartidas!!.usuarioArtistaActual!!.userId
                )
            ) {
                checkFav()
                btnContactEdit.visibility = View.INVISIBLE
                fltBtnFavCamera.visibility = View.INVISIBLE
            }
             */
        }
        if (VariablesCompartidas.idUserArtistVisitMode == null && VariablesCompartidas.usuarioArtistaActual != null) {
            userMuro = VariablesCompartidas.usuarioArtistaActual
            txtUserName.setText(userMuro!!.userName.toString())
            btnContactEdit.setText(R.string.edit_muro)
            txtEmail.text = (userMuro!!.email.toString())
            fltBtnFavCamera.setImageResource(R.drawable.ic_menu_camera)
            imgArtist.setImageBitmap(Utils.StringToBitMap(userMuro!!.img.toString()))
            editMode = true

            for (price in userMuro!!.prices!!) {
                val n = userMuro!!.prices!!.indexOf(price)
                val size = userMuro!!.sizes!![n]
                val st = " [ ${price.toString()}€ <> ${size.toString()}x${size.toString()}cm ] \n"
                ed_txt_prizes_sizes.text.append(st)
            }
        }

    }


    //+++++++++++++++++++++++++++++++++++++++++++++++++++


    private fun getPost(view: View) {
        postList.clear()
        postIdList.clear()
        images.clear()
        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                val datos = getDataFromFireStore()
                obtenerDatos(datos)
            }
            job.join()
        }
        //getUserImagesStorage()
        //getImgStorage()
        //cargarRV(view)
    }

    /*
    Buscamos los post que tenga el mismo id que el propietario del muro
     */
    suspend fun getDataFromFireStore(): QuerySnapshot? {
        try {
            return db.collection("${Constantes.collectionPost}")
                .whereEqualTo("userId", userMuro!!.userId)
                .get()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    private fun obtenerDatos(datos: QuerySnapshot?) {
        postIdList.clear()
        for (dc: DocumentChange in datos?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {

                val postId: String? = dc.document.get("postId").toString()
                val userId: String? = dc.document.get("userId").toString()
                val imgId: String? = dc.document.get("imgId").toString()
                val etiquetas: ArrayList<String>? =
                    dc.document.get("etiquetas") as ArrayList<String>

                if (imgId != null) {
                    postIdList.add(imgId)
                }

                if (postId != null && userId != null) {
                    //se añaden los post a la lista
                    val post = Post(postId, userId, imgId, etiquetas)
                    postList.add(post)
                }
                Log.d("CHE_DE", "IMG ID : ${imgId}")
            }
        }
    }

    private fun getImgStorage() {
        adaptador.deseleccionar()
        storageRef.listAll()
            .addOnSuccessListener { list ->
                runBlocking {
                    val job: Job = launch(context = Dispatchers.Default) {
                        for (i in list.items) {
                            i.getBytes(Constantes.ONE_MEGABYTE).addOnSuccessListener {
                                val img = Utils.getBitmap(it)!!
                                val name = i.name
                                if (img != null && postIdList.contains(name)) {
                                    images.add(Imagen(name, img))
                                }
                            }.await()
                        }
                    }
                    //adaptador.notifyDataSetChanged()
                    job.join()
                }
                mostrarLista()
            }
    }

    private fun mostrarLista() {
        val rvImagenes = viewAux.findViewById<RecyclerView>(R.id.rv_post_artist_muro)
        rvImagenes.setHasFixedSize(true)
        rvImagenes.layoutManager = LinearLayoutManager(viewAux.context)
        rvImagenes.adapter = adaptador
    }


    //+++++++++++++++++++++++++++++++++++++++++++++++++++

    private fun checkFav() {
        if (VariablesCompartidas.usuarioBasicoActual != null) {
            userBasicActual = VariablesCompartidas.usuarioBasicoActual
            if (userBasicActual!!.idFavoritos!!.contains(userMuro!!.userId)) {
                favorite = true
                fltBtnFavCamera.setImageResource(R.drawable.ic_favorite)
            } else {
                favorite = false
                fltBtnFavCamera.setImageResource(R.drawable.ic_unfavorite)
            }
        } else {

            userArtistActual = VariablesCompartidas.usuarioArtistaActual
            if (userArtistActual!!.idFavoritos!!.contains(userMuro!!.userId)) {
                favorite = true
                fltBtnFavCamera.setImageResource(R.drawable.ic_favorite)
            } else {
                favorite = false
                fltBtnFavCamera.setImageResource(R.drawable.ic_unfavorite)
            }
        }
    }

    private fun changeFavCamera() {
        if (editMode) {
            //subir foto en modo propietario del muro
            val nombre = "newImage"
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("stImage", nombre.toString())
            requireContext().startActivity(intent)

        } else {
            //cambiar fav en modo visitante del muro
            if (VariablesCompartidas.usuarioBasicoActual != null) {
                changeFavBasic()
            } else {
                changeFavArtist()
            }
        }
    }

    private fun changeContactEdit() {
        if (editMode) {
            //editar muro en modo propietario
            addNewPriceSize()
        } else {
            //contactar con el artista en modo visitante
            getChat()
        }
    }

    private fun addNewPriceSize() {
        val dialog = layoutInflater.inflate(R.layout.add_price_size, null)
        val price = dialog.findViewById<EditText>(R.id.edPrice)
        val size = dialog.findViewById<EditText>(R.id.edSize)
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_price_size))
            .setView(dialog)
            .setPositiveButton("OK") { view, _ ->
                if (price.text.toString().trim().isNotEmpty() && size.text.toString().trim()
                        .isNotEmpty()
                ) {
                    updateArtistAddPriceSize(price.text.toString(), size.text.toString())
                    val st = " [ ${price.text.toString().toString()}€ <> ${
                        size.text.toString().toString()
                    }x${size.text.toString().toString()}cm ] \n"
                    ed_txt_prizes_sizes.text.append(st)
                }
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.Cancel)) { view, _ ->
                view.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun updateArtistAddPriceSize(price: String, size: String) {
        var artistUser: ArtistUser = VariablesCompartidas.usuarioArtistaActual!!
        artistUser.prices!!.add(price)
        artistUser.sizes!!.add(size)
        db.collection("${Constantes.collectionArtistUser}")
            .document(VariablesCompartidas.usuarioArtistaActual!!.userId.toString()) //Será la clave del documento.
            .set(artistUser).addOnSuccessListener {

                VariablesCompartidas.usuarioArtistaActual = artistUser

            }.addOnFailureListener {
                Toast.makeText(requireContext(), R.string.ERROR, Toast.LENGTH_SHORT).show()
            }
    }


    private fun getChat() {
        db.collection("${Constantes.collectionChat}")
            .whereEqualTo("idUserArtist", userMuro!!.userId)
            .get()
            .addOnSuccessListener { chats ->
                //No existen chats
                if (chats.isEmpty) {
                    crearChat()
                } else {
                    for (chat in chats) {

                        var idChat: String? = null
                        if (chat.get("idChat") != null) {
                            idChat = chat.get("idChat").toString()
                        }
                        var ch = Chat(
                            chat.get("idChat").toString(),
                            chat.get("idUserArtist").toString(),
                            chat.get("userNameArtist").toString(),
                            chat.get("idUserOther").toString(),
                            chat.get("userNameOther").toString(),
                            chat.get("date").toString()
                        )
                        if (ch.idUserOther!!.toString() == VariablesCompartidas.idUsuarioActual) {
                            var myIntent = Intent(context, ChatActivity::class.java)
                            myIntent.putExtra("idChat", idChat)
                            myIntent.putExtra("userName", ch.userNameArtist)
                            myIntent.putExtra("date", ch.date)
                            startActivity(myIntent)
                        }

                    }
                }
            }
    }

    private fun crearChat() {

        var idChat: String? = UUID.randomUUID().toString()
        var idUserArtist: String? = userMuro!!.userId
        var idUserOther: String? = null
        var userNameOther: String? = null
        val date: String? = ""
        if (VariablesCompartidas.usuarioBasicoActual != null) {
            idUserOther = VariablesCompartidas.usuarioBasicoActual!!.userId.toString()
            userNameOther = VariablesCompartidas.usuarioBasicoActual!!.userName.toString()
        } else {
            idUserOther = VariablesCompartidas.usuarioArtistaActual!!.userId.toString()
            userNameOther = VariablesCompartidas.usuarioArtistaActual!!.userName.toString()
        }
        var chat = hashMapOf(
            "idChat" to idChat,
            "idUserArtist" to idUserArtist,
            "userNameArtist" to userMuro!!.userName,
            "idUserOther" to idUserOther,
            "userNameOther" to userNameOther,
            "date" to date,
        )

        db.collection("${Constantes.collectionChat}")
            .document(idChat!!)
            .set(chat)
            .addOnSuccessListener {
                val myIntent = Intent(context, ChatActivity::class.java)
                myIntent.putExtra("idChat", idChat)
                myIntent.putExtra("date", date)
                myIntent.putExtra("userName", userMuro!!.userName)
                startActivity(myIntent)

            }.addOnFailureListener {
                Toast.makeText(context, R.string.ERROR, Toast.LENGTH_SHORT).show()
            }
    }

    //++++++++++++++++++++++++++++++++++++++++
    private fun changeFavArtist() {
        val db = FirebaseFirestore.getInstance()

        if (favorite) {
            //SI YA LO SIGUE, LO DA UNFOLLOW
            var userMod: ArtistUser? = userArtistActual
            userMod!!.idFavoritos!!.remove(userMuro!!.userId.toString())
            db.collection("${Constantes.collectionArtistUser}")
                .document("${userMod!!.userId}")
                .set(userMod!!).addOnSuccessListener {
                    userArtistActual = userMod
                    VariablesCompartidas.usuarioArtistaActual = userArtistActual
                    fltBtnFavCamera.setImageResource(R.drawable.ic_unfavorite)
                }.addOnFailureListener {
                    Toast.makeText(context, R.string.ERROR, Toast.LENGTH_SHORT).show()
                }
        } else {
            //SI NO LO SIGUE, LO DA FOLLOW
            var userMod: ArtistUser? = userArtistActual
            userMod!!.idFavoritos!!.add(userMuro!!.userId.toString())
            db.collection("${Constantes.collectionArtistUser}")
                .document("${userMod!!.userId}")
                .set(userMod!!).addOnSuccessListener {
                    userArtistActual = userMod
                    VariablesCompartidas.usuarioArtistaActual = userArtistActual
                    fltBtnFavCamera.setImageResource(R.drawable.ic_favorite)
                }.addOnFailureListener {
                    Toast.makeText(context, R.string.ERROR, Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun changeFavBasic() {
        val db = FirebaseFirestore.getInstance()

        if (favorite) {
            //SI YA LO SIGUE, LO DA UNFOLLOW
            var userMod: BasicUser? = userBasicActual
            userMod!!.idFavoritos!!.remove(userMuro!!.userId.toString())
            db.collection("${Constantes.collectionUser}")
                .document("${userMod!!.userId}")
                .set(userMod!!).addOnSuccessListener {
                    userBasicActual = userMod
                    VariablesCompartidas.usuarioBasicoActual = userBasicActual
                    fltBtnFavCamera.setImageResource(R.drawable.ic_unfavorite)
                }.addOnFailureListener {
                    Toast.makeText(context, R.string.ERROR, Toast.LENGTH_SHORT).show()
                }
        } else {
            //SI NO LO SIGUE, LO DA FOLLOW
            var userMod: BasicUser? = userBasicActual
            userMod!!.idFavoritos!!.add(userMuro!!.userId.toString())
            db.collection("${Constantes.collectionUser}")
                .document("${userMod!!.userId}")
                .set(userMod!!).addOnSuccessListener {
                    userBasicActual = userMod
                    VariablesCompartidas.usuarioBasicoActual = userBasicActual
                    fltBtnFavCamera.setImageResource(R.drawable.ic_favorite)
                }.addOnFailureListener {
                    Toast.makeText(context, R.string.ERROR, Toast.LENGTH_SHORT).show()
                }

        }
    }


}