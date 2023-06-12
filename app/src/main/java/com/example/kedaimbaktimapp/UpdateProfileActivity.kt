package com.example.kedaimbaktimapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kedaimbaktimapp.databinding.ActivityUpdateProfileBinding
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val mobileNumberPattern = Pattern.compile("^(^\\+62|62|^08)(\\d{3,4}-?){2}\\d{3,4}\$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle("Ubah Profile")

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        numberValidate()
        showUserProfile(firebaseUser)

        binding.btnUpdate.setOnClickListener {
            val name = binding.updateName.text.toString()
            val number = binding.updateNumber.text.toString()

            if (name.isNotEmpty() && number.isNotEmpty()){
                if(number.matches(mobileNumberPattern.toRegex())){
                    updateUserProfile(firebaseUser)
                    Toast.makeText(baseContext, "Informasi berhasil diubah", Toast.LENGTH_SHORT).show()
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    finish()
                } else {
                    Toast.makeText(baseContext, "Format nomor salah", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext, "Kolom tidak boleh kososg", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateUserProfile(firebaseUser: FirebaseUser?) {
        val name = binding.updateName.text.toString()
        val number = binding.updateNumber.text.toString()

        database = FirebaseDatabase.getInstance().getReference("Registered Users")

        val userId = firebaseUser?.uid

        database.child(userId.toString()).child("name").setValue(name)
        database.child(userId.toString()).child("number").setValue(number)
    }

    private fun showUserProfile(firebaseUser: FirebaseUser?) {
        val userID = firebaseUser?.uid
        database = FirebaseDatabase.getInstance().getReference("Registered Users")

        database.child(userID.toString()).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if(user != null){

                    val textViewName = user.name
                    val textViewNumber = user.number

                    binding.updateName.setText(textViewName)
                    binding.updateNumber.setText(textViewNumber)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun numberValidate() {
        binding.updateNumber.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.updateNumber.error = validNumber()
            }
        }
    }

    private fun validNumber(): String? {
        val numberValue = binding.updateNumber.text.toString()
        if(!numberValue.matches(mobileNumberPattern.toRegex())) {
            return "Invalid Phone Number"
        }
        return null
    }

}