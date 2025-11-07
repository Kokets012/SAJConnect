package com.example.sajconnect

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class OpportunityListActivity : AppCompatActivity() {

    private lateinit var listContainer: LinearLayout
    private lateinit var addButton: Button
    private lateinit var repository: OpportunityRepository
    private lateinit var syncButton: Button
    private lateinit var offlineIndicator: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opportunity_list)

        repository = OpportunityRepository(this)
        listContainer = findViewById(R.id.listContainer)
        addButton = findViewById(R.id.addButton)
        syncButton = findViewById(R.id.syncButton)

        // Create offline indicator
        offlineIndicator = TextView(this).apply {
            text = "⚠️ Offline Mode - Showing cached data"
            setPadding(50, 20, 50, 20)
            textSize = 14f
            setBackgroundColor(0x33FFA500) // Light orange background
        }

        setupUI()
        loadOpportunities()

        val homeButton = findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        supportActionBar?.hide()
    }

    private fun setupUI() {
        addButton.setOnClickListener {
            startActivity(Intent(this, AddOpportunityActivity::class.java))
        }

        syncButton.setOnClickListener {
            refreshData()
        }
    }

    private fun loadOpportunities() {
        repository.loadOpportunities()

        // Observe the opportunities flow
        lifecycleScope.launch {
            repository.opportunities.collect { opportunities ->
                displayOpportunities(opportunities)
                updateOfflineIndicator()
            }
        }
    }

    private fun updateOfflineIndicator() {
        if (!repository.isOnline() && repository.hasCachedData()) {
            offlineIndicator.text = "⚠️ Offline Mode - Showing cached data"
            // Add indicator if not already added
            if (offlineIndicator.parent == null) {
                listContainer.addView(offlineIndicator, 0)
            }
            offlineIndicator.visibility = TextView.VISIBLE
        } else {
            offlineIndicator.visibility = TextView.GONE
        }
    }

    private fun refreshData() {
        if (repository.isOnline()) {
            repository.manualSync()
            Toast.makeText(this, "Syncing with server...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayOpportunities(opportunities: List<Opportunity>) {
        listContainer.removeAllViews()

        // Add offline indicator if needed
        updateOfflineIndicator()

        if (opportunities.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = if (repository.isOnline()) {
                    "No opportunities found. Pull to refresh."
                } else {
                    "No cached opportunities. Connect to internet to load data."
                }
                setPadding(50, 50, 50, 50)
                textSize = 16f
                gravity = android.view.Gravity.CENTER
            }
            listContainer.addView(emptyView)
            return
        }

        for (opportunity in opportunities) {
            val view = layoutInflater.inflate(R.layout.item_opportunity, null)

            view.findViewById<TextView>(R.id.itemTitle).text = opportunity.title
            view.findViewById<TextView>(R.id.itemCategory).text = opportunity.category
            view.findViewById<TextView>(R.id.itemDescription).text = opportunity.description

            // Show location if available
            val locationView = view.findViewById<TextView>(R.id.itemLocation)
            locationView?.text = opportunity.location ?: "Location not specified"

            val applyBtn = view.findViewById<Button>(R.id.applyButton)
            applyBtn.setOnClickListener {
                val intent = Intent(this, ApplyActivity::class.java)
                intent.putExtra("opportunityId", opportunity.id)
                intent.putExtra("opportunityTitle", opportunity.title)
                startActivity(intent)
            }

            listContainer.addView(view)
        }
    }

    override fun onResume() {
        super.onResume()
        loadOpportunities() // Refresh when returning to this screen
    }



}