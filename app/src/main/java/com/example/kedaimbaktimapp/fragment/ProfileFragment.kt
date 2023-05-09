package com.example.kedaimbaktimapp.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.kedaimbaktimapp.AboutUsActivity
import com.example.kedaimbaktimapp.LoginActivity
import com.example.kedaimbaktimapp.R
import com.example.kedaimbaktimapp.UpdateProfileActivity
import com.example.kedaimbaktimapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        storageReference = FirebaseDatabase.getInstance().getReference("DisplayPhoto")
//        val uri : Uri = firebaseUser.photoUrl()

        if(firebaseUser == null){
            Toast.makeText(getActivity(),"Failed to load the user",Toast.LENGTH_SHORT).show()
        } else {
            showUserProfile(firebaseUser)
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    private fun showUserProfile(firebaseUser: FirebaseUser) {

        val userID = firebaseUser.uid
        val referenceProfile: DatabaseReference
        referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
        referenceProfile.child(userID).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(com.example.kedaimbaktimapp.model.User::class.java)
                if(user != null){

                    val textViewName = user.name
                    val textViewEmail = user.email
                    val textViewNumber = user.number

                    binding.profileName.text = textViewName
                    binding.profileNumber.text = textViewNumber
                    binding.profileEmail.text = textViewEmail
                    binding.profleUsername.text = firebaseUser.uid
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(getActivity(),"Failed to load the user",Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentProfileBinding.inflate(inflater,container, false)
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.want_to_logout))
                .setPositiveButton(getString(R.string.yes)){ _, _ -> signOut()
                    Toast.makeText(activity, getString(R.string.success_logout), Toast.LENGTH_LONG).show()}
                .setNegativeButton(getString(R.string.no)){ _, _->}
                .show()
        }
        binding.profilePic.setOnClickListener {
            openFileChooser()
        }
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(activity,  UpdateProfileActivity::class.java)
            startActivity(intent)
        }
        binding.btnAboutUs.setOnClickListener{
            val intent = Intent(activity,  AboutUsActivity::class.java)
            startActivity(intent)
        }
        return binding.root
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
            binding.profilePic.setImageURI(selectedFile)
        }
    }

    companion object {

    }
}