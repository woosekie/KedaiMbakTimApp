package com.example.kedaimbaktimapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityConfirmationBinding
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodAdminBinding
import com.example.kedaimbaktimapp.databinding.ActivityDetailHistoryBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Transaction
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var foodDatabase: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction = intent.getParcelableExtra<Transaction>("detail_transaction") as Transaction

        binding.totalOrder.text = transaction.quantity.toString() + " Item"

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val priceIdr = format.format(transaction.price)
        binding.foodPrice.text = priceIdr

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        val userID = firebaseUser?.uid
        database = FirebaseDatabase.getInstance().getReference("Registered Users")

        database.child(userID.toString()).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if(user != null){
                    binding.userName.text = user.name
                    binding.userNumber.text = user.number
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        binding.orderDate.text = transaction.dateOrder
        binding.sendingDate.text = transaction.dateSend
        binding.totalBayarInfo.text = priceIdr
        binding.transactionId.text = transaction.transactionId

        foodDatabase = FirebaseDatabase.getInstance().getReference("Food")

        foodDatabase.child(transaction.foodId).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val food = snapshot.getValue(Food::class.java)
                if (food != null) {
                    Glide.with(applicationContext)
                        .load(food.photo)
                        .into(binding.foodImg)

                    binding.foodName.text = food.name

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })



    }
}