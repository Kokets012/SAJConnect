package com.example.sajconnect

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ApplicationAdapter(
    private var applications: List<Application>,
    private val onCancelClicked: (Application) -> Unit
) : RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder>() {

    class ApplicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.applicationTitle)
        val message: TextView = itemView.findViewById(R.id.applicationMessage)
        val date: TextView = itemView.findViewById(R.id.applicationDate)
        val status: TextView = itemView.findViewById(R.id.applicationStatus) // ✅ Added this line
        val cancelButton: Button = itemView.findViewById(R.id.cancelApplicationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_application, parent, false)
        return ApplicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        val app = applications[position]
        holder.title.text = app.opportunityTitle
        holder.message.text = if (app.message.isNotEmpty()) app.message else "(No message)"
        holder.date.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            .format(Date(app.timestamp))

        //Status styling
        holder.status.text = "Status: ${app.status}"
        when (app.status.lowercase()) {
            "pending" -> holder.status.setTextColor(Color.parseColor("#6B7280")) // gray
            "approved" -> holder.status.setTextColor(Color.parseColor("#16A34A")) // green
            "rejected" -> holder.status.setTextColor(Color.parseColor("#DC2626")) // red
            else -> holder.status.setTextColor(Color.BLACK)
        }

        holder.cancelButton.setOnClickListener {
            onCancelClicked(app)
        }
    }

    override fun getItemCount() = applications.size

    fun updateData(newList: List<Application>) {
        applications = newList
        notifyDataSetChanged()
    }
}
