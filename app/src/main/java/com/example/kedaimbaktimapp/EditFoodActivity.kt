package com.example.kedaimbaktimapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodAdminBinding
import com.example.kedaimbaktimapp.databinding.ActivityEditFoodBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class EditFoodActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var sList: ArrayList<String>
    private lateinit var binding: ActivityEditFoodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle("Ubah Menu Makanan")
        setDropdownCategory()

        val food = intent.getParcelableExtra<Food>("detail_food") as Food
        val foodId = food.foodId
        showFood(foodId)

        binding.cardView.setOnClickListener {
            openFileChooser()

        }


    }
    private fun setDropdownCategory(){
        sList = arrayListOf()
        sList.add("Nasi Kotak")
        sList.add("Tumpeng")
        sList.add("Nasi Bento")
        val dataAdapter = ArrayAdapter(this, R.layout.selected_item_spinner, sList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = dataAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p0!=null){
//                    (p0.getChildAt(0) as TextView?)?.setTextColor(Color.GRAY)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

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
                    binding.updateName.setText(food.name)
                    binding.updatePrice.setText(food.price.toString())
                    binding.description.setText(food.description)
                    Glide.with(applicationContext)
                        .load(food.photo)
                        .into(binding.foodPicture)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}