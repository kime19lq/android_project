package com.example.andproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class TripBooking(
    var id: String = "",
    val date: String = "",
    val time: String = "",
    val username: String = "",
    val tripID: String = "",
    val accountID: String = "",
    val description: String = ""
) : Parcelable