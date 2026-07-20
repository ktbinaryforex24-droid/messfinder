package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MessDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long


    // --- Listings ---
    @Query("SELECT * FROM mess_listings ORDER BY id DESC")
    fun getAllListings(): Flow<List<MessListing>>

    @Query("SELECT * FROM mess_listings WHERE id = :id LIMIT 1")
    fun getListingById(id: Int): Flow<MessListing?>

    @Query("SELECT * FROM mess_listings WHERE ownerEmail = :email ORDER BY id DESC")
    fun getListingsByOwner(email: String): Flow<List<MessListing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: MessListing): Long

    @Query("DELETE FROM mess_listings WHERE id = :id")
    suspend fun deleteListingById(id: Int)


    // --- Saved Listings ---
    @Query("""
        SELECT * FROM mess_listings 
        INNER JOIN saved_listings ON mess_listings.id = saved_listings.listingId 
        WHERE saved_listings.userEmail = :userEmail 
        ORDER BY mess_listings.id DESC
    """)
    fun getSavedListingsForUser(userEmail: String): Flow<List<MessListing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedListing(savedListing: SavedListing)

    @Query("DELETE FROM saved_listings WHERE userEmail = :userEmail AND listingId = :listingId")
    suspend fun deleteSavedListing(userEmail: String, listingId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_listings WHERE userEmail = :userEmail AND listingId = :listingId)")
    fun isListingSaved(userEmail: String, listingId: Int): Flow<Boolean>


    // --- Inquiries ---
    @Query("SELECT * FROM inquiries WHERE ownerEmail = :ownerEmail ORDER BY timestamp DESC")
    fun getInquiriesForOwner(ownerEmail: String): Flow<List<Inquiry>>

    @Query("SELECT * FROM inquiries WHERE senderEmail = :senderEmail ORDER BY timestamp DESC")
    fun getInquiriesForSender(senderEmail: String): Flow<List<Inquiry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: Inquiry)
}
