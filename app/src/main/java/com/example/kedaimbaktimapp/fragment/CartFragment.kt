package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kedaimbaktimapp.adapter.ListFoodAdapter
import com.example.kedaimbaktimapp.databinding.FragmentCartBinding
import com.example.kedaimbaktimapp.model.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
        ReferenceFood = FirebaseDatabase.getInstance().getReference("Food")
        ReferenceFood.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(Food::class.java)
                    if (dataClass != null) {
                        val key : String? = FirebaseDatabase.getInstance().getReference("Food").push().getKey()
//                        Log.d("Foodnya", "ini : " + key)
                        list.add(dataClass)
                    }
                }
                binding.rvFood.visibility = View.VISIBLE
                binding.rvFood.adapter = ListFoodAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



}