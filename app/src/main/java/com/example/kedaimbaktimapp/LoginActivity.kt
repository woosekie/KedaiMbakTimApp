package com.example.kedaimbaktimapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.kedaimbaktimapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        showLoading(false)

        emailValidate()
        passwordValidate()

        binding.btnSignIn.setOnClickListener {
            val email = binding.emailLogin.text.toString()
            val password = binding.passLogin.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(baseContext, getString(R.string.form_not_null), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }

        binding.toRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun login(email: String, password: String) {
        showLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showLoading(false)
                    Toast.makeText(baseContext, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    // check current user
                    val currentUser: FirebaseUser? = auth.currentUser
                    if (currentUser != null) {
                        checkRole(currentUser)
                    }
                } else {
                    showLoading(false)
                    Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }


    //if already login start activity with check role
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkRole(currentUser)
        }
    }

    private fun checkRole(firebaseUser: FirebaseUser) {
        showLoading(true)
        val userID = firebaseUser.uid
        val referenceRole: DatabaseReference
        referenceRole = FirebaseDatabase.getInstance().getReference("Registered Users").child(userID)
        referenceRole.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(com.example.kedaimbaktimapp.model.User::class.java)
                val isAdmin = user?.isAdmin
                if (isAdmin == false) {
                    reload()
                } else if (isAdmin == true) {
                    reloadAdmin()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun reload() {
        showLoading(false)
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    private fun reloadAdmin() {
        showLoading(false)
        startActivity(Intent(applicationContext, AdminActivity::class.java))
        finish()
    }

    private fun emailValidate() {
        binding.emailLogin.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.emailLogin.error = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailValue = binding.emailLogin.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            return "Invalid Email Address"
        }
        return null
    }

    private fun passwordValidate() {
        binding.passLogin.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.passLogin.error = validPass()
            }
        }
    }

    private fun validPass(): String? {
        val passValue = binding.passLogin.text.toString()
        if (passValue.length < 6) {
            return "Minimum 6 Character Password"
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