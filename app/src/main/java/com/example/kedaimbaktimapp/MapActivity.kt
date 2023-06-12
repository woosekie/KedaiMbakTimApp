package com.example.kedaimbaktimapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.kedaimbaktimapp.databinding.ActivityMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.util.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val FINE_PERMISSION_CODE = 1
    var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var binding: ActivityMapBinding
    private var location: com.example.kedaimbaktimapp.model.Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        binding.btnLocation.setOnClickListener {
            getLastLocation()
        }
        binding.btnSave.setOnClickListener {
            if(location != null){
                val intent = Intent(this, AddressDataActivity::class.java)
                intent.putExtra("location", location)
                this.startActivity(Intent(intent))
            } else {
                Toast.makeText(baseContext, "Lokasi saat ini belum didapatkan", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_PERMISSION_CODE
            )
            return
        }
        val task: Task<Location> = fusedLocationProviderClient!!.getLastLocation()
        task.addOnSuccessListener(object : OnSuccessListener<Location> {
            override fun onSuccess(location: Location?) {
                if (location != null) {
                    currentLocation = location
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this@MapActivity)
                    val lat = currentLocation!!.latitude
                    val lng = currentLocation!!.longitude
                    this@MapActivity.location = com.example.kedaimbaktimapp.model.Location(
                        getProvince(lat, lng),
                        getRegency(lat, lng),
                        getSubdistrict(lat, lng),
                        getPostalcode(lat, lng),
                        getArea(lat, lng),
                        getStreet(lat, lng),
                        lat.toString(), lng.toString()
                    )
                    Toast.makeText(baseContext, "Lokasi didapatkan", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(baseContext, "Lokasi belum diaktifkan", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })
    }

    private fun getProvince(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val address: MutableList<Address>? = geocoder.getFromLocation(lat, lng, 1)
        val province = address!!.get(0).adminArea //jawa timur
        return province
    }

    private fun getRegency(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val address: MutableList<Address>? = geocoder.getFromLocation(lat, lng, 1)
        val regency = address!!.get(0).subAdminArea //pasuruan
        return regency
    }

    private fun getSubdistrict(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val address: MutableList<Address>? = geocoder.getFromLocation(lat, lng, 1)
        val subdistrict = address!!.get(0).locality //kecatamatan sukorejo
        return subdistrict
    }

    private fun getPostalcode(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val address: MutableList<Address>? = geocoder.getFromLocation(lat, lng, 1)
        val postalcode = address!!.get(0).postalCode //67161
        return postalcode
    }

    private fun getArea(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val address: MutableList<Address>? = geocoder.getFromLocation(lat, lng, 1)
        val area = address!!.get(0).subLocality //karangsono
        return area
    }

    private fun getStreet(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val address: MutableList<Address>? = geocoder.getFromLocation(lat, lng, 1)
        val street = address!!.get(0).featureName //jagalan
        return street
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sydney = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Location")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                getLastLocation()
            } else {
                Toast.makeText(baseContext, "Harap aktifkan lokasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
