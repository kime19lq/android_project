package com.example.andproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Trip(
    var id: String = "",
    val date: String = "",
    val time: String = "",
    val username: String = "",
    val userID: String = "",
    val origin: String = "",
    val destination: String = "",
    val numberOfSeats: String = "",
    val description: String = ""
) : Parcelable
