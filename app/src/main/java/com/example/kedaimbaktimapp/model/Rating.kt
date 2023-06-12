package com.example.kedaimbaktimapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rating(
    var userId: String="",
    var foodId: String="",
    var rateValue: String ="",
    var comment: String="",
    var date: String=""
): Parcelable