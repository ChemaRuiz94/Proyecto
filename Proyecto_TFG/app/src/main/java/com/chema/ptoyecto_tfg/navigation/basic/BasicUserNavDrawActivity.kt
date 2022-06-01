package com.chema.ptoyecto_tfg.navigation.basic

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.databinding.ActivityBasicUserNavDrawBinding
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas

//import com.chema.ptoyecto_tfg.navigation.basic.databinding.ActivityBasicUserNavDrawBinding

class BasicUserNavDrawActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityBasicUserNavDrawBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBasicUserNavDrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarBasicUserNavDraw.toolbar)

        binding.appBarBasicUserNavDraw.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_basic_user_nav_draw)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        val navUserEmail = headerView.findViewById<View>(R.id.txt_userEmail_header) as TextView
        val navUserName = headerView.findViewById<View>(R.id.txt_userName_header) as TextView
        //val imgBasicUserHeader = headerView.findViewById<View>(R.id.image_basic_user_header) as ImageView

        val u = VariablesCompartidas.usuarioBasicoActual as BasicUser
        navUserName.text = u.userName.toString()
        navUserEmail.setText(VariablesCompartidas.emailUsuarioActual.toString())
        setHeaderImgUser(u)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.basic_user_nav_draw, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_basic_user_nav_draw)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setHeaderImgUser(u : BasicUser?){
        if(u?.img != null){
            var imgST : String? = u?.img.toString()
            var photo: Bitmap? = Utils.StringToBitMap(imgST)
            val navigationView: NavigationView =
                (this as AppCompatActivity).findViewById(R.id.nav_view)
            val header: View = navigationView.getHeaderView(0)
            val imgHe = header.findViewById<ImageView>(R.id.image_basic_user_header)
            imgHe.setImageBitmap(photo)
        }

    }
}