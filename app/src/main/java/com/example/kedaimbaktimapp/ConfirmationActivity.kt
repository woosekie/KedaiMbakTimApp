package com.example.kedaimbaktimapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityConfirmationBinding
import com.example.kedaimbaktimapp.model.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle("Confirmation")


        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        loadData(firebaseUser)

        binding.payButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(Intent(intent))
            Toast.makeText(baseContext, "Pesanan berhasil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData(firebaseUser: FirebaseUser?) {
        val totalOrder = intent.getIntExtra("total_order", 0)
        val totalBayar = intent.getStringExtra("total_bayar")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val formatter = SimpleDateFormat("EEEE, dd-MM-yyyy hh:mm")
        val curentDate = Date()
        val current = formatter.format(curentDate)
        val food = intent.getParcelableExtra<Food>("detail_food") as Food
        Glide.with(applicationContext)
            .load(food.photo)
            .centerCrop()
            .into(binding.foodImg)
        binding.foodName.text= food.name
        binding.foodPrice.text = food.price.toString()
        binding.totalOrder.text = totalOrder.toString() + " Item"
        binding.totalBayarInfo.text = totalBayar.toString()
        binding.totalBayar.text = totalBayar.toString()
        binding.sendingDate.text = date + " "+ time
        binding.orderDate.text = current

        val userID = firebaseUser?.uid
        val referenceProfile: DatabaseReference
        referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
        if (userID != null) {
            referenceProfile.child(userID).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(com.example.kedaimbaktimapp.model.User::class.java)
                    if(user != null){
                        binding.userNumber.text = user.number
                        binding.userName.text = user.name
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}