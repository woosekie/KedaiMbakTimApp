package com.example.kedaimbaktimapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.DetailFoodActivity
import com.example.kedaimbaktimapp.R
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Rating
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ListRateAdapter (private var listRate: ArrayList<Rating>): RecyclerView.Adapter<ListRateAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_rate, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val rate: Rating = listRate[position]

        FirebaseDatabase.getInstance().getReference("Registered Users").child(rate.userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        holder.tvUserName.text = user.name
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        holder.tvComment.text = rate.comment
        holder.tvDate.text = rate.date
        holder.tvStar.rating = rate.rateValue.toFloat()

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = listRate.size
    
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserName: TextView = itemView.findViewById(R.id.user)
        var tvComment: TextView = itemView.findViewById(R.id.comment)
        var tvDate: TextView = itemView.findViewById(R.id.date)
        var tvStar: RatingBar = itemView.findViewById(R.id.rateStar)
    }

}