package com.example.andproject.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.andproject.Constants
import com.example.andproject.R
import com.example.andproject.firestore.AccountRepository
import com.example.andproject.firestore.TripBookingRepository
import com.example.andproject.models.Account
import com.example.andproject.models.TripBooking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_trip_booking.*
import kotlinx.android.synthetic.main.view_trip.*
import java.text.SimpleDateFormat
import java.util.*

class TripBookingActivity : BaseActivity(), View.OnClickListener {

    private lateinit var currentAccount: Account
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_booking)

        toLoginActivity()

        val bundle = intent.extras
        bundle?.getString(Constants.TRIP_ID)

        val btnBookTrip = findViewById<Button>(R.id.btn_create_booking_trip_booking_activity)
        btnBookTrip.setOnClickListener(this)

        showBottomNavigation(R.id.others)
    }

    override fun onResume() {
        super.onResume()
        toLoginActivity()
    }

    override fun onClick(view: View?) {
        if(view != null){
            when(view.id){
                R.id.btn_create_booking_trip_booking_activity -> {
                    createBooking()
                }
            }
        }
    }

    fun getAccountSuccess(account: Account){
        currentAccount = account
    }

    private fun tripBookingValidation(): Boolean {
        return when {
            TextUtils.isEmpty(et_booking_description_trip_booking_activity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.booking_description), true)
                Toast.makeText(
                    baseContext, getString(R.string.fill_in_booking),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }else -> {
                true
            }
        }
    }

    private fun createBooking() {
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val tripId = intent.extras!!.getString(Constants.TRIP_ID)

        if(tripBookingValidation()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            db.collection(Constants.ACCOUNTS)
                .get()
                .addOnSuccessListener { document ->
                    for(doc in document.documents){
                        if(doc.id == AccountRepository().getCurrentAccountID()){

                            val account = doc.toObject(Account::class.java)!!
                            val booking = tripId?.let {
                                TripBooking(
                                    "0",
                                    currentDate,
                                    currentTime,
                                    account.username,
                                    it,
                                    account.id,
                                    et_booking_description_trip_booking_activity.text.toString()
                                        .trim { it <= ' ' }
                                )
                            }
                            if(booking != null) {
                                TripBookingRepository().createBooking(this, booking)
                            } else {
                                tripBookingFailure("Booking is null!")
                                showErrorSnackBar("Failed in creating booking!", true)
                            }
                            return@addOnSuccessListener
                        }
                    }
                }
                .addOnFailureListener { e ->
                    tripBookingFailure(e.message.toString())
                    Log.i("TAG", "Failed to create booking, because of: $e")
                }
        }
    }

    fun tripBookingSuccess(tripID: String){

        hideProgressDialog()

        Toast.makeText(this, getString(R.string.booking_success), Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this, TripActivity::class.java)
        intent.putExtra(Constants.EXTRA_TRIP_ID, tripID)
        this.startActivity(intent)
    }

    fun tripBookingFailure(message: String){

        hideProgressDialog()
        showErrorSnackBar("Failed to create booking, because of: $message", true)

        finish()
    }

}