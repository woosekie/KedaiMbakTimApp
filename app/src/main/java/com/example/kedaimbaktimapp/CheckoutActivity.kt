package com.example.kedaimbaktimapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityCheckoutBinding
import com.example.kedaimbaktimapp.model.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.lang.Integer.max
import java.text.SimpleDateFormat
import java.util.*


class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    var totalOrder = 0
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle("Checkout")

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        showUserProfile(firebaseUser)

        val food = intent.getParcelableExtra<Food>("detail_food") as Food
        Glide.with(applicationContext)
            .load(food.photo)
            .centerCrop()
            .into(binding.foodImg)
        binding.foodName.text= food.name
        binding.foodPrice.text = "Rp. "+ food.price

        //date picker
        binding.dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "EEEE, dd-MM-yyyy"
                    val sdf = SimpleDateFormat(myFormat)
                    binding.dateText.setText(sdf.format(calendar.time))
                }
            DatePickerDialog(
                this,
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //time picker
        binding.timeButton.setOnClickListener {
            val curentTime = Calendar.getInstance()
            val startHour = curentTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = curentTime.get(Calendar.MINUTE)

            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
                binding.timeText.setText("$hourOfDay:$minutes ")
            }, startHour, startMinute, false).show()
        }

        //increase decrease button
        binding.buttonDecrease.setOnClickListener {
            totalOrder = max(0,totalOrder-1)
            binding.totalOrder.text = totalOrder.toString()
        }
        binding.buttonIncrease.setOnClickListener {
            totalOrder++
            binding.totalOrder.text = totalOrder.toString()
        }

        binding.totalOrder.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                binding.totalBayar.text = "Rp. "+(totalOrder*food.price).toString()
            }
        })

        binding.confirmButton.setOnClickListener{
            val intent = Intent(this, ConfirmationActivity::class.java)
            intent.putExtra("detail_food", food)
            intent.putExtra("total_order", totalOrder)
            intent.putExtra("total_bayar", binding.totalBayar.text)
            intent.putExtra("time",  binding.timeText.text)
            intent.putExtra("date", binding.dateText.text)
            this.startActivity(Intent(intent))
        }

    }

    private fun showUserProfile(firebaseUser: FirebaseUser?) {
        val userID = firebaseUser?.uid
        val referenceProfile: DatabaseReference
        referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
        if (userID != null) {
            referenceProfile.child(userID).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(com.example.kedaimbaktimapp.model.User::class.java)
                    if(user != null){
                        val textViewNumber = user.number
                        binding.userNumber.text = textViewNumber
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}
