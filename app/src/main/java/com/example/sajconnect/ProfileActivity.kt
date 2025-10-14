package com.example.sajconnect

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailField = findViewById<EditText>(R.id.emailField)
        val displayNameField = findViewById<EditText>(R.id.displayNameField)
        val newPasswordField = findViewById<EditText>(R.id.newPasswordField)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        val currentUser = auth.currentUser

        // Load user info
        currentUser?.let { user ->
            emailField.setText(user.email)

            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    val name = doc.getString("displayName")
                    if (!name.isNullOrEmpty()) {
                        displayNameField.setText(name)
                    }
                }
        }

        // Update button logic
        updateButton.setOnClickListener {
            val newName = displayNameField.text.toString().trim()
            val newPassword = newPasswordField.text.toString().trim()

            if (newName.isEmpty() && newPassword.isEmpty()) {
                Toast.makeText(this, "Please update at least one field.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updates = mutableMapOf<String, Any>()
            if (newName.isNotEmpty()) updates["displayName"] = newName

            // Update Firestore display name
            if (updates.isNotEmpty() && currentUser != null) {
                db.collection("users").document(currentUser.uid).update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "✅ Profile updated!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error updating profile.", Toast.LENGTH_SHORT).show()
                    }
            }

            // Update password
            if (newPassword.isNotEmpty()) {
                if (newPassword.length < 8) {
                    Toast.makeText(this, "Password too short (min 8 characters).", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                currentUser?.updatePassword(newPassword)
                    ?.addOnSuccessListener {
                        Toast.makeText(this, "🔐 Password updated!", Toast.LENGTH_SHORT).show()
                        newPasswordField.text.clear()
                    }
                    ?.addOnFailureListener { e ->
                        if (e is FirebaseAuthRecentLoginRequiredException) {
                            Toast.makeText(this, "Please log in again to change password.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Error updating password: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        val homeButton = findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }


        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Hide the default ActionBar
        supportActionBar?.hide()
    }
}
