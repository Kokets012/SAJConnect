package com.example.sajconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ApplyActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Get opportunity info passed from previous page
        val opportunityId = intent.getStringExtra("opportunityId") ?: ""
        val opportunityName = intent.getStringExtra("opportunityTitle") ?: "Unknown Opportunity"

        // UI elements
        val opportunityNameText = findViewById<TextView>(R.id.opportunityName)
        val applicantName = findViewById<EditText>(R.id.applicantName)
        val applicantMessage = findViewById<EditText>(R.id.applicantMessage)
        val uploadCVButton = findViewById<Button>(R.id.uploadCVButton)
        val fileNameText = findViewById<TextView>(R.id.fileName)
        val submitButton = findViewById<Button>(R.id.submitApplication)
        val homeButton = findViewById<Button>(R.id.homeButton)
        val progressBar = findViewById<ProgressBar>(R.id.uploadProgress)

        opportunityNameText.text = opportunityName

        // Pick CV (simulated file selection)
        uploadCVButton.setOnClickListener {
            filePickerLauncher.launch("application/pdf")
        }

        // Submit application (simulated upload)
        submitButton.setOnClickListener {
            val name = applicantName.text.toString().trim()
            val message = applicantMessage.text.toString().trim()
            val userId = auth.currentUser?.uid ?: "unknown"

            if (name.isEmpty() || fileUri == null) {
                Toast.makeText(this, "Please provide your name and CV.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show fake upload animation
            progressBar.visibility = View.VISIBLE
            Toast.makeText(this, "📤 Uploading your CV, please wait...", Toast.LENGTH_SHORT).show()

            // Fake CV link (looks real)
            val fakeCvUrl = "https://example.com/cv/${System.currentTimeMillis()}.pdf"

            // Simulate upload delay (2 seconds)
            fileNameText.postDelayed({
                progressBar.visibility = View.GONE

                val data = hashMapOf(
                    "opportunityId" to opportunityId,
                    "opportunityTitle" to opportunityName,
                    "applicantId" to userId,
                    "applicantName" to name,
                    "message" to message,
                    "cvUrl" to fakeCvUrl,
                    "timestamp" to System.currentTimeMillis(),
                    "status" to "Pending" // Default status
                )

                db.collection("applications").add(data)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "✅ Application submitted successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "❌ Error saving application: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }, 2000)
        }

        // Navigate back home
        homeButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        supportActionBar?.hide()
    }

    // File picker logic
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                fileUri = uri
                findViewById<TextView>(R.id.fileName).text =
                    "File selected: ${uri.lastPathSegment}"
                Toast.makeText(this, "✅ File selected successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No file selected.", Toast.LENGTH_SHORT).show()
            }
        }
}
