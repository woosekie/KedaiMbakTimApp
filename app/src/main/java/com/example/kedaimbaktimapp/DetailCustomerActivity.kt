package com.example.kedaimbaktimapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityDetailCustomerBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Location
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class DetailCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCustomerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showData()
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.buttonWa.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=" + binding.customerNumber
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }

    private fun showData() {
        val user = intent.getParcelableExtra<Food>("detail_customer") as User
        binding.customerName.text = user.name
        binding.customerEmail.text = user.email
        binding.customerNumber.text = user.number
        if (user.photo.isNotEmpty()) {
            this.let {
                Glide.with(it.getApplicationContext()).load(user.photo)
                    .into(binding.profilePic)
            }
        }

        getUserId(user.email)
    }

    private fun getUserId(email: String) {
        FirebaseDatabase.getInstance().getReference("Registered Users").orderByChild("email")
            .equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children){
                        val key = data.key.toString()
                        showUserLocation(key)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showUserLocation(userId: String) {
        FirebaseDatabase.getInstance().getReference("Location").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val location = snapshot.getValue(Location::class.java)
                    if (location != null) {
                        binding.customerLoc.text =
                            location.province + ", " + location.regency + ", " + location.subdistrict + ", " + location.area + ", " + location.address + " (" + location.postalcode + ")"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }
}