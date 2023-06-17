package com.example.kedaimbaktimapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityCheckoutBinding
import com.example.kedaimbaktimapp.model.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.lang.Integer.max
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var quantity = 0
    private var totalPay = 0
    private lateinit var orderType: String
    private lateinit var sList: ArrayList<String>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle("Checkout")

        //get foodId and userId
        val foodId = intent.getStringExtra("foodId")
        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        val userId = firebaseUser?.uid

        if (foodId != null && userId != null) {
            showDataFood(foodId)
            showDataUser(userId)
            showAddress(userId)

            //input button for users
            dropDownMenu() //get order type
            pickDate() //get date
            pickTime() //get hours

            binding.settingLoc.setOnClickListener {
                val intent = Intent(this, MapActivity::class.java)
                this.startActivity(Intent(intent))
            }

            binding.confirmButton.setOnClickListener {
                if (binding.address.text != getString(R.string.address_not_added) && quantity > 0 && binding.dateText.text!="00-00-0000" && binding.timeText.text!="00:00") {
                    val intent = Intent(this, ConfirmationActivity::class.java)
                    intent.putExtra("orderType", orderType)
                    intent.putExtra("foodId", foodId)
                    intent.putExtra("quantity", quantity)
                    intent.putExtra("total_pay", totalPay)
                    intent.putExtra("time", binding.timeText.text)
                    intent.putExtra("date", binding.dateText.text)
                    intent.putExtra("note", binding.addNote.text.toString())
                    this.startActivity(Intent(intent))
                } else {
                    Toast.makeText(this, R.string.please_enter_information, Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

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
                        setQuantity(food.price) //calculate food price with quantity
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showDataUser(userId: String) {
        FirebaseDatabase.getInstance().getReference("Registered Users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(com.example.kedaimbaktimapp.model.User::class.java)
                    if (user != null) {
                        binding.userNumber.text = user.number
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showAddress(userId: String) {
        FirebaseDatabase.getInstance().getReference("Location").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val location =
                        snapshot.getValue(com.example.kedaimbaktimapp.model.Location::class.java)
                    if (location != null) {
                        binding.address.text =
                            location.province + ", " + location.regency + ", " + location.subdistrict + ", " + location.area + ", " + location.address + " (" + location.postalcode + ")"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun setQuantity(price: Int) {
        binding.buttonDecrease.setOnClickListener {
            quantity = max(0, quantity - 1)
            binding.quantity.text = quantity.toString()
            totalPay = ((quantity * price))
            binding.totalPay.text = getCurrencyFormat(totalPay)
        }
        binding.buttonIncrease.setOnClickListener {
            quantity++
            binding.quantity.text = quantity.toString()
            totalPay = ((quantity * price))
            binding.totalPay.text = getCurrencyFormat(totalPay)
        }
    }

    private fun dropDownMenu() {
        sList = arrayListOf("Diantarkan", "Diambil")
        val dataAdapter =
            ArrayAdapter(this, R.layout.selected_item_spinner, sList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = dataAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p0 != null) {
                    orderType = p0.getItemAtPosition(p2).toString()
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun pickDate() {
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
    }

    private fun pickTime() {
        binding.timeButton.setOnClickListener {
            val curentTime = Calendar.getInstance()
            val startHour = curentTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = curentTime.get(Calendar.MINUTE)

            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
                val y =convertDate(hourOfDay)
                val x =convertDate(minutes)
                binding.timeText.setText( "$y:$x")
            }, startHour, startMinute, false).show()
        }
    }

    fun convertDate(input: Int): String? {
        return if (input >= 10) {
            input.toString()
        } else {
            "0$input"
        }
    }

    private fun getCurrencyFormat(price: Int): String? {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val priceIdr = format.format(price)
        return priceIdr
    }
}
