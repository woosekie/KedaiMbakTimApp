package com.example.kedaimbaktimapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kedaimbaktimapp.databinding.ActivityAboutUsBinding
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AboutUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUsBinding
    private lateinit var number: String
    private lateinit var account: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAdminNumber()
        getTikTok()


        binding.buttonWa.setOnClickListener {
                val url = "https://api.whatsapp.com/send?phone=$number"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)

        }
        binding.buttonTiktok.setOnClickListener {
                val url =
                    "https://www.tiktok.com/$account?lang=en"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
        }
    }

    private fun getTikTok() {
        FirebaseDatabase.getInstance().getReference("Setting").child("social").child("tiktok")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    account = snapshot.getValue().toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun getAdminNumber() {
        FirebaseDatabase.getInstance().getReference("Registered Users").orderByChild("admin")
            .equalTo(true).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        val admin = itemSnapshot.getValue(User::class.java)
                        if (admin != null) {
                            number = admin.number
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}