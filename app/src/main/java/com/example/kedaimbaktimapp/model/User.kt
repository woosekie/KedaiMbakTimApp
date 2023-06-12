package com.example.kedaimbaktimapp.model

import android.os.Parcelable
import com.google.firebase.database.Transaction
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var name: String = "",
    var email: String = "",
    var number: String = "",
    var photo: String="",
    var isAdmin: Boolean = false,
): Parcelable