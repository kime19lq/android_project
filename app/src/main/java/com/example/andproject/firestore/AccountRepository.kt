package com.example.andproject.firestore

import android.app.Activity
import android.util.Log
import com.example.andproject.Constants
import com.example.andproject.activities.*
import com.example.andproject.models.Account
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

class AccountRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private lateinit var currentAccount: Account

    fun createAccount(activity: Activity, accountInfo: Account){
        db.collection(Constants.ACCOUNTS)
            .document(accountInfo.id)
            .set(accountInfo, SetOptions.merge())
            .addOnSuccessListener {
                when(activity) {
                    is CreateAccountActivity -> {
                        activity.createAccountSuccess()
                    }
                }
            }
            .addOnFailureListener{ e ->
                when(activity) {
                    is CreateAccountActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(Constants.CORIDE_TAG, "Error while registering the user.", e)
            }
    }

    fun getCurrentAccountID(): String {
        return auth.currentUser!!.uid
    }

    fun getAccountDetails(activity: Activity){

        db.collection(Constants.ACCOUNTS)
            .document(getCurrentAccountID())
            .get()
            .addOnSuccessListener { document ->
                val account = document.toObject(Account::class.java)!!
                when(activity){
                    is LoginActivity -> {
                        activity.accountLoggedInSuccess(account)
                    }
                    is AccountActivity -> {
                        activity.accountDetailsSuccess(account)
                    }
                    is CreateTripActivity -> {
                        activity.getAccountSuccess(account)
                    }
                    is TripBookingActivity -> {
                        activity.getAccountSuccess(account)
                    }
                }
            }
            .addOnFailureListener{ e->
                when(activity){
                    is LoginActivity -> {
                        activity.singingInFailure(e.message.toString())
                    }
                    is AccountActivity -> {
                        LoginActivity().singingInFailure(e.message.toString())
                    }
                    is CreateTripActivity -> {
                        LoginActivity().singingInFailure(e.message.toString())
                    }
                    is TripBookingActivity -> {
                        LoginActivity().singingInFailure(e.message.toString())
                    }
                }
            }
    }

}