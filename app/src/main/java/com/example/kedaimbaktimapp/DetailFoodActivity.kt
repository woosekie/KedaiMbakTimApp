package com.example.kedaimbaktimapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodBinding

class DetailFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val food = intent.getParcelableExtra<Food>("extra_detail") as Food
        Glide.with(applicationContext)
            .load(food.photo)
            .into(binding.imgItemPhoto)
        binding.tvItemName.text = food.name
        binding.tvItemPrice.text = getString(R.string.rp) + food.price

        supportActionBar?.setTitle(food.name)

        showData()
    }

    private fun showData(){

    }
}