package com.example.kedaimbaktimapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kedaimbaktimapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPassword : AppCompatActivity() {

    private lateinit var binding : ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnResetPassword.setOnClickListener {
            val emailAddress = binding.resetEmail.text.toString()
            resetPassword(emailAddress)
        }
    }

    private fun resetPassword(email: String) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    Toast.makeText(baseContext, getString(R.string.reset_password_success), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
}