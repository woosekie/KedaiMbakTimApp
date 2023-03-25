package com.example.kedaimbaktimapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodBinding
import com.example.kedaimbaktimapp.model.Food

class DetailFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val food = intent.getParcelableExtra<Food>("detail_food") as Food
        Glide.with(applicationContext)
            .load(food.photo)
            .into(binding.imgItemPhoto)
        binding.tvItemName.text = food.name
        binding.tvItemPrice.text = "Rp. " + food.price

        supportActionBar?.setTitle(food.name)

        showData()

        binding.btnCheckout.setOnClickListener{
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("detail_food", food)
            this.startActivity(Intent(intent))
        }
    }

    private fun showData(){

    }
}