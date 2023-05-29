package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kedaimbaktimapp.R
import com.example.kedaimbaktimapp.adapter.ListOrderAdapter
import com.example.kedaimbaktimapp.databinding.FragmentHistoryBinding
import com.example.kedaimbaktimapp.databinding.FragmentOrderBinding
import com.example.kedaimbaktimapp.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private lateinit var ReferenceFood: DatabaseReference
    private var list = ArrayList<Transaction>()
    private lateinit var sList: java.util.ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(layoutInflater)
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvTransaction.setHasFixedSize(true)
        binding.rvTransaction.layoutManager= LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvTransaction.visibility = View.GONE
//        binding.imgEmpty.visibility = View.VISIBLE
        getItemData()

        sList = arrayListOf("Default","Waktu kirim", "Waktu order")
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.selected_item_spinner, sList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = dataAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p0 != null) {
                    if(p0.getItemAtPosition(p2) == "Waktu kirim"){
                        getItemDataByDeadline()
                    } else if (p0.getItemAtPosition(p2) == "Default"){
                        getItemData()
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                getItemData()
            }

        }

    }

    private fun getItemData() {
        ReferenceFood = FirebaseDatabase.getInstance().getReference("Transaction")
        ReferenceFood.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(Transaction::class.java)
                    if (dataClass != null) {
                        binding.rvTransaction.visibility = View.VISIBLE
//                        binding.imgEmpty.visibility = View.GONE
                        list.add(dataClass)
                    }
                }
                binding.rvTransaction.adapter = ListOrderAdapter(list)


            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun getItemDataByDeadline() {
        ReferenceFood = FirebaseDatabase.getInstance().getReference("Transaction")
        ReferenceFood.orderByChild("dateSend").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(Transaction::class.java)
                    if (dataClass != null) {
                        binding.rvTransaction.visibility = View.VISIBLE
//                        binding.imgEmpty.visibility = View.GONE
                        list.add(dataClass)
                    }
                }
                binding.rvTransaction.adapter = ListOrderAdapter(list)


            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


}