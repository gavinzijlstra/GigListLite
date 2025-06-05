package com.gavinzijlstra.giglistlite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GigAdapter(private var gigList: List<Gig>) : RecyclerView.Adapter<GigAdapter.GigViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    inner class GigViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val header: TextView = itemView.findViewById(R.id.headerText)
        val headerContainer: View = itemView.findViewById(R.id.headerContainer)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val artistText: TextView = itemView.findViewById(R.id.artistText)
        val venueText: TextView = itemView.findViewById(R.id.venueText)
        val details: TextView = itemView.findViewById(R.id.detailsText)
        val caretIcon: ImageView = itemView.findViewById(R.id.caretIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GigViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gig_item, parent, false)
        return GigViewHolder(view)
    }

    override fun onBindViewHolder(holder: GigViewHolder, position: Int) {
        val Gig = gigList[position]

        val isExpanded = expandedPositions.contains(position)
        if (isExpanded) {
            holder.caretIcon.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
        } else {
            holder.caretIcon.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
        }

        holder.dateText.text = Gig.datum
        holder.artistText.text = Gig.band
        val location = if (Gig.zaal != "") " (" + Gig.zaal + ")" else ""
        holder.venueText.text = Gig.venue + location
        holder.details.text = Gig.opmerking
        holder.details.visibility = if (expandedPositions.contains(position)) View.VISIBLE else View.GONE

        holder.headerContainer.setOnClickListener {
            if (expandedPositions.contains(position)) expandedPositions.remove(position)
            else expandedPositions.add(position)
            notifyItemChanged(position)
        }
    }

    fun updateList(newList: List<Gig>) {
        gigList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = gigList.size
}