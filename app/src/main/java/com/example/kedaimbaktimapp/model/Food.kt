package com.example.kedaimbaktimapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Food(
    var foodId: String="",
    var name: String="",
    var price: Long = 0,
    var photo: String="",
    var description: String="",
    var type: String=""
    ): Parcelable