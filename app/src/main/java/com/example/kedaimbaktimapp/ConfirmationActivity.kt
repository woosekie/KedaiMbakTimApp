package com.example.kedaimbaktimapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityConfirmationBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Location
import com.example.kedaimbaktimapp.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class ConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var lokasi: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle("Confirmation")


        //get foodId and userId
        val foodId = intent.getStringExtra("foodId")
        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        val userId = firebaseUser?.uid

        //get data from checkout
        val quantity = intent.getIntExtra("quantity", 0)
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val totalPay = intent.getIntExtra("total_pay", 0)
        val orderType = intent.getStringExtra("orderType").toString()
        val note = intent.getStringExtra("note").toString()
        val orderDate = date + " " + time

        if (foodId != null && userId != null) {
            showDataFood(foodId)
            showDataFromCheckout(
                quantity, date.toString(),
                time.toString(), totalPay, orderType, note
            )
            showDataUser(userId)
            transactionLocation(userId)
            getAdminId()

            binding.payButton.setOnClickListener {
                createTransaction(
                    userId,
                    foodId,
                    quantity,
                    totalPay,
                    orderDate,
                    getCurrentTime(),
                    orderType,
                    note
                )

                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(Intent(intent))
                Toast.makeText(baseContext, "Pesanan berhasil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openMaps(lat: String, lng: String) {
        val geoUri =
            "http://maps.google.com/maps?q=loc:" + lat + "," + lng
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        startActivity(intent)
    }

    private fun showDataUser(userId: String) {
        FirebaseDatabase.getInstance().getReference("Registered Users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user =
                        snapshot.getValue(com.example.kedaimbaktimapp.model.User::class.java)
                    if (user != null) {
                        binding.userName.text = user.name
                        binding.userNumber.text = user.number
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun createTransaction(
        userId: String,
        foodId: String,
        quantity: Int,
        totalPay: Int,
        dateOrder: String,
        dateSend: String,
        orderType: String,
        note: String
    ) {
        val key = FirebaseDatabase.getInstance().getReference("Transaction").push().key
        val transactionId = key.toString()

        val transaction = Transaction(
            transactionId, userId, foodId, quantity, totalPay,
            dateOrder, dateSend, orderType, "Belum diproses", note
        )

        //create transaction
        FirebaseDatabase.getInstance().getReference("Transaction").child(transactionId)
            .setValue(transaction).addOnCompleteListener { task ->

            }

        //create transaction user
        FirebaseDatabase.getInstance().getReference("Registered Users").child(userId)
            .child("transaction").child(transactionId).setValue(transaction)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    createLocationforTransaction(transactionId)
                }
            }

    }

    private fun createLocationforTransaction(key: String) {
        FirebaseDatabase.getInstance().getReference("Location").child(key)
            .setValue(lokasi)
    }

    private fun showDataFromCheckout(
        quantity: Int,
        date: String,
        time: String,
        totalPay: Int,
        orderType: String,
        note: String
    ) {
        binding.quantity.text = quantity.toString() + " Item"
        binding.orderDate.text = getCurrentTime()
        binding.sendingDate.text = date + " " + time
        binding.totalBayarInfo.text = getCurrencyFormat(totalPay)
        binding.totalBayar.text = getCurrencyFormat(totalPay)
        binding.orderType.text = orderType
        binding.note.text = note
    }

    private fun showDataFood(foodId: String) {
        FirebaseDatabase.getInstance().getReference("Food").child(foodId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val food = snapshot.getValue(Food::class.java)
                    if (food != null) {
                        Glide.with(applicationContext)
                            .load(food.photo)
                            .centerCrop()
                            .into(binding.foodImg)
                        binding.foodName.text = food.name
                        binding.foodPrice.text = getCurrencyFormat(food.price)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun transactionLocation(uid: String) {
        FirebaseDatabase.getInstance().getReference("Location").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val location =
                        snapshot.getValue(Location::class.java)
                    if (location != null) {
                        binding.customerLoc.text =
                            location.province + ", " + location.regency + ", " + location.subdistrict + ", " + location.area + ", " + location.address + " (" + location.postalcode + ")"
                        binding.maps.setOnClickListener {
                            openMaps(location.lat, location.lng)
                        }
                        lokasi = location
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun getAdminId() {
        FirebaseDatabase.getInstance().getReference("Registered Users").orderByChild("admin")
            .equalTo(true).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        val adminId = itemSnapshot.key.toString()
                        cateringLocaction(adminId)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun cateringLocaction(adminId: String) {
        FirebaseDatabase.getInstance().getReference("Location").child(adminId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val location =
                    snapshot.getValue(Location::class.java)
                if (location != null) {
                    binding.cateringLoc.text =
                        location.province + ", " + location.regency + ", " + location.subdistrict + ", " + location.area + ", " + location.address + " (" + location.postalcode + ")"
                    binding.maps2.setOnClickListener {
                        openMaps(location.lat, location.lng)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getCurrencyFormat(price: Int): String? {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val priceIdr = format.format(price)
        return priceIdr
    }

    private fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm")
        val curentDate = Date()
        val current = formatter.format(curentDate)
        return current
    }
}