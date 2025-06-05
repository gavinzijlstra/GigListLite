package com.gavinzijlstra.giglistlite

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var gigAdapter: GigAdapter
    private var allGigs: List<Gig> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar = findViewById<EditText>(R.id.searchBar)
        val addGigFab = findViewById<FloatingActionButton>(R.id.addGigFab)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://tiquidesign.nl/") // Pas dit aan
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GigService::class.java)
        service.getGigs().enqueue(object : Callback<List<Gig>> {
            override fun onResponse(call: Call<List<Gig>>, response: Response<List<Gig>>) {
                if (response.isSuccessful) {
                    allGigs = response.body() ?: emptyList()
                    Log.d("GigList-MainActivity", "Gigs ontvangen: ${allGigs.size}")
                    gigAdapter = GigAdapter(allGigs)
                    recyclerView.adapter = gigAdapter
                } else {
                    Log.e("GigList-MainActivity", "Response fout: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Gig>>, t: Throwable) {
                Log.e("GigList-MainActivity", "Fout bij ophalen data", t)
                Toast.makeText(this@MainActivity, "Fout bij ophalen data", Toast.LENGTH_SHORT).show()
            }
        })

        searchBar.addTextChangedListener {
            val query = it.toString()
            Log.d("GigList-MainActivity", "Search for: " + query)
            val filteredList = allGigs.filter { gig ->
                gig.band.contains(query, ignoreCase = true)
            }
            gigAdapter.updateList(filteredList)
        }

        addGigFab.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_gig, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Nieuwe Gig toevoegen")
                .setPositiveButton("Opslaan", null)  // Listener later instellen
                .setNegativeButton("Annuleren", null)

            val dialog = dialogBuilder.create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
                val btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                btnSave.setTextColor(Color.BLACK)
                btnSave.setOnClickListener {
                    val artist = dialogView.findViewById<EditText>(R.id.artistInput).text.toString()
                    val date = dialogView.findViewById<EditText>(R.id.dateInput).text.toString()
                    val venue = dialogView.findViewById<EditText>(R.id.venueInput).text.toString()
                    val zaal = dialogView.findViewById<EditText>(R.id.zaalInput).text.toString()
                    val opmerking = dialogView.findViewById<EditText>(R.id.opmerkingInput).text.toString()

                    // Maak nieuwe Gig en voeg toe aan lijst

                    Log.d("GigList-MainActivity", "artist: ${artist},date: ${date}, venue: ${venue} ")
                    // Validatie en toevoegen aan je lijst/adapter
                    if (artist.isNotBlank() && venue.isNotBlank() && date.isNotBlank()) {
                        //val newGig = Gig(artist = artist, venue = venue, date = date, details = "")
                        //allGigs.add(newGig)
                        //gigAdapter.updateList(allGigs)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "De velden band, zaal en datum zijn.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            dialog.show()
        }
    }
}