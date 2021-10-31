package com.example.andproject.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.andproject.Constants
import com.example.andproject.R
import com.example.andproject.activities.TripActivity
import com.example.andproject.models.Trip
import kotlinx.android.synthetic.main.view_trip.view.*

open class TripsAdapter (private val context: Context, private val myList: ArrayList<Trip>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return TripViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.view_trip,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val aTrip = myList[position]
        if(holder is TripViewHolder){
            holder.itemView.tv_date_trip_view.text = "${aTrip.date}"
            holder.itemView.tv_username_trip_view.text = "${aTrip.username}"
            holder.itemView.tv_from_trip_view.text = "${aTrip.origin}"
            holder.itemView.tv_to_trip_view.text = "${aTrip.destination}"
            holder.itemView.tv_seats_trip_view.text = "${aTrip.numberOfSeats}"

            holder.itemView.setOnClickListener{
                val intent = Intent(context, TripActivity::class.java)
                intent.putExtra(Constants.EXTRA_TRIP_ID, aTrip.id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    class TripViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
