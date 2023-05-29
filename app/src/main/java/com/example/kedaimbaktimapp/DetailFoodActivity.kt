package com.example.kedaimbaktimapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodBinding
import com.example.kedaimbaktimapp.model.Food
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*


class DetailFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFoodBinding
    private lateinit var FavouriteDatabase: DatabaseReference
    private var isFavourite: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val food = intent.getParcelableExtra<Food>("detail_food") as Food
        val firebaseUser = Firebase.auth.currentUser
        val userID = firebaseUser?.uid

        if (userID != null) {
            checkFavourite(food, userID)
            showData(food)

            binding.favourite.setOnClickListener {
                if (isFavourite == true){
                    removeFavouriteFood(food, userID)
                }
                else if (isFavourite == false) {
                    addFavouriteFood(food, userID)
                }
            }
        }
    }

    private fun addFavouriteFood(food: Food, uid: String) {
        FavouriteDatabase =
            Firebase.database.getReference("Registered Users").child(uid)
                .child("favourite")
        FavouriteDatabase.child(food.foodId).setValue(food)
    }

    private fun removeFavouriteFood(food: Food, uid: String){
        val ref = FirebaseDatabase.getInstance().reference
        val applesQuery = ref.child("Registered Users").child(uid).child("favourite").orderByChild("foodId").equalTo(food.foodId)

        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun checkFavourite(food: Food, uid: String) {
        FirebaseDatabase.getInstance().getReference("Registered Users").child(uid)
            .child("favourite").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        if (data.key!!.equals(food.foodId)) {
                            binding.favourite.setColorFilter(
                                ContextCompat.getColor(
                                    baseContext,
                                    R.color.red
                                ), android.graphics.PorterDuff.Mode.SRC_IN
                            )
                            isFavourite = true
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    private fun showData(food: Food) {
        Glide.with(applicationContext)
            .load(food.photo)
            .fitCenter()
            .into(binding.imgItemPhoto)
        binding.tvItemName.text = food.name
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        binding.tvItemPrice.text = format.format(food.price)

        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("detail_food", food)
            this.startActivity(Intent(intent))
        }
    }
}