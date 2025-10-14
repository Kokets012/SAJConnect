package com.example.sajconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyApplicationsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ApplicationAdapter
    private lateinit var progressBar: ProgressBar

    private val applications = mutableListOf<Application>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_applications)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.myApplicationsRecycler)
        progressBar = findViewById(R.id.loadingBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ApplicationAdapter(applications) { app ->
            confirmCancel(app)
        }

        recyclerView.adapter = adapter
        loadApplications()

        val homeButton = findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }


        supportActionBar?.hide()
    }

    private fun loadApplications() {
        val userId = auth.currentUser?.uid ?: return
        progressBar.visibility = android.view.View.VISIBLE

        db.collection("applications")
            .whereEqualTo("applicantId", userId)
            .get()
            .addOnSuccessListener { docs ->
                applications.clear()
                for (doc in docs) {
                    val app = Application(
                        id = doc.id,
                        opportunityTitle = doc.getString("opportunityTitle") ?: "Untitled",
                        message = doc.getString("message") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        status = doc.getString("status") ?: "Pending"
                    )
                    applications.add(app)
                }

                progressBar.visibility = android.view.View.GONE
                if (applications.isEmpty()) {
                    Toast.makeText(this, "No applications found.", Toast.LENGTH_SHORT).show()
                }
                adapter.updateData(applications)
            }
            .addOnFailureListener {
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Error loading applications: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmCancel(app: Application) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Application")
            .setMessage("Are you sure you want to cancel your application for '${app.opportunityTitle}'?")
            .setPositiveButton("Yes") { _, _ ->
                cancelApplication(app)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelApplication(app: Application) {
        db.collection("applications").document(app.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "❌ Application cancelled.", Toast.LENGTH_SHORT).show()
                applications.remove(app)
                adapter.updateData(applications)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cancelling: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
