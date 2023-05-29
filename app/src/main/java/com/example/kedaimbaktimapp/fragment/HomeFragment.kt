package com.example.kedaimbaktimapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kedaimbaktimapp.adapter.ListFoodAdapter
import com.example.kedaimbaktimapp.databinding.FragmentHomeBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var list = ArrayList<Food>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFood.setHasFixedSize(true)
        binding.rvFood.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvFood.setNestedScrollingEnabled(false)

        getItemData()
        getWelcomeText()

        binding.chip1.setOnClickListener {
            resetSelectedColorType()
            getItemData()
            binding.chip1.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    com.example.kedaimbaktimapp.R.color.green_button
                )
            )
        }
        binding.chip2.setOnClickListener {
            resetSelectedColorType()
            filterFood("nasi kotak")
            binding.chip2.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    com.example.kedaimbaktimapp.R.color.green_button
                )
            )
        }
        binding.chip3.setOnClickListener {
            resetSelectedColorType()
            filterFood("tumini")
            binding.chip3.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    com.example.kedaimbaktimapp.R.color.green_button
                )
            )
        }
        binding.chip4.setOnClickListener {
            resetSelectedColorType()
            filterFood("bento")
            binding.chip4.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(), com.example.kedaimbaktimapp.R.color.green_button
                )
            )
        }
    }

    private fun resetSelectedColorType() {
        binding.chip1.setBackgroundTintList(
            ContextCompat.getColorStateList(
                requireContext(),
                com.example.kedaimbaktimapp.R.color.gray_back
            )
        )
        binding.chip2.setBackgroundTintList(
            ContextCompat.getColorStateList(
                requireContext(),
                com.example.kedaimbaktimapp.R.color.gray_back
            )
        )
        binding.chip3.setBackgroundTintList(
            ContextCompat.getColorStateList(
                requireContext(),
                com.example.kedaimbaktimapp.R.color.gray_back
            )
        )
        binding.chip4.setBackgroundTintList(
            ContextCompat.getColorStateList(
                requireContext(),
                com.example.kedaimbaktimapp.R.color.gray_back
            )
        )
    }

    private fun getWelcomeText() {
        binding.dateText.text = getCurrentTime()
        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        val userID = firebaseUser?.uid
        if (userID != null) {
            FirebaseDatabase.getInstance().getReference("Registered Users").child(userID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            val name = user.name.substringBefore(" ")
                            val c = Calendar.getInstance()
                            val timeOfDay = c[Calendar.HOUR_OF_DAY]
                            if (timeOfDay >= 0 && timeOfDay < 12) {
                                binding.timeText.text = getString(com.example.kedaimbaktimapp.R.string.morning) + name
                            } else if (timeOfDay >= 12 && timeOfDay < 16) {
                                binding.timeText.text = getString(com.example.kedaimbaktimapp.R.string.day)+ name
                            } else if (timeOfDay >= 16 && timeOfDay < 18) {
                                binding.timeText.text = getString(com.example.kedaimbaktimapp.R.string.evening)+ name
                            } else if (timeOfDay >= 18 && timeOfDay < 24) {
                                binding.timeText.text = getString(com.example.kedaimbaktimapp.R.string.night)+ name
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }

    private fun getItemData() {
        showLoading(true)
        FirebaseDatabase.getInstance().getReference("Food")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (itemSnapshot in snapshot.children) {
                        val dataClass = itemSnapshot.getValue(Food::class.java)
                        if (dataClass != null) {
                            list.add(dataClass)
                        }
                    }
                    showLoading(false)
                    binding.rvFood.adapter = ListFoodAdapter(list)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun filterFood(category: String) {
        showLoading(true)
        FirebaseDatabase.getInstance().getReference("Food").orderByChild("type").equalTo(category)
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
                        showLoading(false)
                        binding.rvFood.adapter = ListFoodAdapter(list)
                    } else {
                        Toast.makeText(getActivity(),"Gagal untuk menampilkan makanan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.shimer.startShimmer()
            binding.shimer.visibility = View.VISIBLE
            binding.rvFood.visibility = View.GONE
        } else {
            binding.shimer.stopShimmer()
            binding.shimer.visibility = View.GONE
            binding.rvFood.visibility = View.VISIBLE
        }
    }

    private fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy")
        val curentDate = Date()
        val current = formatter.format(curentDate)
        return current
    }

    companion object {
    }
}