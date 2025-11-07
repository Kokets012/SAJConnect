package com.example.sajconnect

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class OpportunityRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val cacheManager = OfflineCacheManager(context)
    private val _opportunities = MutableStateFlow<List<Opportunity>>(emptyList())
    val opportunities: StateFlow<List<Opportunity>> = _opportunities

    private companion object {
        const val TAG = "OpportunityRepository"
    }

    // Check if device is online
    fun isOnline(): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    // Load opportunities - tries online first, falls back to cache
    fun loadOpportunities() {
        Log.d(TAG, "Loading opportunities. Online: ${isOnline()}")

        if (isOnline()) {
            Log.d(TAG, "Loading from Firestore...")
            loadFromFirestore()
        } else {
            Log.d(TAG, "Loading from cache...")
            loadFromCache()
        }
    }

    private fun loadFromFirestore() {
        db.collection("opportunities")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Successfully loaded ${documents.size()} opportunities from Firestore")

                val opportunities = documents.map { doc ->
                    Opportunity(
                        id = doc.id,
                        title = doc.getString("title") ?: "No Title",
                        category = doc.getString("category") ?: "Uncategorized",
                        location = doc.getString("location") ?: "Location not specified",
                        description = doc.getString("description") ?: "No description"
                    )
                }

                // Update state
                _opportunities.value = opportunities

                // Save to cache
                cacheManager.saveOpportunities(opportunities)
                Log.d(TAG, "Saved ${opportunities.size} opportunities to cache")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to load from Firestore: ${exception.message}")
                // If Firestore fails, try cache
                loadFromCache()
            }
    }

    private fun loadFromCache() {
        val cachedOpportunities = cacheManager.getCachedOpportunities()
        Log.d(TAG, "Loaded ${cachedOpportunities.size} opportunities from cache")
        _opportunities.value = cachedOpportunities
    }

    // Manual sync
    fun manualSync() {
        if (isOnline()) {
            loadFromFirestore()
        }
    }

    // Check if we have cached data
    fun hasCachedData(): Boolean {
        return cacheManager.hasCachedOpportunities()
    }
}