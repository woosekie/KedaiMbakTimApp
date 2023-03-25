package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kedaimbaktimapp.ListFoodAdapter
import com.example.kedaimbaktimapp.R
import com.example.kedaimbaktimapp.model.Food
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.chip.Chip
import com.google.firebase.database.*


class HomeFragment : Fragment() {
    private lateinit var ReferenceFood: DatabaseReference
    private lateinit var rvHeroes: RecyclerView
    private var list = ArrayList<Food>()
    private lateinit var shimer: ShimmerFrameLayout
    private lateinit var chip1: Chip
    private lateinit var chip2: Chip
    private lateinit var selectedChipData: ArrayList<String>
//    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shimer = view.findViewById(R.id.shimer)
        shimer.startShimmer()
        rvHeroes = view.findViewById(R.id.rv_food)
        rvHeroes.setHasFixedSize(true)
        rvHeroes.layoutManager = GridLayoutManager(requireActivity(), 2)

        chip1 = view.findViewById(R.id.chip1)
        chip2 = view.findViewById(R.id.chip2)

        getItemData()

        chip1.setOnCheckedChangeListener { chip, isChecked ->
            rvHeroes.adapter = ListFoodAdapter(list)
            getItemData()
        }

        chip2.setOnCheckedChangeListener { chip, isChecked ->
            rvHeroes.adapter = ListFoodAdapter(list)
            filterPrice()
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
                shimer.stopShimmer()
                shimer.visibility = View.GONE
                rvHeroes.visibility = View.VISIBLE
                rvHeroes.adapter = ListFoodAdapter(list)
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
                        shimer.stopShimmer()
                        shimer.visibility = View.GONE
                        rvHeroes.visibility = View.VISIBLE
                        rvHeroes.adapter = ListFoodAdapter(list)
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