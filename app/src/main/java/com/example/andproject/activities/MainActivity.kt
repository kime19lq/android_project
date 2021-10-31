package com.example.andproject.activities

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.toColorInt
import com.example.andproject.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.andproject.firestore.TripRepository
import com.example.andproject.adapters.TripsAdapter
import com.example.andproject.firestore.AccountRepository
import com.example.andproject.models.Trip

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class MainActivity : BaseActivity(), View.OnClickListener{

    private lateinit var tripList: ArrayList<Trip>
    private val auth = Firebase.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btn_login_main_activity)
        val btnCreateTrip = findViewById<Button>(R.id.btn_create_trip_main_activity)

        btnLogin.setOnClickListener(this)
        btnCreateTrip.setOnClickListener(this)

        if(auth.currentUser != null){
            btnLogin.isVisible = false
            showBottomNavigation(R.id.trips)
        } else{
            hideBottomNavigation()
            btnLogin.isVisible = true
        }

        getTrips()

    }

    override fun onClick(view: View?){
        if(view != null){
            when(view.id){
                R.id.btn_login_main_activity -> {
                    toLoginActivity()
                }
                R.id.btn_create_trip_main_activity -> {
                    startActivity(
                        Intent(this, CreateTripActivity::class.java)
                    )
                }
            }
        }
    }

    private fun getTrips(){
        showProgressDialog(resources.getString(R.string.please_wait))
        TripRepository().getAllTripsByActivity(this@MainActivity)
    }

    fun successLoadingTrips(tripsList: ArrayList<Trip>){
        this.tripList = tripsList
        setTripItemList()
    }

    private fun setTripItemList() {
        hideProgressDialog()
        if(tripList.size > 0){
            var i = 1
            for(trip in tripList){
                i++
            }
            var recyclerView = findViewById<RecyclerView>(R.id.rv_main_activity)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.setHasFixedSize(true)
            val myAdapter = TripsAdapter(this, tripList)
            recyclerView.adapter = myAdapter
        }
    }

    fun tripLoadingFailure(message: String){
        hideProgressDialog()
        showErrorSnackBar(message, true)
    }

}