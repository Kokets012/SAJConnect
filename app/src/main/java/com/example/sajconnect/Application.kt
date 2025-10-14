package com.example.sajconnect

data class Application( //data class that gets a collection of the application created
    val id: String = "",
    val opportunityTitle: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val status: String = "Pending"
)