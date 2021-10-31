package com.example.andproject.firestore

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.example.andproject.Constants
import com.example.andproject.activities.MainActivity
import com.example.andproject.activities.MyBookingsActivity
import com.example.andproject.activities.TripActivity
import com.example.andproject.activities.TripBookingActivity
import com.example.andproject.models.TripBooking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase


class TripBookingRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    fun createBooking(activity: TripBookingActivity, booking: TripBooking){

        val ref = db.collection(Constants.BOOKINGS).document()
        booking.id = ref.id
        db.collection(Constants.BOOKINGS)
            .document(booking.id)
            .set(booking, SetOptions.merge())
            .addOnSuccessListener {
                activity.tripBookingSuccess(booking.tripID)
            }
            .addOnFailureListener { e ->
                activity.tripBookingFailure(e.message.toString())
                Log.e(activity.javaClass.simpleName,"Error while registering the user.", e)
            }
    }

    fun getAllBookingsByActivity(activity: Activity, tripID: String){

        db.collection(Constants.BOOKINGS).whereEqualTo(Constants.BOOKING_TRIP_ID, tripID)
            .get()
            .addOnSuccessListener { document ->
                val bookingList: ArrayList<TripBooking> = ArrayList()
                for(doc in document.documents){
                    val booking = doc.toObject(TripBooking::class.java)!!
                    if (booking != null) {
                        bookingList.add(booking)
                    }
                }
                when(activity){
                     is TripActivity ->{
                         activity.successLoadingBookings(bookingList)
                     }
                    is MyBookingsActivity ->{
                        activity.successLoadingMyBookings(bookingList)
                    }
                }
            }
            .addOnFailureListener {e->
                when(activity){
                    is TripActivity ->{
                        activity.loadBookingFailure(e.message.toString())
                    }
                    is MyBookingsActivity ->{
                        activity.loadingMyBookingsFailure(e.message.toString())
                }
            }
            }
    }

    fun getMyBookingsByActivity(activity: Activity){

         db.collection(Constants.BOOKINGS).whereEqualTo(Constants.ACCOUNT_ID, AccountRepository().getCurrentAccountID())
             .get()
             .addOnSuccessListener { document ->
                 val bookingList: ArrayList<TripBooking> = ArrayList()
                 for(doc in document.documents){
                     val booking = doc.toObject(TripBooking::class.java)!!
                     if (booking != null) {
                         bookingList.add(booking)
                     }
                 }
                 when(activity){
                     is MyBookingsActivity ->{
                         activity.successLoadingMyBookings(bookingList)
                     }
                 }
             }
             .addOnFailureListener { e->
                 when(activity){
                     is MyBookingsActivity ->{
                         activity.loadingMyBookingsFailure(e.message.toString())
                     }
                 }
             }
    }

    fun getBookingDetails(activity: Activity, bookingID: String){
        db.collection(Constants.BOOKINGS)
            .get()
            .addOnSuccessListener { document ->
                for(doc in document.documents) {
                    if (doc.id == bookingID) {

                        val booking = doc.toObject(TripBooking::class.java)!!
                        when (activity) {
                              is TripActivity -> {
                                  activity.loadBookingSuccess(booking)
                                  activity.getCurrentBookingID(booking)
                              }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is TripActivity -> {
                        activity.loadBookingFailure(e.message.toString())
                    }
                }
                Log.e(activity.javaClass.simpleName,"Error while getting user details.", e)
            }
    }

    fun deleteBooking(bookingID: String){
        Log.d(Constants.CORIDE_TAG, "melessssssssssssssssssssssssssss")
        db.collection(Constants.BOOKINGS)
            .document(bookingID)
            .delete()
            .addOnSuccessListener {
                Log.d(Constants.CORIDE_TAG, "success Melesssssssssssssssss!")
            }
            .addOnFailureListener { e ->
                Log.w(Constants.CORIDE_TAG, "Error deleting document", e)
            }
    }
}