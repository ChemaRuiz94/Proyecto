package com.chema.ptoyecto_tfg.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.chema.ptoyecto_tfg.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SignUpArtistActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var imgArtistSignUp : ImageView
    private lateinit var edTxtArtistUserName : EditText
    private lateinit var edTxtArtistEmail : EditText
    private lateinit var edTxtArtistPhone : EditText
    private lateinit var edTxtArtistCif : EditText
    private lateinit var edTxtArtistPwd : EditText
    private lateinit var edTxtArtistRepeatPwd : EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnSelectStudio: Button

    private lateinit var mMap: GoogleMap
    private val LOCATION_REQUEST_CODE: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_artist)

        imgArtistSignUp = findViewById(R.id.img_sign_up_artist)
        edTxtArtistUserName = findViewById(R.id.ed_txt_user_name_sign_up_artist)
        edTxtArtistEmail = findViewById(R.id.ed_txt_email_sign_up_artist)
        edTxtArtistPhone = findViewById(R.id.ed_txt_phone_sign_up_artist)
        edTxtArtistCif = findViewById(R.id.ed_txt_cif_sign_up_artist)
        edTxtArtistPwd = findViewById(R.id.ed_txt_pwd_sign_up_artist)
        edTxtArtistRepeatPwd = findViewById(R.id.ed_txt_repeat_pwd_sign_up_artist)
        btnSelectStudio = findViewById(R.id.btn_select_location)
        btnSignUp = findViewById(R.id.btnSignUpArtist)

        btnSelectStudio.setOnClickListener{
            val mapIntent = Intent(this, SelectStudioMapsActivity::class.java).apply {
                //putExtra("email",email)
            }
            startActivity(mapIntent)
            //startActivityForResult(mapIntent,1)
        }
        cargarMapa()
    }

    private fun cargarMapa() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.frm_MapLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }




    override fun onMapReady(p0: GoogleMap) {
        mMap = p0!!
        mMap.mapType=GoogleMap.MAP_TYPE_NORMAL
        val Madrid = LatLng(40.416, -3.703)
        mMap?.addMarker(MarkerOptions().position(Madrid).title("SELECT STUDIO LOCATION"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(Madrid,15f))
        enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    fun enableMyLocation() {
        if (!::mMap.isInitialized) return
        if (isPermissionsGranted()) {
            mMap.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }
    }
}