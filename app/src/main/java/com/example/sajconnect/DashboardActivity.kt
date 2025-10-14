package com.example.sajconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        val user = auth.currentUser
        welcomeText.text = "Welcome, ${user?.email ?: "User"}!"


        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }


        val viewOpportunities = findViewById<Button>(R.id.viewOpportunities)
        viewOpportunities.setOnClickListener {
            startActivity(Intent(this, OpportunityListActivity::class.java))
        }

        val searchButton = findViewById<Button>(R.id.searchOpportunitiesButton)
        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }


        val myAppsButton = findViewById<Button>(R.id.myApplicationsButton)
        myAppsButton.setOnClickListener {
            startActivity(Intent(this, MyApplicationsActivity::class.java))
        }



        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }
}
