package com.example.kedaimbaktimapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ListenOrderService : Service() {

    private lateinit var db: FirebaseDatabase
    private lateinit var orders: DatabaseReference



    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}