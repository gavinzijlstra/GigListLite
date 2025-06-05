package com.gavinzijlstra.giglistlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import android.widget.EditText
import androidx.core.widget.addTextChangedListener

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var gigAdapter: GigAdapter
    private var allGigs: List<Gig> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar = findViewById<EditText>(R.id.searchBar)



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
                    Log.d("MainActivity", "Gigs ontvangen: ${allGigs.size}")
                    gigAdapter = GigAdapter(allGigs)
                    recyclerView.adapter = gigAdapter
                } else {
                    Log.e("MainActivity", "Response fout: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Gig>>, t: Throwable) {
                Log.e("MainActivity", "Fout bij ophalen data", t)
                Toast.makeText(this@MainActivity, "Fout bij ophalen data", Toast.LENGTH_SHORT).show()
            }
        })

        searchBar.addTextChangedListener {
            val query = it.toString()
            Log.d("MainActivity", "Search for: " + query)
            val filteredList = allGigs.filter { gig ->
                gig.band.contains(query, ignoreCase = true)
            }
            gigAdapter.updateList(filteredList)
        }
    }
}