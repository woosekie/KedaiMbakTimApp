package com.example.kedaimbaktimapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodBinding
import com.example.kedaimbaktimapp.model.Food
import java.text.NumberFormat
import java.util.*

class DetailFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showData()
    }

    private fun showData() {
        val food = intent.getParcelableExtra<Food>("detail_food") as Food
        Glide.with(applicationContext)
            .load(food.photo)
            .fitCenter()
            .into(binding.imgItemPhoto)
        binding.tvItemName.text = food.name
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        binding.tvItemPrice.text  = format.format(food.price)
        supportActionBar?.setTitle(food.name)

        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("detail_food", food)
            this.startActivity(Intent(intent))
        }
    }
}