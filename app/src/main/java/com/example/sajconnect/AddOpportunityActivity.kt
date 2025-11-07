package com.example.sajconnect

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddOpportunityActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_opportunity)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val titleField = findViewById<EditText>(R.id.titleField)
        val categoryField = findViewById<EditText>(R.id.categoryField)
        val locationField = findViewById<EditText>(R.id.locationField)
        val descriptionField = findViewById<EditText>(R.id.descriptionField)
        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val title = titleField.text.toString().trim()
            val category = categoryField.text.toString().trim()
            val location = locationField.text.toString().trim()
            val description = descriptionField.text.toString().trim()
            val userId = auth.currentUser?.uid ?: "unknown"

            if (title.isEmpty() || category.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = hashMapOf(
                "title" to title,
                "category" to category,
                "location" to location,
                "description" to description,
                "userId" to userId,
                "timestamp" to System.currentTimeMillis()
            )

            // Show loading
            saveButton.isEnabled = false
            saveButton.text = "Saving..."

            db.collection("opportunities").add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "✅ Opportunity added successfully!", Toast.LENGTH_SHORT).show()
                    // Send notification
                    sendNewOpportunityNotification(title)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    saveButton.isEnabled = true
                    saveButton.text = "Save"
                }
        }

        val homeButton = findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        supportActionBar?.hide()
    }


    private fun sendNewOpportunityNotification(opportunityTitle: String) {
        // This is a simple version - in a real app you'd use Cloud Functions
        // For testing, we'll just log it
        Log.d("NOTIFICATION", "New opportunity added: $opportunityTitle")

        // Show a local notification
        showLocalNotification(opportunityTitle)
    }

    private fun showLocalNotification(title: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "sajconnect_channel",
                "SAJConnect Opportunities",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new job opportunities"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "sajconnect_channel")
            .setSmallIcon(R.drawable.ic_notification) // Make sure you have this icon
            .setContentTitle(getString(R.string.new_opportunity_available))
            .setContentText("$title - ${getString(R.string.new_opportunity_available)}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }
}