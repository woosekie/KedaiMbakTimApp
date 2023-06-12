package com.example.kedaimbaktimapp.adapter

import android.content.Intent
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
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class ListFoodAdapter(private var listFood: ArrayList<Food>): RecyclerView.Adapter<ListFoodAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val food: Food = listFood[position]
        holder.tvName.text = food.name
        holder.tvPrice.text = getCurrencyFormat(food.price)
        Glide.with(holder.imgPhoto).load(food.photo).into(holder.imgPhoto)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.imgPhoto.context, DetailFoodActivity::class.java)
            intent.putExtra("foodId", food.foodId)
            holder.imgPhoto.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listFood.size

    fun searchDataList(searchList: ArrayList<Food>){
        listFood = searchList
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvPrice: TextView = itemView.findViewById(R.id.tv_item_price)
    }

    private fun getCurrencyFormat(price: Int): String? {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val priceIdr = format.format(price)
        return priceIdr
    }
}