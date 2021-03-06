package com.chema.ptoyecto_tfg

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.chema.ptoyecto_tfg.activities.LoginActivity
import com.chema.ptoyecto_tfg.ui.main.SectionsPagerAdapter
import com.chema.ptoyecto_tfg.databinding.ActivityTabBasicUserBinding
import com.chema.ptoyecto_tfg.utils.Utils
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import android.R.menu

import android.view.MenuInflater




class TabBasicUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTabBasicUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTabBasicUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

    }


    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sersion")
            .setMessage("Desea cerrar sesion")
            .setPositiveButton("OK") { view, _ ->
                super.onBackPressed()
                VariablesCompartidas.usuarioBasicoActual = null
                VariablesCompartidas.usuarioArtistaVisitaMuro = null
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