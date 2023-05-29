package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.disklrucache.DiskLruCache
import com.example.kedaimbaktimapp.adapter.ListFoodAdapter
import com.example.kedaimbaktimapp.adapter.ListFoodAdminAdapter
import com.example.kedaimbaktimapp.databinding.FragmentCartBinding
import com.example.kedaimbaktimapp.model.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var ReferenceFood: DatabaseReference
    private var list = ArrayList<Food>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater)
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFood.setHasFixedSize(true)
        binding.rvFood.layoutManager = GridLayoutManager(requireActivity(), 2)
        getItemData()

    }

    private fun getItemData() {
        val firebaseUser = Firebase.auth.currentUser
        val userID = firebaseUser?.uid
        FirebaseDatabase.getInstance().getReference("Registered Users").child(userID.toString()).child("favourite")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (itemSnapshot in snapshot.children) {
                        val dataClass = itemSnapshot.getValue(Food::class.java)
                        if (dataClass != null) {
                            list.add(dataClass)
                        }
                    }
                    binding.rvFood.adapter = ListFoodAdapter(list)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }



}