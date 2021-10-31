package com.example.andproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
class Account (
    val id: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val username: String = "",
    val email: String = "",
    val mobile: Long = 0,
    val profileCompleted: Int = 0
) : Parcelable
