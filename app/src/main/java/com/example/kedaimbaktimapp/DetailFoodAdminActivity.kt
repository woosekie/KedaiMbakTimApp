package com.example.kedaimbaktimapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.adapter.ListRateAdapter
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodAdminBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Rating
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class DetailFoodAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFoodAdminBinding
    private lateinit var foodId: String
    private var list = ArrayList<Rating>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFoodAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        foodId = intent.getStringExtra("foodId").toString()
        val firebaseUser = Firebase.auth.currentUser
        val userId = firebaseUser?.uid

        if (foodId != null && userId != null) {
            showDataFood(foodId)
            getRating(foodId)
        }
    }

    private fun showDataFood(foodId: String) {
        FirebaseDatabase.getInstance().getReference("Food").child(foodId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val food = snapshot.getValue(Food::class.java)
                    if (food != null) {
                        supportActionBar?.setTitle(food.name)
                        showDataFood(food.foodId)
                        Glide.with(applicationContext)
                            .load(food.photo)
                            .into(binding.imgItemPhoto)
                        binding.foodName.text = food.name
                        binding.foodPrice.text = getCurrencyFormat(food.price)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.failed_load_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.option_menu_food, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change -> {
                val intent = Intent(this, EditFoodActivity::class.java)
                intent.putExtra("foodId", foodId)
                this.startActivity(Intent(intent))
            }
            R.id.action_delete -> {
                AlertDialog.Builder(this)
                    .setTitle("Hapus Makanan")
                    .setMessage("Apakah kamu yakin ingin menghapus makanan ini ?")
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        deleteFood(foodId)
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.success_delete),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .setNegativeButton(getString(R.string.no)) { _, _ -> }
                    .show()
            }
        }
        return true
    }

    private fun deleteFood(foodId: String) {
        FirebaseDatabase.getInstance().getReference("Food").child(foodId).removeValue()
        FirebaseDatabase.getInstance().getReference("Rating").orderByChild("foodId")
            .equalTo(foodId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (itemSnapshot in dataSnapshot.children) {
                        if (itemSnapshot != null) {
                            itemSnapshot.ref.removeValue()
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
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


}