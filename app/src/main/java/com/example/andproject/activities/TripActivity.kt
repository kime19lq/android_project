package com.example.andproject.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.andproject.Constants
import com.example.andproject.R
import com.example.andproject.adapters.BookingsAdapter
import com.example.andproject.firestore.TripBookingRepository
import com.example.andproject.firestore.TripRepository
import com.example.andproject.models.Trip
import com.example.andproject.models.TripBooking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_trip.*
import kotlinx.android.synthetic.main.view_trip_details.*

class TripActivity : BaseActivity(), View.OnClickListener {

    private lateinit var currentTrip: Trip
    private var currentTripID: String = ""
    private lateinit var bookingArray: ArrayList<TripBooking>
    private lateinit var currentBooking: TripBooking
    private var currentBookingID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)

        toLoginActivity()

        val bundle = intent.extras
        bundle?.getString(Constants.EXTRA_TRIP_ID)

        getTripDetails()
        getBookingDetails()

        val btnBookTrip = findViewById<Button>(R.id.btn_create_booking_trip_activity)
        btnBookTrip.setOnClickListener(this)

        showBottomNavigation(R.id.trips)
    }

    override fun onClick(view: View?) {
        if(view != null){
            when(view.id) {
                R.id.btn_create_booking_trip_activity -> {
                    val intent = Intent(this, TripBookingActivity::class.java)
                    intent.putExtra(Constants.TRIP_ID, currentTripID)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        toLoginActivity()
        getBookings()
    }

    private fun getBookings(){
        if(intent.extras != null) {
            currentTripID = intent.extras!!.getString(Constants.EXTRA_TRIP_ID).toString()
            TripBookingRepository().getAllBookingsByActivity(this@TripActivity, currentTripID)
        }
    }

    private fun getTripDetails(){
        if(intent.extras != null){
            currentTripID = intent.extras!!.getString(Constants.EXTRA_TRIP_ID).toString()
            TripRepository().getTripDetails(this, currentTripID)
        }
    }

    private fun getBookingDetails(){
        TripBookingRepository().getBookingDetails(this, currentBookingID)
    }

    fun getCurrentTripID(trip: Trip): String{
        currentTrip = trip
        return trip.id
    }

    fun getCurrentBookingID(booking: TripBooking): String{
        currentBooking = booking
        return booking.id
    }

    fun loadTripSuccess(trip: Trip){
        currentTrip = trip
        tv_date_trip_details_trip_activity.text = "${trip.date}"
        tv_time_trip_details_trip_activity.text = "${trip.time}"
        tv_from_trip_details_trip_activity.text = "${trip.origin}"
        tv_to_trip_details_trip_activity.text = "${trip.destination}"
        tv_username_trip_details_trip_activity.text = "${trip.username}"
        tv_seats_trip_details_trip_activity.text = "${trip.numberOfSeats}"
        tv_description_trip_activity.text = "${trip.description}"
    }

    fun loadTripFailure(message: String){
        showErrorSnackBar(message, true)
    }

    fun loadBookingSuccess(booking: TripBooking){
        currentBooking = booking
        tv_username_trip_details_view.text = "${booking.username}"
        tv_date_trip_details_view.text = "${booking.date}"
    }

    fun loadBookingFailure(message: String){
        showErrorSnackBar(message, true)
    }

    fun successLoadingBookings(bookingList: ArrayList<TripBooking>){
        this.bookingArray = bookingList
        getBookingItemList()
    }

    private fun getBookingItemList() {
        if(bookingArray.size > 0){
            rv_trip_bookings_trip_activity.visibility = View.VISIBLE
            rv_trip_bookings_trip_activity.layoutManager = LinearLayoutManager(this)
            rv_trip_bookings_trip_activity.setHasFixedSize(true)
            val adapter = BookingsAdapter(this, bookingArray)
            rv_trip_bookings_trip_activity.adapter = adapter
        }
    }
}