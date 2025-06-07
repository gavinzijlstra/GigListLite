package com.gavinzijlstra.giglistlite

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GigDialog(
    private val context: Context,
    private val token: String,
    private val gig: Gig? = null, // null = nieuw, anders = edit
    private val onComplete: () -> Unit
) {

    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_gig, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle(if (gig == null) "Nieuwe Gig toevoegen" else "Gig bewerken")
            .setPositiveButton("Opslaan", null)
            .setNegativeButton("Annuleren", null)

        val dialog = dialogBuilder.create()

        // Vul velden in bij bewerken
        val artistInput = dialogView.findViewById<EditText>(R.id.artistInput)
        val dateInput = dialogView.findViewById<EditText>(R.id.dateInput)
        val venueInput = dialogView.findViewById<EditText>(R.id.venueInput)
        val zaalInput = dialogView.findViewById<EditText>(R.id.zaalInput)
        val opmerkingInput = dialogView.findViewById<EditText>(R.id.opmerkingInput)

        gig?.let {
            artistInput.setText(it.band)
            dateInput.setText(it.datum)
            venueInput.setText(it.venue)
            zaalInput.setText(it.zaal)
            opmerkingInput.setText(it.opmerking)
        }

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
            val btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnSave.setTextColor(Color.BLACK)

            btnSave.setOnClickListener {
                val artist = artistInput.text.toString()
                val date = dateInput.text.toString()
                val venue = venueInput.text.toString()
                val zaal = zaalInput.text.toString()
                val opmerking = opmerkingInput.text.toString()

                if (artist.isNotBlank() && venue.isNotBlank() && date.isNotBlank()) {
                    val newGig = Gig(
                        datum = date,
                        venue = venue,
                        zaal = zaal,
                        band = artist,
                        opmerking = opmerking,
                        _id = gig?._id ?: "" // nodig bij update
                    )

                    val service = ApiClient.gigService
                    val call: Call<Void> = if (gig == null) {
                        service.addGig(token, newGig)
                    } else {
                        service.updateGig(token, newGig) // Dit vereist dat je `updateGig()` implementeert
                    }

                    call.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                onComplete()
                                dialog.dismiss()
                            } else {
                                Toast.makeText(context, "Fout bij opslaan", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(context, "Verbinding mislukt", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(context, "Alle velden zijn verplicht", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }
}