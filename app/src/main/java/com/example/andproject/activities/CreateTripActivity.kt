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
import com.example.andproject.firestore.TripRepository
import com.example.andproject.models.Account
import com.example.andproject.models.Trip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_trip.*

class CreateTripActivity : BaseActivity(), View.OnClickListener {

    private lateinit var currentAccount: Account
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_trip)

        val btnCreateTrip = findViewById<Button>(R.id.btn_create_trip_create_trip_activity)
        btnCreateTrip.setOnClickListener(this)

        toLoginActivity()

        showBottomNavigation(R.id.others)
    }

    override fun onResume() {
        super.onResume()
        toLoginActivity()
    }

    override fun onClick(view: View?) {
        if(view != null){
            when(view.id){
                R.id.btn_create_trip_create_trip_activity -> {
                    createTrip()
                }
            }
        }
    }

    private fun createTripValidation(): Boolean {
        return when {
            TextUtils.isEmpty(et_date_create_trip.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.enter_date), true)
                false
            }
            TextUtils.isEmpty(et_origin_create_trip_activity.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.enter_origin), true)
                false
            }
            TextUtils.isEmpty(et_destination_create_trip_activity.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.enter_destination), true)
                false
            }
            TextUtils.isEmpty(et_description_create_trip_activity.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.input_text_description), true)
                false
            }
            TextUtils.isEmpty(et_seats_create_trip_activity.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.available_seats_text), true)
                false
            }
            !cb_terms_and_condition_create_trip_activity.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_conditions), true)
                false
            }
            else -> {
                //showErrorSnackBar(resources.getString(R.string.account_creation_correct), false)
                true
            }
        }
    }

    private fun createTrip(){
        if(createTripValidation()) {
            showProgressDialog("Creating Trip, please wait...")

            db.collection(Constants.ACCOUNTS)
                .get()
                .addOnSuccessListener { document ->
                    for(doc in document.documents){
                        if(doc.id == AccountRepository().getCurrentAccountID()){
                            val account = doc.toObject(Account::class.java)!!
                            val trip = Trip(
                                "0",
                                et_date_create_trip.text.toString().trim { it <= ' ' },
                                et_time_create_trip_activity.text.toString().trim { it <= ' ' },
                                account.username,
                                doc.id,
                                et_origin_create_trip_activity.text.toString().trim { it <= ' ' },
                                et_destination_create_trip_activity.text.toString().trim { it <= ' ' },
                                et_seats_create_trip_activity.text.toString().trim { it <= ' ' },
                                et_description_create_trip_activity.text.toString().trim { it <= ' ' }
                            )
                            if(trip != null) {
                                TripRepository().createTrip(this, trip)
                            } else {
                                tripUploadFailure("Trip is null, trip upload Failure!")
                            }
                            return@addOnSuccessListener
                        }
                    }
                }
                .addOnFailureListener { e ->
                    tripUploadFailure(e.message.toString())
                    showErrorSnackBar("Error while creating Trip!", true)
                    Log.i("TAG", "Failed from database. Error: $e ")
                }
        }
    }

    fun tripUploadSuccess(){
        Toast.makeText(this, getString(R.string.trip_success), Toast.LENGTH_SHORT)
            .show()
        hideProgressDialog()
        startActivity(
            Intent(this, MainActivity::class.java)
        )
        finish()
    }

    fun tripUploadFailure(error: String){
        hideProgressDialog()
        showErrorSnackBar("Failed to Create trip! Because of: $error", true)
    }

    fun getAccountSuccess(account: Account){
        currentAccount = account
    }
}