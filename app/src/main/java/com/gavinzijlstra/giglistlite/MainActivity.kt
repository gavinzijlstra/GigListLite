package com.gavinzijlstra.giglistlite

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.*
import android.util.Log
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var gigAdapter: GigAdapter
    private var allGigs: List<Gig> = emptyList()
    //val token = "12345"
    //val token = "8b1cfa0b-4a61-4536-b92a-210463027220" // Gavin
    val token = "32991640-9b1b-487a-86ac-2b21639ac300" // Manfredi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar = findViewById<EditText>(R.id.searchBar)
        val addGigFab = findViewById<FloatingActionButton>(R.id.addGigFab)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        gigAdapter = GigAdapter(emptyList()) { selectedGig ->
            GigDialog(this, token, selectedGig) {
                refreshGigList(swipeRefreshLayout)
            }.show()
        }
        recyclerView.adapter = gigAdapter

        val service = ApiClient.gigService
        service.getGigs(token).enqueue(object : Callback<List<Gig>> {
            override fun onResponse(call: Call<List<Gig>>, response: Response<List<Gig>>) {
                if (response.isSuccessful) {
                    allGigs = response.body() ?: emptyList()
                    Log.d("GigList-MainActivity", "Gigs ontvangen: ${allGigs.size}")
                    gigAdapter.updateList(allGigs)
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
            Log.d("GigList-MainActivity", "Search for: $query")
            val filteredList = allGigs.filter { gig ->
                gig.band.contains(query, ignoreCase = true)
            }
            gigAdapter.updateList(filteredList)
        }
        addGigFab.setOnClickListener {
            GigDialog(this, token, null) {
                refreshGigList(swipeRefreshLayout)
            }.show()
        }
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // geen drag & drop
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val gigToDelete = allGigs[position]

                // Bevestigingsdialog
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Verwijder Gig")
                    .setMessage("Weet je zeker dat je '${gigToDelete.band}' wilt verwijderen?")
                    .setPositiveButton("Ja") { _, _ ->
                        ApiClient.gigService.deleteGig(token, gigToDelete._id)
                            .enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    Log.e("GigList-MainActivity", "Response fout: ${response.code()} - ${response.message()}")
                                    if (response.isSuccessful) {
                                        Toast.makeText(this@MainActivity, "Gig verwijderd", Toast.LENGTH_SHORT).show()
                                        refreshGigList(swipeRefreshLayout)
                                    } else {
                                        Toast.makeText(this@MainActivity, "Verwijderen mislukt", Toast.LENGTH_SHORT).show()
                                        gigAdapter.notifyItemChanged(position)
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Toast.makeText(this@MainActivity, "Fout bij verbinding", Toast.LENGTH_SHORT).show()
                                    gigAdapter.notifyItemChanged(position)
                                }
                            })
                    }
                    .setNegativeButton("Nee") { _, _ ->
                        gigAdapter.notifyItemChanged(position) // swipe ongedaan maken
                    }
                    .setCancelable(false)
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        swipeRefreshLayout.setOnRefreshListener {
            refreshGigList(swipeRefreshLayout)
        }
    }

    fun refreshGigList(swipeRefreshLayout: SwipeRefreshLayout) {
        ApiClient.gigService.getGigs(token).enqueue(object : Callback<List<Gig>> {
            override fun onResponse(call: Call<List<Gig>>, response: Response<List<Gig>>) {
                if (response.isSuccessful) {
                    allGigs = response.body() ?: emptyList()
                    gigAdapter.updateList(allGigs)
                }
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Gig>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Fout bij verversen lijst", Toast.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

}