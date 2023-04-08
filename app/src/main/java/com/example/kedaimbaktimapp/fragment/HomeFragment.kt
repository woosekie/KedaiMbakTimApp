package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kedaimbaktimapp.adapter.ListFoodAdapter
import com.example.kedaimbaktimapp.databinding.FragmentHomeBinding
import com.example.kedaimbaktimapp.model.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var ReferenceFood: DatabaseReference
    private var list = ArrayList<Food>()
    private lateinit var selectedChipData: ArrayList<String>
    private lateinit var auth: FirebaseAuth

//    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shimer.startShimmer()
        binding.rvFood.setHasFixedSize(true)
        binding.rvFood.layoutManager = GridLayoutManager(requireActivity(), 2)

        getItemData()
        getWelcomeText()

        binding.chip1.setOnClickListener {
            binding.rvFood.adapter = ListFoodAdapter(list)
            getItemData()
        }

        binding.chip2.setOnClickListener {
            binding.rvFood.adapter = ListFoodAdapter(list)
            var data = "nasi kotak"
            filterFood(data)
        }

        binding.chip3.setOnClickListener {
            binding.rvFood.adapter = ListFoodAdapter(list)
            var data = "tumini"
            filterFood(data)
        }



//        searchView.clearFocus()
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                filterList(newText.toString())
//                return true
//            }
//
//        })
    }

    private fun getWelcomeText() {
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy")
        val curentDate = Date()
        val current = formatter.format(curentDate)
        binding.dateText.text = current

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        val userID = firebaseUser?.uid
        val referenceProfile: DatabaseReference
        referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
        if (userID != null) {
            referenceProfile.child(userID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(com.example.kedaimbaktimapp.model.User::class.java)
                    if (user != null) {

                        val c = Calendar.getInstance()
                        val timeOfDay = c[Calendar.HOUR_OF_DAY]
                        if (timeOfDay >= 0 && timeOfDay < 12) {
                            val time = "Selamat pagi, " + user.name.substringBefore(" ")
                            binding.nameText.text = time
                        } else if (timeOfDay >= 12 && timeOfDay < 16) {
                            val time = "Selamat siang, " + user.name.substringBefore(" ")
                            binding.nameText.text = time
                        } else if (timeOfDay >= 16 && timeOfDay < 18) {
                            val time = "Selamat sore, " + user.name.substringBefore(" ")
                            binding.nameText.text = time
                        } else if (timeOfDay >= 18 && timeOfDay < 24) {
                            val time = "Selamat malam, " + user.name.substringBefore(" ")
                            binding.nameText.text = time
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
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
                binding.shimer.stopShimmer()
                binding.shimer.visibility = View.GONE
                binding.rvFood.visibility = View.VISIBLE
                binding.rvFood.adapter = ListFoodAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

//    private fun filterList(query: String){
//        val searchList: ArrayList<Food> = ArrayList()
//        for (food: Food in list) {
//            if(food.name.toLowerCase().contains(query.toLowerCase())){
//                searchList.add(food)
//            }
//        }
//        var adapter: ListFoodAdapter? = ListFoodAdapter(list)
//        adapter?.searchDataList(searchList)
//    }

    private fun filterFood(data: String) {
        // Specifying path and filter category and adding a
        ReferenceFood = FirebaseDatabase.getInstance().getReference("Food")
        ReferenceFood.orderByChild("type").equalTo(data)
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
                        binding.shimer.stopShimmer()
                        binding.shimer.visibility = View.GONE
                        binding.rvFood.visibility = View.VISIBLE
                        binding.rvFood.adapter = ListFoodAdapter(list)
                    } else {

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


    companion object {
    }
}