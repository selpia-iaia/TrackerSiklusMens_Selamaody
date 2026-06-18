package com.example.trackersiklusmenstruasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private val items: List<AnnouncementModel>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        val dbHelper = DatabaseHelper.getInstance(context)
        val userName = dbHelper.getUserProfile()?.name ?: "User"

        // Mengganti {name} dengan nama user yang terdaftar di aplikasi
        val formattedTitle = item.title.replace("{name}", userName, ignoreCase = true)
        val formattedMessage = item.message.replace("{name}", userName, ignoreCase = true)

        holder.tvTitle.text = formattedTitle
        holder.tvMessage.text = formattedMessage
        holder.tvDate.text = item.created_at
    }

    override fun getItemCount() = items.size
}
