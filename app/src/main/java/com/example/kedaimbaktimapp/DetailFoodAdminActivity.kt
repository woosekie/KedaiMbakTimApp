package com.example.kedaimbaktimapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodAdminBinding
import com.example.kedaimbaktimapp.model.Food

class DetailFoodAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFoodAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFoodAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val food = intent.getParcelableExtra<Food>("detail_food") as Food
        Glide.with(applicationContext)
            .load(food.photo)
            .into(binding.imgItemPhoto)
        binding.tvItemName.text = food.name
        binding.tvItemPrice.text = "Rp. " + food.price

        supportActionBar?.setTitle(food.name)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.option_menu_food, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change -> {
                val food = intent.getParcelableExtra<Food>("detail_food") as Food
                val intent = Intent(this, EditFoodActivity::class.java)
                intent.putExtra("detail_food", food)
                this.startActivity(Intent(intent))
            }
            R.id.action_delete -> {
                AlertDialog.Builder(this)
                    .setTitle("Hapus Makanan")
                    .setMessage("Apakah kamu yakin ingin menghapus makanan ini ?")
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        deleteFood()
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.success_logout),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .setNegativeButton(getString(R.string.no)) { _, _ -> }
                    .show()
            }
        }
        return true
    }

    private fun deleteFood() {
        TODO("Not yet implemented")
    }


}