package com.example.kedaimbaktimapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.adapter.ListRateAdapter
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Rating
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*


class DetailFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFoodBinding
    private var list = ArrayList<Rating>()
    private var isFavourite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get foodId and userId
        val foodId = intent.getStringExtra("foodId")
        val firebaseUser = Firebase.auth.currentUser
        val userId = firebaseUser?.uid

        if (foodId != null && userId != null) {
            checkFavourite(foodId, userId)
            showDataFood(foodId)
            getRating(foodId)
            binding.favourite.setOnClickListener {
                if (isFavourite == true) {
                    removeFavouriteFood(foodId, userId)
                } else if (isFavourite == false) {
                    addFavouriteFood(foodId, userId)
                }
                changeColourFavourite(isFavourite)
            }

            binding.btnCheckout.setOnClickListener {
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtra("foodId", foodId)
                this.startActivity(Intent(intent))
            }
        }
    }

    private fun getRating(foodId: String) {
        binding.rvRate.setHasFixedSize(true)
        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(binding.rvRate)
        binding.rvRate.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        FirebaseDatabase.getInstance().getReference("Rating").orderByChild("foodId").equalTo(foodId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    var count = 0
                    var sum = 0
                    for (itemSnapshot in snapshot.children) {
                        val rating = itemSnapshot.getValue(Rating::class.java)
                        if (rating != null) {
                            binding.textRate.setText(R.string.the_latest_review)
                            binding.rvRate.visibility = View.VISIBLE
                            list.add(rating)
                            count++
                            sum += Integer.parseInt(rating.rateValue)
                        }
                    }
                    if (count != 0) {
                        val average: Float = (sum.toFloat() / count.toFloat())
                        Log.d("average:", "" + average)
                        val number3digits: Double = Math.round(average * 1000.0) / 1000.0
                        val number2digits: Double = Math.round(number3digits * 100.0) / 100.0
                        val solution: Double = Math.round(number2digits * 10.0) / 10.0
                        binding.rateSum.text = solution.toString()
                    }
                    binding.rvRate.adapter = ListRateAdapter(list)
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun addFavouriteFood(foodId: String, uid: String) {
        FirebaseDatabase.getInstance().getReference("Food").child(foodId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val food = snapshot.getValue(Food::class.java)
                    if (food != null) {
                        FirebaseDatabase.getInstance().getReference("Favourite Food").child(uid)
                            .child(foodId).setValue(food)
                    } else {
                        Toast.makeText(
                            baseContext,
                            getString(R.string.failed_load_data),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        isFavourite=true
    }

    private fun removeFavouriteFood(foodId: String, uid: String) {
        val ref = FirebaseDatabase.getInstance().reference
        val applesQuery =
            ref.child("Favourite Food").child(uid).orderByChild("foodId")
                .equalTo(foodId)
        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        isFavourite = false
    }

    private fun checkFavourite(foodId: String, uid: String) {
        FirebaseDatabase.getInstance().getReference("Favourite Food").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        if (data.key.equals(foodId)) {
                            isFavourite = true
                            changeColourFavourite(true)
                        } else {
                            isFavourite = false
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showDataFood(foodId: String) {
        FirebaseDatabase.getInstance().getReference("Food").child(foodId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val food = snapshot.getValue(Food::class.java)
                    if (food != null) {
                        Glide.with(applicationContext)
                            .load(food.photo)
                            .fitCenter()
                            .into(binding.imgItemPhoto)
                        binding.foodName.text = food.name
                        binding.foodPrice.text = getCurrencyFormat(food.price)
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

    private fun changeColourFavourite(isFavourite : Boolean){
        if (isFavourite == true){
            binding.favourite.setColorFilter(
                ContextCompat.getColor(
                    baseContext,
                    R.color.red
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else if (isFavourite == false){
            binding.favourite.setColorFilter(
                ContextCompat.getColor(
                    baseContext,
                    R.color.white
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }
}