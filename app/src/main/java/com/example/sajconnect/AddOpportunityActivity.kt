package com.example.sajconnect

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
        val descriptionField = findViewById<EditText>(R.id.descriptionField)
        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val title = titleField.text.toString().trim()
            val category = categoryField.text.toString().trim()
            val description = descriptionField.text.toString().trim()
            val userId = auth.currentUser?.uid ?: "unknown"

            if (title.isEmpty() || category.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            val data = hashMapOf(
                "title" to title,
                "category" to category,
                "description" to description,
                "userId" to userId,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("opportunities").add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "✅ Opportunity added successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        val homeButton = findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }


        // Hide the default ActionBar
        supportActionBar?.hide()
    }

}
