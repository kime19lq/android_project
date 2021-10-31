package com.example.andproject.firestore

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.andproject.Constants
import com.example.andproject.activities.CreateTripActivity
import com.example.andproject.activities.LoginActivity
import com.example.andproject.activities.MainActivity
import com.example.andproject.activities.TripActivity
import com.example.andproject.models.Trip
import com.example.andproject.models.TripBooking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

class TripRepository : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()


    fun createTrip(activity: Activity, trip: Trip){

        val ref = db.collection(Constants.TRIPS).document()
        trip.id = ref.id
        db.collection(Constants.TRIPS)
            .document(trip.id)
            .set(trip, SetOptions.merge())
            .addOnSuccessListener {
                when(activity){
                    is CreateTripActivity-> {
                        activity.tripUploadSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity){
                    is CreateTripActivity-> {
                        activity.tripUploadFailure(e.message.toString())
                    }
                }
                Log.e(activity.javaClass.simpleName,"Error while registering the user.", e)
            }
    }

    fun getAllTripsByActivity(activity: Activity){
        db.collection(Constants.TRIPS)
            .get()
            .addOnSuccessListener { document ->
                val tripList: ArrayList<Trip> = ArrayList()
                for(doc in document.documents){
                    val trip = doc.toObject(Trip::class.java)!!
                    tripList.add(trip)
                }

                when(activity){
                    is MainActivity ->{
                        activity.successLoadingTrips(tripList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity){
                    is MainActivity ->{
                        activity.tripLoadingFailure(e.message.toString())
                    }
                }}
    }

    fun getTripDetails(activity: Activity, tripID: String){

        db.collection(Constants.TRIPS)
            .document(tripID)
            .get()
            .addOnSuccessListener { document ->
                var trip = document.toObject(Trip::class.java)!!
                if(trip != null) {
                    when (activity) {
                        is TripActivity -> {
                            activity.loadTripSuccess(trip)
                            activity.getCurrentTripID(trip)
                        }
                    }
                } else {
                    when (activity) {
                        is TripActivity -> {
                            activity.loadTripFailure("Trip does not exist!")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is TripActivity -> {
                        activity.loadTripFailure(e.message.toString())
                    }
                }
                Log.e(activity.javaClass.simpleName,"Error while getting user details.", e)
            }
    }

}