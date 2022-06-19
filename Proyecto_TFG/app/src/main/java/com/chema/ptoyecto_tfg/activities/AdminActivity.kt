package com.chema.ptoyecto_tfg.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.databinding.ActivityAdminBinding
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_admin)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_all_basic_user, R.id.navigation_all_artist_users, R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed(){

        AlertDialog.Builder(this)
            .setTitle("Cerrar sersion")
            .setMessage("Desea cerrar sesion")
            .setPositiveButton("OK") { view, _ ->
                super.onBackPressed()
                VariablesCompartidas.usuarioArtistaActual = null
                VariablesCompartidas.usuarioArtistaVisitaMuro = null
                VariablesCompartidas.userAdminMode = false
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                view.dismiss()
            }
            .setNegativeButton("NO") { view, _ ->
                view.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }
}