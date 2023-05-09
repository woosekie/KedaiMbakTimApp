package com.example.kedaimbaktimapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityConfirmationBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class ConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmationBinding
    private lateinit var ReferenceProfile: DatabaseReference
    private lateinit var Referenceuser: DatabaseReference
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
            createTransaction()
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(Intent(intent))
            Toast.makeText(baseContext, "Pesanan berhasil", Toast.LENGTH_SHORT).show()
        }
        binding.map.setOnClickListener {
            val geoUri =
                "http://maps.google.com/maps?q=loc:" + "-7.736912237719496" + "," + "112.72631179897735" + " (" + "Kedai Mbak Tim" + ")"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(geoUri)
            startActivity(i)
        }
    }

    private fun createTransaction() {
        val totalOrder = intent.getIntExtra("total_order", 0)
        val totalBayar = intent.getIntExtra("total_bayar", 0)
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")

        val formatter = SimpleDateFormat("EEEE, dd-MM-yyyy hh:mm")
        val curentDate = Date()
        val current = formatter.format(curentDate)
        val food = intent.getParcelableExtra<Food>("detail_food") as Food

        var userId = auth.currentUser?.uid

        val dateOrder = date + " " + time

        val database = FirebaseDatabase.getInstance()
        val key = database.getReference("Transaction").push().key

        val transaction = Transaction(
            key.toString(), userId!!, food.foodId, totalOrder, totalBayar,
            current, dateOrder, "karangsono", "Belum diproses"
        )

        ReferenceProfile = Firebase.database.getReference("Transaction")
        ReferenceProfile.child(key.toString()).setValue(transaction).addOnCompleteListener { task ->

        }
        Referenceuser = Firebase.database.getReference("Registered Users")
        if (userId != null) {
            Referenceuser.child(userId).child("transaction").child(key!!).setValue(transaction)
                .addOnCompleteListener { task ->
                }
        }


    }

    private fun loadData(firebaseUser: FirebaseUser?) {
        val totalOrder = intent.getIntExtra("total_order", 0)
        val totalBayar = intent.getIntExtra("total_bayar", 0)
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val formatter = SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm")
        val curentDate = Date()
        val current = formatter.format(curentDate)
        val food = intent.getParcelableExtra<Food>("detail_food") as Food
        Glide.with(applicationContext)
            .load(food.photo)
            .centerCrop()
            .into(binding.foodImg)

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val priceIdr = format.format(totalBayar)
        val priceFood = format.format(food.price)

        val catatan = intent.getStringExtra("catatan")
        binding.addNote.text = catatan
        binding.foodName.text = food.name
        binding.foodPrice.text = priceFood
        binding.totalOrder.text = totalOrder.toString() + " Item"

        binding.totalBayarInfo.text = priceIdr
        binding.totalBayar.text = priceIdr
        binding.sendingDate.text = date + " " + time
        binding.orderDate.text = current
        val userID = firebaseUser?.uid
        val referenceProfile: DatabaseReference
        referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
        if (userID != null) {
            referenceProfile.child(userID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(com.example.kedaimbaktimapp.model.User::class.java)
                    if (user != null) {
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