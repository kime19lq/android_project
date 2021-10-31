package com.example.andproject.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.andproject.Constants
import com.example.andproject.R
import com.example.andproject.activities.TripBookingActivity
import com.example.andproject.models.TripBooking
import kotlinx.android.synthetic.main.recycler_view_mybookings.view.*
import android.content.DialogInterface
import com.example.andproject.activities.MyBookingsActivity


open class MyBookingAdapter (private val context: Context, private val myList: ArrayList<TripBooking>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.recycler_view_mybookings,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val booking = myList[position]
        if(holder is MyViewHolder){
            holder.itemView.tv_username_my_booking_rv.text = "${booking.username}"
            holder.itemView.tv_date_my_bookings_rv.text = "${booking.date}"
            holder.itemView.tv_time_my_bookings_rv.text = "${booking.time}"
            holder.itemView.tv_description_my_bookings_rv.text = "${booking.description}"


            holder.itemView.setOnClickListener { v ->
            AlertDialog.Builder(context)
                    .setTitle("Delete Booking")
                    .setMessage("Are you sure you want to delete this booking?") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes) { dialog, which ->

                        MyBookingsActivity().alertDialogDeleteBooking(booking.id)
                        v.context.startActivity(Intent(context, MyBookingsActivity::class.java))

                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        }
    }



    override fun getItemCount(): Int {
        return myList.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}


