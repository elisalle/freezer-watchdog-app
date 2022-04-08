package com.example.freezerwatchdog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]


        // sets the freezer name text to the textview from our itemHolder class
        holder.freezerName.text = ItemsViewModel.freezerName

        // sets the freezer status text to the freezerStatus from our itemHolder class
        holder.freezerStatus.text = ItemsViewModel.freezerStatus

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val freezerName: TextView = itemView.findViewById(R.id.freezerName)
        val freezerStatus: TextView = itemView.findViewById(R.id.freezerStatus)
    }
}
