package com.example.kedaimbaktimapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityDetailFoodAdminBinding
import com.example.kedaimbaktimapp.databinding.ActivityEditFoodBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class EditFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditFoodBinding
    private lateinit var database: DatabaseReference
    private lateinit var sList: ArrayList<String>
    var type = ""
    private var uriImage: Uri? = null
    private lateinit var urlImage: String
    private lateinit var foodImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dropDownMenu()

        val foodId = intent.getStringExtra("foodId").toString()
        showFood(foodId)

        binding.cardView.setOnClickListener {
            openFileChooser()
        }

        binding.btnUpdate.setOnClickListener {
            val foodname = binding.updateName.text.toString()
            val price: String = binding.updatePrice.getText().toString()
            val description = binding.description.text.toString()

            if (foodImage != null && foodId.isNotEmpty() && foodname.isNotEmpty() && price != null && description.isNotEmpty()) {
                editFood(foodId, foodname, price.toInt(), foodImage, description, type)
                if (uriImage != null){
                    uploadImg(foodId)
                }
                finish()
            } else {
                Toast.makeText(baseContext, getString(R.string.form_not_null), Toast.LENGTH_SHORT).show()
            }
        }


    }
    private fun dropDownMenu() {
        sList = arrayListOf()
        sList.add("Nasi Kotak")
        sList.add("Tumpeng")
        sList.add("Nasi Bento")
        sList.add("Nasi Urap")
        val dataAdapter = ArrayAdapter(this, R.layout.selected_item_spinner, sList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = dataAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p0 != null) {
                    type = p0.selectedItem.toString()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    private fun editFood(
        foodId: String,
        name: String,
        price: Int,
        photo: String,
        description: String,
        type: String
    ) {
        val food = Food(foodId, name, price, photo, description, type)
        FirebaseDatabase.getInstance().getReference("Food").child(foodId).setValue(food)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, getString(R.string.update_food_success), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }

    }

    private fun uploadImg(foodid: String) {
        val storageRegerence = FirebaseStorage.getInstance().getReference("FoodImg")
        val imgName = foodid + "." + getFileExtension(uriImage)
        val fireRegerence = storageRegerence.child(imgName)

        fireRegerence.putFile(uriImage!!)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(tasksnapshot: UploadTask.TaskSnapshot?) {
                    fireRegerence.downloadUrl.addOnSuccessListener(object :
                        OnSuccessListener<Uri> {
                        override fun onSuccess(p0: Uri?) {
                            var downloadUrl: Uri? = p0
                            fireRegerence.getDownloadUrl()
                                .addOnSuccessListener(OnSuccessListener<Uri?> {
                                    // Got the download URL for 'users/me/profile.png'
                                    urlImage = downloadUrl.toString()

                                    FirebaseDatabase.getInstance().getReference("Food")
                                        .child(foodid).child("photo").setValue(urlImage)

                                }).addOnFailureListener(OnFailureListener {
                                    // Handle any errors
                                })
                        }

                    })
                }

            })


    }

    private fun getFileExtension(uri: Uri?): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
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
            uriImage = data?.data // The URI with the location of the file
            binding.foodPicture.setImageURI(uriImage)
        }
    }

    private fun showFood(foodId: String?) {
        database = FirebaseDatabase.getInstance().getReference("Food")

        database.child(foodId.toString()).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val food = snapshot.getValue(Food::class.java)
                if(food != null){
                    binding.updateName.setText(food.name)
                    binding.updatePrice.setText(food.price.toString())
                    binding.description.setText(food.description)
                    Glide.with(applicationContext)
                        .load(food.photo)
                        .into(binding.foodPicture)
                    foodImage = food.photo
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}