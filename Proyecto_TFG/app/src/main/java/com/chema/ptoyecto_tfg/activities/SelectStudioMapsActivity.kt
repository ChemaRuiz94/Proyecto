package com.chema.ptoyecto_tfg.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chema.ptoyecto_tfg.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
//import com.chema.ptoyecto_tfg.activities.databinding.ActivitySelectStudioMapsBinding
import com.chema.ptoyecto_tfg.databinding.ActivitySelectStudioMapsBinding
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.gms.maps.model.Marker

class SelectStudioMapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivitySelectStudioMapsBinding
    private val LOCATION_REQUEST_CODE: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectStudioMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        val Madrid = LatLng(40.416, -3.703)
        mMap?.addMarker(MarkerOptions().position(Madrid).title("SELECT STUDIO LOCATION"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(Madrid,10f))

        mMap.setOnMapClickListener(this)
        mMap.setOnMarkerClickListener(this)
        enableMyLocation()
    }

    override fun onMapClick(p0: LatLng)  {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0!!).title("location"))

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
            Toast.makeText(this, R.string.accept_permissions, Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.modo_normal -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.modo_hybrid -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.modo_satellite -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMarkerClick(p0: Marker): Boolean {

        AlertDialog.Builder(this).setTitle(R.string.select_this_location)
            .setPositiveButton(R.string.CONFIRM) { view, _ ->
                //guardamos la posi del marcador para usarla en otra activity
                VariablesCompartidas.marcadorActual = p0.position
                VariablesCompartidas.latitudStudioSeleccionado = p0.position.latitude.toString()
                VariablesCompartidas.longitudStudioSeleccionado = p0.position.longitude.toString()
                finish()
                view.dismiss()
            }.setNegativeButton(R.string.NO) { view, _ ->
                //elimina marcador
                p0.remove()
                view.dismiss()
            }.create().show()
        return false
    }
}