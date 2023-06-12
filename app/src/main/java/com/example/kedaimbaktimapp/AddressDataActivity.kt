package com.example.kedaimbaktimapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kedaimbaktimapp.databinding.ActivityAddressDataBinding
import com.example.kedaimbaktimapp.model.Location
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class AddressDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressDataBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val location = intent.getParcelableExtra<Location>("location") as Location

        binding.provinsi.setText(location.province)
        binding.kabupaten.setText(location.regency)
        binding.kecamatan.setText(location.subdistrict)
        binding.kodepost.setText(location.postalcode)
        binding.alamat.setText(location.area)
        binding.detail.setText(location.address)

        binding.save.setOnClickListener {
            var province = binding.provinsi.text
            var regency = binding.kabupaten.text
            var subdisctrict = binding.kecamatan.text
            var postal = binding.kodepost.text
            var area = binding.alamat.text
            var address = binding.detail.text
            val lat = location.lat
            val lng = location.lng

            if (province.isNotEmpty() && regency.isNotEmpty() && subdisctrict.isNotEmpty() && postal.isNotEmpty() && area.isNotEmpty() && address.isNotEmpty() && lat.isNotEmpty() && lng.isNotEmpty()) {
                auth = Firebase.auth
                val firebaseUser = auth.currentUser
                val userID = firebaseUser?.uid

                val location = Location(
                    province.toString(),
                    regency.toString(), subdisctrict.toString(),
                    postal.toString(), area.toString(), address.toString(), lat, lng
                )
                FirebaseDatabase.getInstance().getReference("Location").child(userID.toString())
                    .setValue(location)
                finish()
                Toast.makeText(this, R.string.success_add_address, Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, R.string.please_enter_information, Toast.LENGTH_SHORT)
                    .show()

            }
        }
    }
}
