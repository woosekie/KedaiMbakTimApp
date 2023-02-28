package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kedaimbaktimapp.Food
import com.example.kedaimbaktimapp.ListFoodAdapter
import com.example.kedaimbaktimapp.R


class HomeFragment : Fragment() {
    private lateinit var rvHeroes: RecyclerView
    private val list = ArrayList<Food>()
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvHeroes = view.findViewById(R.id.rv_food)
        rvHeroes.setHasFixedSize(true)
        list.addAll(listHeroes)
        showRecyclerList()

//        val inflater = requireActivity().layoutInflater
//        val dialogView: View = inflater.inflate(R.layout.fragment_home, null)
//        val searchView = dialogView.findViewById<View>(R.id.searchView) as SearchView
    }

    private val listHeroes: ArrayList<Food>
        get() {
            val dataName = resources.getStringArray(R.array.data_name)
            val dataDescription = resources.getStringArray(R.array.data_price)
            val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
            val listHero = ArrayList<Food>()
            for (i in dataName.indices) {
                val hero = Food(dataName[i],dataDescription[i], dataPhoto.getResourceId(i, -1))
                listHero.add(hero)
            }
            return listHero
        }

    private fun showRecyclerList() {
        rvHeroes.layoutManager = GridLayoutManager(requireActivity(), 2)
        val listFoodAdapter = ListFoodAdapter(list)
        rvHeroes.adapter = listFoodAdapter
    }

    companion object {
    }
}