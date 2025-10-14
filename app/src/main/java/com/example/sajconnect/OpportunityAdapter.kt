package com.example.sajconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OpportunityAdapter(
    private var items: List<Opportunity>,
    private val onApplyClick: (Opportunity) -> Unit
) : RecyclerView.Adapter<OpportunityAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle)
        val category: TextView = view.findViewById(R.id.itemCategory)
        val description: TextView = view.findViewById(R.id.itemDescription)
        val applyButton: Button = view.findViewById(R.id.applyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_opportunity, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.category.text = item.category
        holder.description.text = item.description

        holder.applyButton.setOnClickListener {
            onApplyClick(item)
        }
    }

    fun updateData(newList: List<Opportunity>) {
        items = newList
        notifyDataSetChanged()
    }
}
