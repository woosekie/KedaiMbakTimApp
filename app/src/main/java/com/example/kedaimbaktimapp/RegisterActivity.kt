package com.example.kedaimbaktimapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kedaimbaktimapp.databinding.ActivityRegisterBinding
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ReferenceProfile: DatabaseReference
    private val mobileNumberPattern = Pattern.compile("^(^\\+62|62|^08)(\\d{3,4}-?){2}\\d{3,4}\$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.register)
        auth = Firebase.auth

        showLoading(false)
        emailValidate()
        passwordValidate()
        numberValidate()

        binding.btnRegis.setOnClickListener {
            val name = binding.nameRegis.text.toString()
            val email = binding.emailRegis.text.toString()
            val password = binding.passRegis.text.toString()
            val number = binding. numberRegis.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && number.isNotEmpty()) {
                if (number.matches(mobileNumberPattern.toRegex())){
                    registerUser(name, email, password, number)
                } else {
                    Toast.makeText(baseContext, getString(R.string.number_not_matches), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext, getString(R.string.form_not_null), Toast.LENGTH_SHORT).show()
            }
        }

        binding.toLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser(name: String, email: String, password: String, number:String){
        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showLoading(false)
                    val user = auth.currentUser

                    //enter user data to firebase realtime database
                    val users = User(name, email, number, isAdmin = false)
                    ReferenceProfile = Firebase.database.getReference("Registered Users")
                    if (user != null) {
                        ReferenceProfile.child(user.uid).setValue(users).addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, R.string.register_fail, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }

                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                                startActivity(Intent(applicationContext, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(baseContext, "User profile update failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    showLoading(false)
                    Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

    }
    private fun emailValidate() {
        binding.emailRegis.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.emailRegis.error = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailValue = binding.emailRegis.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            return "Invalid Email Address"
        }
        return null
    }

    private fun passwordValidate() {
        binding.passRegis.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.passRegis.error = validPass()
            }
        }
    }

    private fun validPass(): String? {
        val passValue = binding.passRegis.text.toString()
        if (passValue.length < 6) {
            return "Minimum 6 Character Password"
        }
        return null
    }

    private fun numberValidate() {
        binding.numberRegis.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.numberRegis.error = validNumber()
            }
        }
    }

    private fun validNumber(): String? {
        val numberValue = binding.numberRegis.text.toString()
        if(!numberValue.matches(mobileNumberPattern.toRegex())) {
            return "Invalid Phone Number"
        }
        return null
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}


