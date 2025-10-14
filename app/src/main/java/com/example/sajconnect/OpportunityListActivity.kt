package com.example.sajconnect

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class OpportunityListActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var listContainer: LinearLayout
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opportunity_list)

        db = FirebaseFirestore.getInstance()
        listContainer = findViewById(R.id.listContainer)
        addButton = findViewById(R.id.addButton)

        loadOpportunities()

        addButton.setOnClickListener {
            startActivity(Intent(this, AddOpportunityActivity::class.java))
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
        listContainer.removeAllViews()

        db.collection("opportunities")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    val view = layoutInflater.inflate(R.layout.item_opportunity, null)
                    val title = doc.getString("title") ?: "Untitled"
                    val category = doc.getString("category") ?: "Uncategorized"
                    val description = doc.getString("description") ?: "No description"

                    view.findViewById<TextView>(R.id.itemTitle).text = title
                    view.findViewById<TextView>(R.id.itemCategory).text = category
                    view.findViewById<TextView>(R.id.itemDescription).text = description

                    val applyBtn = view.findViewById<Button>(R.id.applyButton)
                    applyBtn.setOnClickListener {
                        val intent = Intent(this, ApplyActivity::class.java)
                        intent.putExtra("opportunityId", doc.id)
                        intent.putExtra("opportunityTitle", title)
                        startActivity(intent)
                    }

                    listContainer.addView(view)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onResume() {
        super.onResume()
        loadOpportunities() // refresh after adding new ones
    }
}
