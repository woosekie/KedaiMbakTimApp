package com.example.kedaimbaktimapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kedaimbaktimapp.databinding.ActivityAddFoodBinding
import com.example.kedaimbaktimapp.model.Food
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding
    private lateinit var sList: ArrayList<String>
    var type = ""
    private var uriImage: Uri? = null
    private lateinit var urlImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get user input data
        dropDownMenu()

        binding.uploadPhoto.setOnClickListener {
            openFileChooser()
        }
        binding.cardView.setOnClickListener {
            openFileChooser()
        }
        binding.btnAddMenu.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val key = database.getReference("Food").push().key
            val foodid = key.toString()
            val foodname = binding.foodName.text.toString()
            val price: String = binding.foodPrice.getText().toString()
            val description = binding.desc.text.toString()

            if (uriImage != null && foodid.isNotEmpty() && foodname.isNotEmpty() && price != null && description.isNotEmpty()) {
                addFood(foodid, foodname, price.toInt(), "", description, type)
                uploadImg(foodid)
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

    private fun addFood(
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
                    Toast.makeText(baseContext, getString(R.string.add_food_success), Toast.LENGTH_SHORT).show()
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
            uriImage = data?.data!! // The URI with the location of the file
            binding.foodPicture.setImageURI(uriImage)
            binding.cardView.visibility = View.VISIBLE
            binding.uploadPhoto.visibility = View.GONE
        }
    }


}