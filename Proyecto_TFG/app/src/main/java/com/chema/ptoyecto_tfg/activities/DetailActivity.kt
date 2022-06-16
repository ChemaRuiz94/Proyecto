package com.chema.ptoyecto_tfg.activities

import android.Manifest
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chema.ptoyecto_tfg.R
import android.graphics.Bitmap

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chema.ptoyecto_tfg.TabBasicUserActivity
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.models.Post
import com.chema.ptoyecto_tfg.models.Rol
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


class DetailActivity : AppCompatActivity() {

    private lateinit var ed_txt_styles: EditText
    private lateinit var ed_txt_add_style: EditText
    private lateinit var flt_btn_add_style: FloatingActionButton
    private lateinit var btn_add_post: Button
    private lateinit var img_detail: ImageView

    private val db = FirebaseFirestore.getInstance()
    private var storage = Firebase.storage
    private var storageRef = storage.reference

    private var postDetail: Post? = null
    private var listaEtiquetas: ArrayList<String> = ArrayList()
    private var photo: Bitmap? = null
    private var idImg: String? = null
    private var newImageMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val extras = intent.extras
        if (extras == null) {
            idImg = null
        } else {
            idImg = extras.getString("stImage")
        }

        img_detail = findViewById(R.id.img_detail)
        ed_txt_styles = findViewById(R.id.ed_txt_styles)
        ed_txt_add_style = findViewById(R.id.ed_txt_add_style)
        btn_add_post = findViewById(R.id.btn_add_post)
        flt_btn_add_style = findViewById(R.id.flt_btn_add_style)

        if (idImg.equals("newImage")) {
            newImageMode = true
            ed_txt_add_style.visibility = View.VISIBLE
            btn_add_post.visibility = View.VISIBLE
            flt_btn_add_style.visibility = View.VISIBLE
        }

        if (newImageMode) {
            img_detail.setOnClickListener {
                chooseFoto()
            }
            flt_btn_add_style.setOnClickListener {
                addNewStyle()
            }
            btn_add_post.setOnClickListener {
                savePost()
            }
        }
        cargarDetalleActivity()
    }

    private fun savePost() {
        if (photo != null) {
            val postId = UUID.randomUUID().toString()
            val stId = UUID.randomUUID().toString()
            val imgStId = "$stId.jpg"
            val imageRef = storageRef.child("${stId}.jpg")

            //subimos la img a storage
            val uploadTask = imageRef.putBytes(Utils.getBytes(photo!!)!!)
            uploadTask.addOnSuccessListener {
                //saveComentarioFirebase( Utils.getBytes(photo!!)  )
                val byteArray: ByteArray? = Utils.getBytes(photo!!)

                val post = Post(
                    postId,
                    VariablesCompartidas.usuarioArtistaActual!!.userId,
                    imgStId,
                    listaEtiquetas
                )
                //guardamos el post en firebase
                db.collection("${Constantes.collectionPost}")
                    .document("${postId.toString()}") //Será la clave del documento.
                    .set(post).addOnSuccessListener {
                        listaEtiquetas.clear()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, getString(R.string.ERROR), Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(this, getString(R.string.ERROR), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addNewStyle() {
        if (ed_txt_add_style.text.toString().trim().isNotEmpty()) {
            val style = ed_txt_add_style.text.toString().trim()
            listaEtiquetas.add(style)
            var stAux = ed_txt_styles.text.toString()
            var styleSt = "${stAux.toString()}\n#${style.toString()}"
            ed_txt_styles.setText(styleSt)
            ed_txt_add_style.setText("")
        }
    }

    private fun cargarDetalleActivity() {
        if (newImageMode) {
            chooseFoto()
        } else {
            getImageStorage()
            getPostFirebase()
            putPostStyles()
        }
    }


    private fun getImageStorage() {
        val storageRef = Firebase.storage.reference
        storageRef.child("${idImg}").getBytes(Long.MAX_VALUE).addOnSuccessListener {
            var img_bd = Utils.getBitmap(it)
            img_detail.setImageBitmap(img_bd)
        }.addOnFailureListener {
            Toast.makeText(this, "Se produjo un ERROR al bajar la imagen", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getPostFirebase() {
        db.collection("${Constantes.collectionPost}")
            .whereEqualTo("imgId", "${idImg.toString()}")
            .get()
            .addOnSuccessListener { posts ->
                //Existe
                for (post in posts) {
                    var postId = "";
                    if (post.get("postId").toString() != "") {
                        postId = post.get("postId").toString()
                    }
                    /*
                    var imgId = "";
                    if (post.get("imgId").toString() != "") {
                        imgId = post.get("imgId").toString()
                    }

                     */
                    var userId = "";
                    if (post.get("userId").toString() != "") {
                        userId = post.get("userId").toString()
                    }
                    var etiquetas: ArrayList<String>? = ArrayList()
                    if (post.get("etiquetas") as ArrayList<String>? != null) {
                        etiquetas = post.get("etiquetas") as ArrayList<String>?
                    }
                    Toast.makeText(this, "POST_ID -> ${postId.toString()}", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "IMG_ID -> ${idImg.toString()}", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "USER_ID -> ${userId.toString()}", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "ETIQUETAS -> ${etiquetas!!.size}", Toast.LENGTH_SHORT).show()
                    postDetail = Post(postId, userId, idImg, etiquetas)
                }
            }
            .addOnFailureListener { exception ->
                //Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun putPostStyles(){
        if(postDetail != null){
            var stFin = ""
            for(sty in postDetail!!.etiquetas!!){
                var stAux = ""
                stAux = "${stFin.toString()}#${sty.toString()}"
                stFin = stAux
            }
            ed_txt_styles.setText(stFin)
        }
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private fun chooseFoto() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.chosePhoto))
            .setMessage(getString(R.string.strMensajeElegirFoto))
            .setPositiveButton(getString(R.string.strCamara)) { view, _ ->
                hacerFoto()
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strGaleria)) { view, _ ->
                elegirDeGaleria()
                view.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    /*
    Permite elegir una imagen de la galeria
     */
    private fun elegirDeGaleria() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Seleccione una imagen"),
            Constantes.CODE_GALLERY
        )
    }

    /*
    Permite realizar una foto con la camara
     */
    private fun hacerFoto() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                Constantes.CODE_CAMERA
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, Constantes.CODE_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constantes.CODE_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    photo = data?.extras?.get("data") as Bitmap
                    img_detail.setImageBitmap(photo)
                }
            }
            Constantes.CODE_GALLERY -> {
                if (resultCode === Activity.RESULT_OK) {
                    val selectedImage = data?.data
                    val selectedPath: String? = selectedImage?.path
                    if (selectedPath != null) {
                        var imageStream: InputStream? = null
                        try {
                            imageStream = selectedImage.let {
                                this.contentResolver.openInputStream(
                                    it
                                )
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        val bmp = BitmapFactory.decodeStream(imageStream)
                        photo = Bitmap.createScaledBitmap(bmp, 200, 300, true)
                        img_detail.setImageBitmap(photo)
                    }
                }
            }
        }
    }
}