package com.example.masandazacleaning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.masandazacleaning.database.Booking

class BookingAdapter(
    private var bookings: List<Booking>,
    private val onItemClick: ((Booking) -> Unit)? = null
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvServiceType: TextView = view.findViewById(R.id.tvServiceType)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvServiceType.text = booking.serviceType
        holder.tvDate.text = "Date: ${booking.date}"
        holder.tvStatus.text = booking.status

        // Color code the status
        val statusColor = when (booking.status) {
            "Completed" -> holder.itemView.context.getColor(R.color.green_success)
            "Pending" -> holder.itemView.context.getColor(R.color.yellow_dark)
            "Cancelled" -> holder.itemView.context.getColor(R.color.red_accent)
            "In Progress" -> holder.itemView.context.getColor(R.color.blue_primary)
            else -> holder.itemView.context.getColor(R.color.gray_text)
        }
        holder.tvStatus.setTextColor(statusColor)

        holder.tvPrice.text = "R ${String.format("%.2f", booking.price)}"

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(booking)
        }
    }

    override fun getItemCount() = bookings.size

    fun updateBookings(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}