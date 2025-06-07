package com.gavinzijlstra.giglistlite

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GigAdapter(
    private var gigList: List<Gig>,
    private val onGigLongClicked: (Gig) -> Unit
) : RecyclerView.Adapter<GigAdapter.GigViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    inner class GigViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val header: TextView = itemView.findViewById(R.id.headerText)
        val headerContainer: View = itemView.findViewById(R.id.headerContainer)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val artistText: TextView = itemView.findViewById(R.id.artistText)
        val venueText: TextView = itemView.findViewById(R.id.venueText)
        val caretIcon: ImageView = itemView.findViewById(R.id.caretIcon)
        val details: TextView = itemView.findViewById(R.id.detailsText)
        val detailsContainer : LinearLayout = itemView.findViewById(R.id.detailsContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GigViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gig_item, parent, false)
        return GigViewHolder(view)
    }

    override fun onBindViewHolder(holder: GigViewHolder, position: Int) {
        val gig = gigList[position]

        val isExpanded = expandedPositions.contains(position)
        if (isExpanded) {
            holder.caretIcon.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
        } else {
            holder.caretIcon.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
        }

        holder.dateText.text = gig.datum
        holder.artistText.text = gig.band
        val location = if (gig.zaal != "") " (" + gig.zaal + ")" else ""
        holder.venueText.text = "${gig.venue}$location"
        holder.details.text = gig.opmerking
        //holder.details.visibility = if (expandedPositions.contains(position)) View.VISIBLE else View.GONE
        holder.detailsContainer.visibility = if (expandedPositions.contains(position)) View.VISIBLE else View.GONE

        // Openvouwen van de description
        holder.headerContainer.setOnClickListener {
            if (expandedPositions.contains(position)) expandedPositions.remove(position)
            else expandedPositions.add(position)
            notifyItemChanged(position)
        }

        // Long press op band-naam
        holder.artistText.setOnLongClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
            //onLongClick(gig)
            //Toast.makeText(holder.itemView.context, "Long click op: ${gig.band}", Toast.LENGTH_SHORT).show()
            Log.d("GigList-GigAdapter", "Long click op: ${gig.band}")
            val context = holder.itemView.context
            if (context is MainActivity) {
                onGigLongClicked(gig)
            }
            true
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Gig>) {
        gigList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = gigList.size

}