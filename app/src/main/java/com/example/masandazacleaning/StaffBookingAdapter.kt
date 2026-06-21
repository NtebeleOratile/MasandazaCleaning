package com.example.masandazacleaning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.masandazacleaning.database.Booking

class StaffBookingAdapter(
    private var bookings: List<Booking>,
    private val onItemClick: (Booking) -> Unit
) : RecyclerView.Adapter<StaffBookingAdapter.StaffViewHolder>() {

    class StaffViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvServiceType: TextView = view.findViewById(R.id.tvServiceType)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvServiceType.text = "${booking.serviceType} (User ID: ${booking.userId})"
        holder.tvDate.text = booking.date
        holder.tvStatus.text = booking.status
        holder.tvPrice.text = "R ${booking.price}"
        
        holder.itemView.setOnClickListener { onItemClick(booking) }
    }

    override fun getItemCount() = bookings.size

    fun updateBookings(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
