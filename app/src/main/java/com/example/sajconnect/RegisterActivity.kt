package com.example.sajconnect

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginRedirect = findViewById<TextView>(R.id.loginRedirect)
        val passwordHint = findViewById<TextView>(R.id.passwordHint)

        // Password strength validation
        passwordField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (isStrongPassword(password)) {
                    passwordHint.text = "✅ Strong password!"
                    passwordHint.setTextColor(getColor(android.R.color.holo_green_dark))
                } else {
                    passwordHint.text = "❌ Password too weak (8+ chars, upper, lower, number, symbol)."
                    passwordHint.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        registerButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            when {
                email.isEmpty() || password.isEmpty() ->
                    Toast.makeText(this, "Please fill in both email and password.", Toast.LENGTH_SHORT).show()
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                !isStrongPassword(password) ->
                    Toast.makeText(this, "Password is too weak. Try a stronger one.", Toast.LENGTH_LONG).show()
                else -> registerUser(email, password)
            }
        }

        loginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid ?: return@addOnSuccessListener
                val userMap = hashMapOf("email" to email, "createdAt" to System.currentTimeMillis())

                db.collection("users").document(userId).set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "🎉 Account created successfully!", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
            }
            .addOnFailureListener { e ->
                val message = when (e) {
                    is FirebaseAuthUserCollisionException -> "This email is already registered."
                    is FirebaseNetworkException -> "Check your internet connection."
                    else -> "Registration failed: ${e.message}"
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
    }

    private fun isStrongPassword(password: String): Boolean {
        val passwordPattern =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$")
        return password.matches(passwordPattern)
    }
}
