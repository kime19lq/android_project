package com.example.andproject.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.andproject.R
import com.example.andproject.adapters.MyBookingAdapter
import com.example.andproject.firestore.AccountRepository
import com.example.andproject.firestore.TripBookingRepository
import com.example.andproject.models.TripBooking
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_my_bookings.*


class MyBookingsActivity : BaseActivity(){

    private lateinit var myBookings: ArrayList<TripBooking>
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        toLoginActivity()
        getMyBookings()

        showBottomNavigation(R.id.my_bookings)
    }

    override fun onResume() {
        super.onResume()
        toLoginActivity()
        getMyBookings()
    }

    private fun getMyBookings(){
        TripBookingRepository().getMyBookingsByActivity(this@MyBookingsActivity)
    }

    fun successLoadingMyBookings(bookingList: ArrayList<TripBooking>){
        this.myBookings = bookingList
        getMyBookingsItemList()
    }
    private fun getMyBookingsItemList() {
        if(myBookings.size > 0){
            rv_my_bookings_activity.visibility = View.VISIBLE
            rv_my_bookings_activity.layoutManager = LinearLayoutManager(this)
            rv_my_bookings_activity.setHasFixedSize(true)
            val adapter = MyBookingAdapter(this, myBookings)
            rv_my_bookings_activity.adapter = adapter
        }
    }

    fun loadingMyBookingsFailure(message: String){
        showErrorSnackBar(message, true)
    }

    fun alertDialogDeleteBooking(id: String){
        TripBookingRepository().deleteBooking(id)
    }
}