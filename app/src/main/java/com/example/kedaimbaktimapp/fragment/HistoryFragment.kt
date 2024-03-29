package com.example.kedaimbaktimapp.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kedaimbaktimapp.adapter.ListFoodAdapter
import com.example.kedaimbaktimapp.adapter.ListTransactionAdapter
import com.example.kedaimbaktimapp.databinding.FragmentHistoryBinding
import com.example.kedaimbaktimapp.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var ReferenceTransaction: DatabaseReference
    private var list = ArrayList<Transaction>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvTransaction.setHasFixedSize(true)
        binding.rvTransaction.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvTransaction.visibility = View.GONE
        binding.imgEmpty.visibility = View.VISIBLE

        getItemData()

        binding.toggleButtonGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->

            if (isChecked) {
                binding.rvTransaction.adapter = ListTransactionAdapter(list)
                when (checkedId) {
                    binding.toggle1.id -> getItemDataFilter("Belum diproses")
                    binding.toggle2.id -> getItemDataFilter("Sedang diproses")
                    binding.toggle3.id -> getItemDataFilter("Siap diambil")
                    binding.toggle4.id -> getItemDataFilter("Dalam pengiriman")
                    binding.toggle5.id -> getItemDataFilter("Selesai")
                }
            } else {
                if (toggleButtonGroup.checkedButtonId == View.NO_ID) {
                    getItemData()
                }
            }
        }

    }

    private fun getItemData() {
        auth = Firebase.auth
        val user = auth.currentUser
        ReferenceTransaction = FirebaseDatabase.getInstance().getReference("Registered Users")
        ReferenceTransaction.child(user!!.uid).child("transaction")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (itemSnapshot in snapshot.children) {
                        val dataClass = itemSnapshot.getValue(Transaction::class.java)
                        if (dataClass != null) {
                            binding.rvTransaction.visibility = View.VISIBLE
                            binding.imgEmpty.visibility = View.GONE
                            list.add(dataClass)
                        }
                    }
                    binding.rvTransaction.adapter = ListTransactionAdapter(list)


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun getItemDataFilter(data: String) {
        auth = Firebase.auth
        val user = auth.currentUser
        ReferenceTransaction = FirebaseDatabase.getInstance().getReference("Registered Users").child(user!!.uid).child("transaction")
        ReferenceTransaction.orderByChild("status").equalTo(data)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (itemSnapshot in snapshot.children) {
                        val dataClass = itemSnapshot.getValue(Transaction::class.java)
                        if (dataClass != null) {
                            binding.rvTransaction.visibility = View.VISIBLE
                            binding.imgEmpty.visibility = View.GONE
                            list.add(dataClass)
                        }
                    }
                    binding.rvTransaction.adapter = ListTransactionAdapter(list)


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }



}