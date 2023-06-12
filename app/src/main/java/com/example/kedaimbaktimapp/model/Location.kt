package com.example.kedaimbaktimapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    var province: String="",
    var regency: String="",
    var subdistrict: String ="",
    var postalcode: String="",
    var area: String="",
    var address: String="",
    var lat: String="",
    var lng: String=""
): Parcelable