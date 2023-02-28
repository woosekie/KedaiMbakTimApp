package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kedaimbaktimapp.User
import com.example.kedaimbaktimapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        if(firebaseUser == null){
            Toast.makeText(getActivity(),"Failed to load the user",Toast.LENGTH_SHORT).show()
        } else {
            showUserProfile(firebaseUser)
        }
    }

    private fun showUserProfile(firebaseUser: FirebaseUser) {

        val userID = firebaseUser.uid
        val referenceProfile: DatabaseReference
        referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
        referenceProfile.child(userID).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if(user != null){

                    val textViewName = firebaseUser.displayName.toString()
                    val textViewEmail = firebaseUser.email.toString()
                    val textViewNumber = user.number

                    binding.profileName.text = textViewName
                    binding.profileNumber.text = textViewNumber
                    binding.profileEmail.text = textViewEmail
                    binding.profleUsername.text = firebaseUser.uid
                    showLoading(false)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(getActivity(),"Failed to load the user",Toast.LENGTH_SHORT).show()
                showLoading(false)
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentProfileBinding.inflate(inflater,container, false)
        return binding.root
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {

    }
}