package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kedaimbaktimapp.Food
import com.example.kedaimbaktimapp.ListFoodAdapter
import com.example.kedaimbaktimapp.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
    private lateinit var ReferenceFood: DatabaseReference
    private lateinit var rvHeroes: RecyclerView
    private var list = ArrayList<Food>()
    private lateinit var shimer: ShimmerFrameLayout
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
        getItemData()

//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                filterList(newText.toString())
//                return false
//            }
//
//        })
    }

    private fun getItemData() {
        ReferenceFood = FirebaseDatabase.getInstance().getReference("Food")
        ReferenceFood.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for(itemSnapshot in snapshot.children){
                    val dataClass = itemSnapshot.getValue(Food::class.java)
                    if(dataClass != null){
                        list.add(dataClass)
                    }
                }
                shimer.stopShimmer()
                shimer.visibility = View.GONE
                rvHeroes.visibility = View.VISIBLE
                rvHeroes.adapter = ListFoodAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

//    private fun filterList(query: String){
//        if(query!=null){
//            ReferenceFood = FirebaseDatabase.getInstance().getReference("Food")
//            val order = ReferenceFood.child("Food").orderByChild("name").equalTo(query)
//
//        } else {
//
//        }
//    }


    companion object {
    }
}