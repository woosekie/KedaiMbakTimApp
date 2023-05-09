package com.example.kedaimbaktimapp.fragment

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kedaimbaktimapp.AddFoodActivity
import com.example.kedaimbaktimapp.adapter.ListFoodAdminAdapter
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
    private lateinit var selectedChipData: ArrayList<String>
    private lateinit var auth: FirebaseAuth

//    private lateinit var searchView: SearchView

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

        binding.shimer.startShimmer()
        binding.rvFood.setHasFixedSize(true)
        binding.rvFood.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvFood.setNestedScrollingEnabled(false)

        getItemData()
        getWelcomeText()

        binding.chip1.setOnClickListener {
            binding.rvFood.adapter = ListFoodAdminAdapter(list)
            getItemData()
        }

        binding.chip2.setOnClickListener {
            binding.rvFood.adapter = ListFoodAdminAdapter(list)
            var data = "nasi kotak"
            filterFood(data)
        }

        binding.chip3.setOnClickListener {
            binding.rvFood.adapter = ListFoodAdminAdapter(list)
            var data = "tumini"
            filterFood(data)
        }

        binding.chip4.setOnClickListener {
            binding.rvFood.adapter = ListFoodAdminAdapter(list)
            var data = "bento"
            filterFood(data)
        }

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Menambahkan menu baru yang ditampilkan", Snackbar.LENGTH_LONG)
                .show()
            val intent = Intent(activity, AddFoodActivity::class.java)
            startActivity(intent)
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
                            binding.timeText.text = "Selamat Pagi, Admin"
                        } else if (timeOfDay >= 12 && timeOfDay < 16) {
                            binding.timeText.text = "Selamat Siang, Admin"
                        } else if (timeOfDay >= 16 && timeOfDay < 18) {
                            binding.timeText.text = "Selamat Sore, Admin"
                        } else if (timeOfDay >= 18 && timeOfDay < 24) {
                            binding.timeText.text = "Selamat Malam, Admin"                        }
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
                binding.rvFood.adapter = ListFoodAdminAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        binding.rvFood.adapter?.notifyDataSetChanged()
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
                        binding.rvFood.adapter = ListFoodAdminAdapter(list)
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