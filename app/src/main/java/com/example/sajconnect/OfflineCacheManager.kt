package com.example.sajconnect

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OfflineCacheManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("sajconnect_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Save opportunities to cache
    fun saveOpportunities(opportunities: List<Opportunity>) {
        val json = gson.toJson(opportunities)
        sharedPreferences.edit().putString("cached_opportunities", json).apply()
    }

    // Get opportunities from cache
    fun getCachedOpportunities(): List<Opportunity> {
        val json = sharedPreferences.getString("cached_opportunities", null)
        return if (json != null) {
            val type = object : TypeToken<List<Opportunity>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Check if we have cached data
    fun hasCachedOpportunities(): Boolean {
        return sharedPreferences.contains("cached_opportunities")
    }

    // Clear cache
    fun clearCache() {
        sharedPreferences.edit().remove("cached_opportunities").apply()
    }
}