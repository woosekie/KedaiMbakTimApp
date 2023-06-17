package com.example.kedaimbaktimapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.databinding.ActivityDetailOrderBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Transaction
import com.example.kedaimbaktimapp.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.*


class DetailOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailOrderBinding
    var selectedItemIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction = intent.getParcelableExtra<Transaction>("detail_order") as Transaction

        getFoodData(transaction.foodId)
        getTransactionData(transaction.transactionId)
        getUserData(transaction.userId)

        binding.deleteOrder.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus pesanan")
                .setMessage("Apakah kamu yakin ingin menghapus pesanan ini ?")
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    deleteOrder(transaction.transactionId)
                    Toast.makeText(
                        applicationContext,
                        "Berhasil menghapus pesanan",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                .setNegativeButton(getString(R.string.no)) { _, _ -> }
                .show()
        }
        binding.changeStatus.setOnClickListener {
            showSelectorDialog(transaction.transactionId, transaction.userId)
        }
    }

    private fun getUserData(userId: String) {
        FirebaseDatabase.getInstance().getReference("Registered Users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        binding.userName.text = user.name
                        binding.userNumber.text = user.number
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun getFoodData(foodId: String) {
        FirebaseDatabase.getInstance().getReference("Food").child(foodId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val food = snapshot.getValue(Food::class.java)
                    if (food != null) {
                        binding.foodName.text = food.name
                        binding.foodPrice.text = formatRp(food.price)
                        Glide.with(applicationContext)
                            .load(food.photo)
                            .into(binding.foodImg)
                    } else {
                        Toast.makeText(baseContext, "Data makanan tidak tersedia", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun getTransactionData(transactionId: String) {
        FirebaseDatabase.getInstance().getReference("Transaction").child(transactionId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transaction = snapshot.getValue(Transaction::class.java)
                    if (transaction != null) {
                        binding.status.text = transaction.status
                        binding.quantity.text = transaction.quantity.toString() + " Item"
                        binding.transactionId.text = transaction.transactionId
                        binding.orderDate.text = transaction.dateOrder
                        binding.SendingDate.text = transaction.dateSend
                        binding.totalPay.text = formatRp(transaction.price)
                        binding.note.text = transaction.orderNote
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun showSelectorDialog(transactionId: String, userId: String) {
        val status = arrayOf("Belum diproses", "Sedang diproses", "Siap diambil","Dalam pengiriman", "Selesai")
        var selectedStatus = status[selectedItemIndex]

        MaterialAlertDialogBuilder(this).setTitle("Ganti Status Pesanan")
            .setSingleChoiceItems(status, selectedItemIndex) { _, which ->
                selectedItemIndex = which
                selectedStatus = status[which]
            }.setPositiveButton("Pilih") { _, _ ->
                if (selectedStatus == "Belum diproses") {
                    changeStatus(transactionId, "Belum diproses", userId)
                } else if (selectedStatus == "Sedang diproses") {
                    changeStatus(transactionId, "Sedang diproses", userId)
                } else if (selectedStatus == "Siap diambil") {
                    changeStatus(transactionId, "Siap diambil", userId)
                } else if (selectedStatus == "Dalam pengiriman") {
                    changeStatus(transactionId, "Dalam pengiriman",userId)
                } else if (selectedStatus == "Selesai") {
                    changeStatus(transactionId, "Selesai", userId)
                }
            }.setNeutralButton("Batal") { _, _ ->
            }.show()
    }

    private fun formatRp(price: Int): String? {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val priceIdr = format.format(price)
        return priceIdr
    }

    private fun changeStatus(transactionId: String, status: String, userId: String) {
        FirebaseDatabase.getInstance().getReference("Transaction").child(transactionId)
            .child("status").setValue(status)

        FirebaseDatabase.getInstance().getReference("Registered Users").child(userId)
            .child("transaction").child(transactionId).child("status").setValue(status)
    }

    private fun deleteOrder(transactionId: String) {
        FirebaseDatabase.getInstance().getReference("Transaction").child(transactionId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (appleSnapshot in dataSnapshot.children) {
                        appleSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }


}