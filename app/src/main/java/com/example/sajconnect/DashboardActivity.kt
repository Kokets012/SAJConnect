package com.example.sajconnect

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

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

        testFCM()

        val testButton = findViewById<Button>(R.id.testNotificationButton)
        testButton.setOnClickListener {
            showTestNotification()
        }

    }


    private fun testFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TEST", "FCM Token: $token")

                // Use the separate TextView
                val tokenTextView = findViewById<TextView>(R.id.tokenTextView)
                tokenTextView.text = "FCM Token: $token" // Show full token

                Toast.makeText(this, "FCM Token received!", Toast.LENGTH_LONG).show()
                Log.d("FCM_FULL_TOKEN", "Full Token: $token")
            } else {
                Log.e("FCM_TEST", "FCM failed: ${task.exception}")
                Toast.makeText(this, "FCM setup issue", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun simulateNotification() {
        val simulateButton = findViewById<Button>(R.id.testNotificationButton)

        // If you don't have the button, create it programmatically
        val button = Button(this).apply {
            text = "Test Notification"
            setOnClickListener {
                showTestNotification()
            }
        }
        // Add to your layout or show as dialog
    }

    private fun showTestNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "saj_connect_channel",
                "SAJ Connect Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build and show notification
        val notification = NotificationCompat.Builder(this, "saj_connect_channel")
            .setContentTitle("SAJ Connect Test")
            .setContentText("Notifications are working! 🎉")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Use system icon
            .setAutoCancel(true)
            .build()

        notificationManager.notify(123, notification)

        Toast.makeText(this, "Test notification sent!", Toast.LENGTH_SHORT).show()
    }
}
