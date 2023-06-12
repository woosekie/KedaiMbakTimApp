package com.example.kedaimbaktimapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.kedaimbaktimapp.AboutUsActivity
import com.example.kedaimbaktimapp.MapActivity
import com.example.kedaimbaktimapp.databinding.FragmentDashboardBinding
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var builderDialog: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private lateinit var auth: FirebaseAuth
    private var isAdmin: Boolean = false
    private lateinit var number: String
    private lateinit var account: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        val userId = firebaseUser?.uid

        if (userId != null) {
//            checkRole(userId)
            getDataSummary()
            getAdminNumber(userId)
            showAddress(userId)
            getTikTok()
            binding.update.setOnClickListener {
                showSettingDialog(com.example.kedaimbaktimapp.R.layout.admin_settings, userId)
            }
            binding.location.setOnClickListener{
                val intent = Intent(activity, MapActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showSettingDialog(adminSettings: Int, userId: String) {
        builderDialog = AlertDialog.Builder(requireActivity())
        val layout: View = layoutInflater.inflate(adminSettings, null)
        val button: Button = layout.findViewById(com.example.kedaimbaktimapp.R.id.save)
        val editNumber: EditText = layout.findViewById(com.example.kedaimbaktimapp.R.id.number)
        val editTikTok: EditText = layout.findViewById(com.example.kedaimbaktimapp.R.id.tiktok)

        editNumber.setText(number)
        editTikTok.setText(account)


        builderDialog.setView(layout)
        alertDialog = builderDialog.create()
        alertDialog.show()

        button.setOnClickListener {
            val number = editNumber.text.toString()
            val tiktok = editTikTok.text.toString()
            if (number.isNotEmpty() && tiktok.isNotEmpty()) {
                FirebaseDatabase.getInstance().getReference("Registered Users").child(userId)
                    .child("number").setValue(number)
                FirebaseDatabase.getInstance().getReference("Setting").child("social")
                    .child("tiktok").setValue(tiktok)
                Toast.makeText(
                    getActivity(),
                    com.example.kedaimbaktimapp.R.string.success_add_data,
                    Toast.LENGTH_SHORT
                ).show()
                alertDialog.dismiss()
            } else {
                Toast.makeText(
                    getActivity(),
                    com.example.kedaimbaktimapp.R.string.form_not_null,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun getDataSummary() {
        FirebaseDatabase.getInstance().getReference("Registered Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val numOfLikes: Long = snapshot.getChildrenCount()
                    binding.totalUser.text = numOfLikes.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        FirebaseDatabase.getInstance().getReference("Transaction")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val numOfLikes: Long = snapshot.getChildrenCount()
                    binding.totalOrder.text = numOfLikes.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        FirebaseDatabase.getInstance().getReference("Registered Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var numMaleCases = 0
                    for (data in snapshot.children) {
                        if (data.child("transaction").exists()) {
                            numMaleCases++
                            binding.totalCustomer.text = numMaleCases.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

//    private fun checkRole(userId: String) {
//        FirebaseDatabase.getInstance().getReference("Registered Users").child(userId).child("admin")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val value = snapshot.value
//                    if (value!!.equals(true)) {
//                        isAdmin = true
//                    } else {
//                        isAdmin = false
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//            })
//    }

    private fun getAdminNumber(userId: String) {
        FirebaseDatabase.getInstance().getReference("Registered Users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        number = user.number
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun getTikTok() {
        FirebaseDatabase.getInstance().getReference("Setting").child("social").child("tiktok")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tiktok = snapshot.getValue().toString()
                    account = tiktok
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showAddress(userId: String) {
        FirebaseDatabase.getInstance().getReference("Location").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val location =
                        snapshot.getValue(com.example.kedaimbaktimapp.model.Location::class.java)
                    if (location != null) {
                        binding.address.text =
                            location.province + ", " + location.regency + ", " + location.subdistrict + ", " + location.area + ", " + location.address + " (" + location.postalcode + ")"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


}
