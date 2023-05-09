package com.example.kedaimbaktimapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodAdminBinding
import com.example.kedaimbaktimapp.databinding.ActivityEditFoodBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class EditFoodActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityEditFoodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle("Ubah Menu Makanan")

        showFood("d")

        binding.uploadPhoto.setOnClickListener{
            openFileChooser()
            binding.cardView.visibility = View.VISIBLE
            binding.uploadPhoto.visibility = View.GONE
        }

        binding.cardView.setOnClickListener {
            openFileChooser()

        }


    }
    private fun openFileChooser() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data // The URI with the location of the file
            binding.foodPicture.setImageURI(selectedFile)
        }
    }

    private fun showFood(foodID: String?) {
        database = FirebaseDatabase.getInstance().getReference("Food")

        database.child(foodID.toString()).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val food = snapshot.getValue(Food::class.java)
                if(food != null){

                    val textViewName = food.name

                    binding.updateName.setText(textViewName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}