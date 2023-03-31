package com.example.kedaimbaktimapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kedaimbaktimapp.DetailHistoryActivity
import com.example.kedaimbaktimapp.ListFoodAdapter
import com.example.kedaimbaktimapp.R
import com.example.kedaimbaktimapp.UpdateProfileActivity
import com.example.kedaimbaktimapp.databinding.FragmentHistoryBinding
import com.example.kedaimbaktimapp.databinding.FragmentHomeBinding

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.itemFood.setOnClickListener {
            val intent = Intent(activity,  DetailHistoryActivity::class.java)
            startActivity(intent)
        }

    }
}