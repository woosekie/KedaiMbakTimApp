package com.example.kedaimbaktimapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Food(
    var name: String,
    var price: String,
    var photo: Int
): Parcelable