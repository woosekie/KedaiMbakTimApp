package com.example.kedaimbaktimapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Transaction(
    var transactionId : String="",
    var userId: String ="",
    var foodId: String = "",
    var quantity: Int = 0,
    var price: Int = 0,
    var dateOrder: String = "",
    var dateSend: String = "",
    var location: String="",
    var status: String=""

): Parcelable
