package com.example.kedaimbaktimapp.adapter

import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.DetailCustomerActivity
import com.example.kedaimbaktimapp.DetailFoodActivity
import com.example.kedaimbaktimapp.R
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.User
import kotlin.collections.ArrayList

class ListCustomerAdapter(private var listCustomer: ArrayList<User>) : RecyclerView.Adapter<ListCustomerAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_customer, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user: User = listCustomer[position]
        holder.tvName.text = user.name
        holder.tvEmail.text = user.email
        holder.tvNumber.text = user.number
        if (user.photo.isNotEmpty()){
            Glide.with(holder.tvProfilePict).load(user.photo).into(holder.tvProfilePict)
        } else {
            Glide.with(holder.tvProfilePict).load(R.drawable.ic_profile_user_svgrepo_com__1_).into(holder.tvProfilePict)
        }
        holder.tvDetailCust.setOnClickListener {
            val intent = Intent(holder.tvDetailCust.context, DetailCustomerActivity::class.java)
            intent.putExtra("detail_customer", user)
            holder.tvDetailCust.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listCustomer.size

    fun searchDataList(searchList: ArrayList<User>){
        listCustomer = searchList
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.customerName)
        var tvEmail: TextView = itemView.findViewById(R.id.customerEmail)
        var tvNumber: TextView = itemView.findViewById(R.id.customerNumber)
        var tvDetailCust: LinearLayout = itemView.findViewById(R.id.detailCustomer)
        var tvProfilePict: ImageView = itemView.findViewById(R.id.profilePic)

    }
    
}