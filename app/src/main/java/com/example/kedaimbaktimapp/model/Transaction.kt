package com.example.kedaimbaktimapp.model

import java.util.*

data class Transaction(
    var userId: String ="",
    var foodId: String = "",
    var quantity: Long = 0,
    var price: Long= 0,
    var dateOrder: Date,
    var dateSend: Date,
    var location: String=""

)
