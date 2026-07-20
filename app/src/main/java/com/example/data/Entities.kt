package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: String, // "seeker" or "owner"
    val phone: String
)

@Entity(tableName = "mess_listings")
data class MessListing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val city: String, // Dhaka, Chattogram, Sylhet, Rajshahi, Khulna, Barishal, Rangpur, Mymensingh
    val area: String, // Mirpur, Dhanmondi, Mohammadpur, Uttara, etc.
    val address: String,
    val price: Int,
    val genderPreference: String, // "male", "female", "family"
    val roomType: String, // "single", "shared"
    val seatsAvailable: Int,
    val totalSeats: Int,
    val isVerified: Boolean = false,
    val hasWiFi: Boolean = false,
    val hasAC: Boolean = false,
    val hasAttachedBath: Boolean = false,
    val hasMealService: Boolean = false,
    val hasGym: Boolean = false,
    val hasSecurity: Boolean = false,
    val description: String,
    val ownerName: String,
    val ownerPhone: String,
    val ownerEmail: String,
    val imageId: Int = 1 // 1 to 6 to map to visual designs
)

@Entity(tableName = "saved_listings", primaryKeys = ["userEmail", "listingId"])
data class SavedListing(
    val userEmail: String,
    val listingId: Int
)

@Entity(tableName = "inquiries")
data class Inquiry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val listingTitle: String,
    val ownerEmail: String,
    val senderName: String,
    val senderPhone: String,
    val senderEmail: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
