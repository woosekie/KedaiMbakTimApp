package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kedaimbaktimapp.adapter.ListCustomerAdapter
import com.example.kedaimbaktimapp.adapter.ListTransactionAdapter
import com.example.kedaimbaktimapp.databinding.FragmentCustomerBinding
import com.example.kedaimbaktimapp.model.Transaction
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.database.*

class CustomerFragment : Fragment() {

    private lateinit var binding: FragmentCustomerBinding
    private lateinit var ReferenceFood: DatabaseReference
    private var list = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCustomerBinding.inflate(layoutInflater)
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvTransaction.setHasFixedSize(true)
        binding.rvTransaction.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        getItemData()

    }

    private fun getItemData() {
        FirebaseDatabase.getInstance().getReference("Registered Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (itemSnapshot in snapshot.children) {
                        if (itemSnapshot.child("transaction").exists()) {
                            val dataClass = itemSnapshot.getValue(User::class.java)
                            if (dataClass != null) {
                                binding.rvTransaction.visibility = View.VISIBLE
                                list.add(dataClass)
                            }
                        }
                    }
                    binding.rvTransaction.adapter = ListCustomerAdapter(list)


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }


}