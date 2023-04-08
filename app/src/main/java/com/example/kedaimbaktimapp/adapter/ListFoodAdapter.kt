package com.example.kedaimbaktimapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.DetailFoodActivity
import com.example.kedaimbaktimapp.R
import com.example.kedaimbaktimapp.model.Food


class ListFoodAdapter(private val listFood: ArrayList<Food>) : RecyclerView.Adapter<ListFoodAdapter.ListViewHolder>() {

    private lateinit var dataList: ArrayList<Food>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val food: Food = listFood[position]
        holder.tvName.text = food.name
        holder.tvPrice.text = "Rp. " + food.price.toString()
        Glide.with(holder.imgPhoto).load(food.photo).into(holder.imgPhoto);

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.imgPhoto.context, DetailFoodActivity::class.java)
            intent.putExtra("detail_food", food)
            holder.imgPhoto.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listFood.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvPrice: TextView = itemView.findViewById(R.id.tv_item_price)
    }

    fun searchDataList(searchList: ArrayList<Food>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}