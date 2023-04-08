package com.example.kedaimbaktimapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kedaimbaktimapp.AddFoodActivity
import com.example.kedaimbaktimapp.MainActivity
import com.example.kedaimbaktimapp.adapter.ListFoodAdminAdapter
import com.example.kedaimbaktimapp.databinding.FragmentHomeBinding
import com.example.kedaimbaktimapp.databinding.FragmentManuBinding
import com.example.kedaimbaktimapp.model.Food
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class MenuFragment : Fragment() {

    private lateinit var binding: FragmentManuBinding
    private lateinit var ReferenceFood: DatabaseReference
    private var list = ArrayList<Food>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManuBinding.inflate(layoutInflater)
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFood.setHasFixedSize(true)
        binding.rvFood.layoutManager = GridLayoutManager(requireActivity(), 2)

        getItemData()

        binding.chip1.setOnClickListener {
            binding.rvFood.adapter = ListFoodAdminAdapter(list)
            getItemData()
        }

        binding.chip2.setOnClickListener {
            binding.rvFood.adapter = ListFoodAdminAdapter(list)
            filterPrice()
        }


        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Menambahkan menu baru yang ditampilkan", Snackbar.LENGTH_LONG)
                .show()
            val intent = Intent(activity, AddFoodActivity::class.java)
            startActivity(intent)
        }
    }


    private fun getItemData() {
        ReferenceFood = FirebaseDatabase.getInstance().getReference("Food")
        ReferenceFood.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(Food::class.java)
                    if (dataClass != null) {
                        list.add(dataClass)
                    }
                }
                binding.rvFood.adapter = ListFoodAdminAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun filterPrice() {
        // Specifying path and filter category and adding a
        ReferenceFood = FirebaseDatabase.getInstance().getReference("Food")
        ReferenceFood.orderByChild("name").equalTo("Nasi Ayam")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        list.clear()
                        for (itemSnapshot in snapshot.children) {
                            val dataClass = itemSnapshot.getValue(Food::class.java)
                            if (dataClass != null) {
                                list.add(dataClass)
                            }
                        }
                        binding.rvFood.adapter = ListFoodAdminAdapter(list)
                    } else {

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}