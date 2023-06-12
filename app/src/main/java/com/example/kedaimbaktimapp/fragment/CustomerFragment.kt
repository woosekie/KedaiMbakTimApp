package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kedaimbaktimapp.adapter.ListCustomerAdapter
import com.example.kedaimbaktimapp.adapter.ListFoodAdapter
import com.example.kedaimbaktimapp.adapter.ListTransactionAdapter
import com.example.kedaimbaktimapp.databinding.FragmentCustomerBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Transaction
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.database.*

class CustomerFragment : Fragment() {

    private lateinit var binding: FragmentCustomerBinding
    private var list = ArrayList<User>()
    private lateinit var adapter: ListCustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCustomerBinding.inflate(layoutInflater)
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvUser.setHasFixedSize(true)
        binding.rvUser.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


        list = ArrayList()
        adapter = ListCustomerAdapter(list)
        binding.rvUser.adapter = adapter

        getItemData()

    }

    private fun getItemData() {
        FirebaseDatabase.getInstance().getReference("Registered Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    searchView()
                    for (itemSnapshot in snapshot.children) {
                        if (itemSnapshot.child("transaction").exists()) {
                            val dataClass = itemSnapshot.getValue(User::class.java)
                            if (dataClass != null) {
                                binding.rvUser.visibility = View.VISIBLE
                                list.add(dataClass)
                            }
                        }
                    }
//                    binding.rvUser.adapter = ListCustomerAdapter(list)
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun searchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                return false
            }
            override fun onQueryTextChange(p0: String): Boolean {
                searchList(p0)
                return true
            }
        })
    }

    fun searchList(text: String) {
        val searchList = ArrayList<User>()
        for (dataClass in list) {
            if (dataClass.name?.lowercase()?.contains(text.toLowerCase()) == true) {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }
}