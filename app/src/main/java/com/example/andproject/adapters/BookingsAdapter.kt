package com.example.andproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.andproject.R
import com.example.andproject.models.TripBooking
import kotlinx.android.synthetic.main.recycler_view_booking.view.*

open class BookingsAdapter (private val context: Context, private val bookingList: ArrayList<TripBooking>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return BookingViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.recycler_view_booking,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tripBooking = bookingList[position]

        if(holder is BookingViewHolder){
            holder.itemView.tv_rv_username_booking.text = tripBooking.date
            holder.itemView.tv_rv_date_booking.text = tripBooking.username

        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
