package com.example.kedaimbaktimapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kedaimbaktimapp.DetailHistoryActivity
import com.example.kedaimbaktimapp.DetailOrderActivity
import com.example.kedaimbaktimapp.R
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Transaction
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.*

class ListOrderAdapter(
    private val listTransaction: ArrayList<Transaction>,
//    val context: Context
) :
    RecyclerView.Adapter<ListOrderAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_order, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val transaction: Transaction = listTransaction[position]

        FirebaseDatabase.getInstance().getReference("Registered Users").child(transaction.userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        holder.tvCustName.text = user.name
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        FirebaseDatabase.getInstance().getReference("Food").child(transaction.foodId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val food = snapshot.getValue(Food::class.java)
                    if (food != null) {
                        holder.tvFoodName.text = food.name
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        holder.tvOrderDeadline.text = transaction.dateSend
        holder.tvQuantity.text = transaction.quantity.toString() + " Item"
        holder.tvTotalPrice.text = formatRp(transaction.price)
        holder.tvStatus.text = transaction.status
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailOrderActivity::class.java)
            intent.putExtra("detail_order", transaction)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listTransaction.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCustName: TextView = itemView.findViewById(R.id.orderName)
        var tvOrderDeadline: TextView = itemView.findViewById(R.id.orderDate)
        var tvFoodName: TextView = itemView.findViewById(R.id.foodName)
        var tvQuantity: TextView = itemView.findViewById(R.id.quantity)
        var tvTotalPrice: TextView = itemView.findViewById(R.id.transaction_price)
        var tvStatus: TextView = itemView.findViewById(R.id.status)

    }

    private fun formatRp(price: Int): String? {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val priceIdr = format.format(price)
        return priceIdr
    }
}

