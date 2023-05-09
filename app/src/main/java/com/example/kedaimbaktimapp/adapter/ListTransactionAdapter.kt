package com.example.kedaimbaktimapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kedaimbaktimapp.DetailHistoryActivity
import com.example.kedaimbaktimapp.R
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.*


class ListTransactionAdapter(
    private val listTransaction: ArrayList<Transaction>,
//    val context: Context
) :
    RecyclerView.Adapter<ListTransactionAdapter.ListViewHolder>() {

    private lateinit var foodDatabase: DatabaseReference
    private lateinit var transacDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_transaction, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val transaction: Transaction = listTransaction[position]

        foodDatabase = FirebaseDatabase.getInstance().getReference("Food")
        foodDatabase.child(transaction.foodId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val food = snapshot.getValue(Food::class.java)
                if (food != null) {
                    holder.tvFoodName.text = food.name
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
//        var numbertransacion = transaction.transactionId
        holder.tvTransactionId.text = transaction.transactionId

        holder.tvStatus.text = transaction.status

//        if(holder.tvStatus.equals("")){
//            holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.red));
//        }

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val priceIdr = format.format(transaction.price)

        holder.tvPrice.text = priceIdr
        holder.tvQuantity.text = transaction.quantity.toString() + " Item"
        holder.tvDateOrder.text = transaction.dateOrder

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailHistoryActivity::class.java)
            intent.putExtra("detail_transaction", transaction)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listTransaction.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvFoodName: TextView = itemView.findViewById(R.id.transaction_name)
        var tvPrice: TextView = itemView.findViewById(R.id.transaction_price)
        var tvQuantity: TextView = itemView.findViewById(R.id.quantity)
        var tvDateOrder: TextView = itemView.findViewById(R.id.date_order)
        var tvTransactionId: TextView = itemView.findViewById(R.id.transaction_id)
        var tvStatus: TextView = itemView.findViewById(R.id.status)

    }
}

