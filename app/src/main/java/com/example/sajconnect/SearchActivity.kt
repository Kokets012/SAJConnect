package com.example.sajconnect

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OpportunityAdapter
    private lateinit var searchView: SearchView
    private lateinit var categorySpinner: Spinner
    private lateinit var locationSpinner: Spinner

    private val opportunities = mutableListOf<Opportunity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.searchResultsRecycler)
        searchView = findViewById(R.id.searchView)
        categorySpinner = findViewById(R.id.categorySpinner)
        locationSpinner = findViewById(R.id.locationSpinner)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ FIXED: Provide both arguments for adapter
        adapter = OpportunityAdapter(opportunities) { opportunity ->
            val intent = Intent(this, ApplyActivity::class.java)
            intent.putExtra("opportunityId", opportunity.id)
            intent.putExtra("opportunityTitle", opportunity.title)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        val categories = listOf("All", "Internship", "Full-time", "Part-time", "Remote")
        val locations = listOf("All", "Johannesburg", "Cape Town", "Durban", "Pretoria")

        categorySpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        locationSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)

        // Load all opportunities initially
        loadOpportunities()

        // Search text listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                filterResults(newText)
                return true
            }
        })

        // Filter when dropdowns change
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                filterResults(searchView.query.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                filterResults(searchView.query.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val homeButton = findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }


        // Hide the default ActionBar
        supportActionBar?.hide()
    }

    private fun loadOpportunities() {
        db.collection("opportunities").get()
            .addOnSuccessListener { docs ->
                opportunities.clear()
                for (doc in docs) {
                    val item = Opportunity(
                        doc.id,
                        doc.getString("title") ?: "",
                        doc.getString("category") ?: "",
                        doc.getString("location") ?: "",
                        doc.getString("description") ?: ""
                    )
                    opportunities.add(item)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterResults(query: String?) {
        val q = query?.lowercase() ?: ""
        val cat = categorySpinner.selectedItem.toString()
        val loc = locationSpinner.selectedItem.toString()

        val filtered = opportunities.filter {
            (cat == "All" || it.category == cat) &&
                    (loc == "All" || it.location == loc) &&
                    it.title.lowercase().contains(q)
        }

        adapter.updateData(filtered)
    }
}
