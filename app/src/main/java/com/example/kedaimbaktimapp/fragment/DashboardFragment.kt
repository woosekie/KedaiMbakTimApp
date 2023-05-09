package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kedaimbaktimapp.databinding.FragmentDashboardBinding
import com.google.firebase.database.*


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var referenceUser: DatabaseReference;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseDatabase.getInstance().getReference("Registered Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val numOfLikes: Long = snapshot.getChildrenCount()
                binding.totalUser.text = numOfLikes.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        FirebaseDatabase.getInstance().getReference("Transaction").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val numOfLikes: Long = snapshot.getChildrenCount()
                binding.totalOrder.text = numOfLikes.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        FirebaseDatabase.getInstance().getReference("Registered Users").addValueEventListener(object : ValueEventListener {
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


}
