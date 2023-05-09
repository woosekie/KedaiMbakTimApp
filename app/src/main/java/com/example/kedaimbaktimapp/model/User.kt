package com.example.kedaimbaktimapp.model

import com.google.firebase.database.Transaction

data class User(
    var name: String = "",
    var email: String = "",
    var number: String = "",
    var isAdmin: Boolean = false,
)


