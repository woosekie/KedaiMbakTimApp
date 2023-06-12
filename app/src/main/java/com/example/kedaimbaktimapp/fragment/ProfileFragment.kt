package com.example.kedaimbaktimapp.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.AboutUsActivity
import com.example.kedaimbaktimapp.LoginActivity
import com.example.kedaimbaktimapp.R
import com.example.kedaimbaktimapp.UpdateProfileActivity
import com.example.kedaimbaktimapp.databinding.FragmentProfileBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var referenceUser : DatabaseReference
    private var uriImage: Uri? = null
    private lateinit var urlImage: String
    private lateinit var  userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        userId = firebaseUser?.uid.toString()

        showUserProfile(userId)

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.want_to_logout))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    signOut()
                    Toast.makeText(activity, getString(R.string.success_logout), Toast.LENGTH_LONG)
                        .show()
                }
                .setNegativeButton(getString(R.string.no)) { _, _ -> }
                .show()
        }
        binding.profilePic.setOnClickListener {
            openFileChooser()
        }
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(activity, UpdateProfileActivity::class.java)
            startActivity(intent)
        }
        binding.btnAboutUs.setOnClickListener {
            val intent = Intent(activity, AboutUsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun signOut() {
        Firebase.auth.signOut()
        activity?.finish()
        startActivity(Intent(activity, LoginActivity::class.java))
    }

    private fun showUserProfile(userId: String) {
        referenceUser = Firebase.database.getReference("Registered Users")
        referenceUser.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(com.example.kedaimbaktimapp.model.User::class.java)
                if (user != null) {
                    binding.profileName.text = user.name
                    binding.profileNumber.text = user.email
                    binding.profileEmail.text = user.number
                    binding.profleUsername.text = userId
                    if(user.photo.isNotEmpty()){
                        getActivity()?.let {
                            Glide.with(it.getApplicationContext()).load(user.photo)
                                .into(binding.profilePic)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(getActivity(), R.string.failed_load_data, Toast.LENGTH_SHORT).show()
            }
        })

        //mendapatkan lokasi user
        FirebaseDatabase.getInstance().getReference("Location").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val location =
                        snapshot.getValue(com.example.kedaimbaktimapp.model.Location::class.java)
                    if (location != null) {
                        binding.address.text = location.province +", " + location.regency +", "+ location.subdistrict +", "+ location.area +", "+ location.address +" ("+ location.postalcode+")"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    //put image to storage database and set user photos atribute with the image url
    private fun uploadImg(userId: String) {
        val storageReference = FirebaseStorage.getInstance().getReference("UserImg")
        val imgName = userId + "." + getFileExtension(uriImage)
        val fileReference = storageReference.child(imgName)
        fileReference.putFile(uriImage!!)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(tasksnapshot: UploadTask.TaskSnapshot?) {
                    fileReference.downloadUrl.addOnSuccessListener(object :
                        OnSuccessListener<Uri> {
                        override fun onSuccess(p0: Uri?) {
                            val downloadUrl: Uri? = p0
                            fileReference.getDownloadUrl()
                                .addOnSuccessListener(OnSuccessListener<Uri?> {
                                    urlImage = downloadUrl.toString()
                                    FirebaseDatabase.getInstance().getReference("Registered Users")
                                        .child(userId).child("photo").setValue(urlImage)
                                }).addOnFailureListener(OnFailureListener {
                                    // Handle any errors
                                })
                        }

                    })
                }

            })
    }

    private fun getFileExtension(uri: Uri?): String? {
        val cR = requireActivity().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }

    //implicit intent to get file
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
            binding.profilePic.setImageURI(uriImage)
            if (uriImage != null) {
                uploadImg(userId)
            }
        }
    }

}