package com.example.kedaimbaktimapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityDetailHistoryBinding
import com.example.kedaimbaktimapp.model.*
import com.example.kedaimbaktimapp.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var lat : String
    private lateinit var lng : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get uid
        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        val userID = firebaseUser?.uid

        //get transaction id from adapter with parcelable
        val transaction = intent.getParcelableExtra<Transaction>("detail_transaction") as Transaction
        if (transaction != null) {
            getAdminId()
            showTransaction(transaction.transactionId, userID.toString())
        }

        binding.buttonRate.setOnClickListener {
            createRating(transaction.status, transaction.userId, transaction.foodId, transaction.transactionId)
        }

        binding.maps.setOnClickListener {
            if(lat.isNotEmpty() && lng.isNotEmpty()){
                openMaps(lat, lng)
            }
        }

    }

    private fun openMaps(lat: String, lng: String) {
        val geoUri = "http://maps.google.com/maps?q=loc:$lat,$lng"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        startActivity(intent)
    }


    private fun showTransaction(transactionId: String, userID: String) {
        FirebaseDatabase.getInstance().getReference("Registered Users").child(userID)
            .child("transaction").child(transactionId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transaction = snapshot.getValue(Transaction::class.java)
                    FirebaseDatabase.getInstance().getReference("Location").child(transactionId).addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var location =
                                snapshot.getValue(com.example.kedaimbaktimapp.model.Location::class.java)
                            if (location != null) {
                                binding.customerLoc.text = location.province + ", " + location.regency + ", " + location.subdistrict + ", " + location.area + ", " + location.address + " (" + location.postalcode + ")"
                                lat=location.lat
                                lng=location.lng
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                    //get user id to get user information that use to other fun
                    val userId = transaction?.userId
                    showUser(userId)
                    //get food id to get food information that use to other fun
                    val foodId = transaction?.foodId
                    showFood(foodId)
                    //get food rating
                    checkRating(transactionId)
                    //Set value from transactions
                    binding.totalOrder.text = transaction!!.quantity.toString() + " Item"
                    binding.orderDate.text = transaction!!.dateOrder
                    binding.sendingDate.text = transaction!!.dateSend
                    binding.totalBayarInfo.text = getCurrencyFormat(transaction!!.price)
                    binding.transactionId.text = transaction.transactionId
                    binding.orderType.text = transaction.TypeOrder
                    binding.note.text = transaction.orderNote
                    binding.status.text = transaction.status
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showUser(userId: String?) {
        FirebaseDatabase.getInstance().getReference("Registered Users").child(userId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    binding.userNumber.text = user!!.number
                    binding.userName.text = user!!.name
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showFood(foodId: String?) {
        FirebaseDatabase.getInstance().getReference("Food").child(foodId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val food = snapshot.getValue(Food::class.java)
                    if(food !=null){
                        binding.foodName.text = food.name
                        Glide.with(applicationContext)
                            .load(food.photo)
                            .into(binding.foodImg)
                        binding.foodPrice.text = getCurrencyFormat(food.price)
                    } else {
                        Toast.makeText(baseContext, "Data makanan tidak tersedia", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun checkRating(transactionId: String) {
        FirebaseDatabase.getInstance().getReference("Rating")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        if (data.key == transactionId) {
                            showRating(transactionId)
                            binding.cardview.visibility = View.GONE
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun showRating(transactionId: String) {
        FirebaseDatabase.getInstance().getReference("Rating").child(transactionId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rating = snapshot.getValue(Rating::class.java)
                    binding.cardview2.visibility = View.VISIBLE
                    binding.comment2.text = rating!!.comment
                    binding.rBar2.rating = rating.rateValue.toFloat()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun createRating(transactionStatus: String, userId: String, foodId: String, transactionId: String) {
        if (transactionStatus.equals("Selesai")) {
            val starRate = binding.rBar.rating.toInt()
            val comment = binding.comment.text
            val date = getCurrentTime()

            val rating = Rating(userId, foodId, starRate.toString(), comment.toString(), date)

            FirebaseDatabase.getInstance().getReference().child("Rating").child(transactionId)
                .setValue(rating).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, R.string.rating_success, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } else {
            Toast.makeText(baseContext, getString(R.string.button_rate_fail), Toast.LENGTH_SHORT)
                .show()
        }
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
        val formatter = SimpleDateFormat("dd MMMM yyyy")
        val curentDate = Date()
        val current = formatter.format(curentDate)
        return current
    }

}