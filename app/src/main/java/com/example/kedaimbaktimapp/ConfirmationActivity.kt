package com.example.kedaimbaktimapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kedaimbaktimapp.databinding.ActivityCheckoutBinding
import com.example.kedaimbaktimapp.databinding.ActivityConfirmationBinding
import com.example.kedaimbaktimapp.fragment.HomeFragment

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle("Confirmation")

        binding.payButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(Intent(intent))
            Toast.makeText(baseContext, "Pesanan berhasil", Toast.LENGTH_SHORT).show()
        }
    }
}