package com.example.kedaimbaktimapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kedaimbaktimapp.databinding.ActivityDetailCustomerBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.User

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
            val url = "https://api.whatsapp.com/send?phone="+binding.customerNumber
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }
    private fun showData(){
        val user = intent.getParcelableExtra<Food>("detail_customer") as User
        binding.customerName.text = user.name
        binding.customerEmail.text = user.email
        binding.customerNumber.text = user.number
    }
}