package com.chema.ptoyecto_tfg.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chema.ptoyecto_tfg.R
import android.graphics.Bitmap

import android.content.Intent
import android.os.Parcelable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton


class DetailActivity : AppCompatActivity() {

    private lateinit var img_detail : ImageView
    private lateinit var ed_txt_styles : EditText
    private lateinit var ed_txt_add_style : EditText
    private lateinit var flt_btn_add_style : FloatingActionButton
    private lateinit var btn_add_post : Button

    private var idImg : String? = null
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

        if(idImg.equals("newImage")){
            ed_txt_add_style.visibility = View.VISIBLE
            btn_add_post.visibility = View.VISIBLE
            flt_btn_add_style.visibility = View.VISIBLE
        }
    }
}