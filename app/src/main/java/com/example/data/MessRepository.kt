package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take

class MessRepository(private val messDao: MessDao) {

    val allListings: Flow<List<MessListing>> = messDao.getAllListings()

    fun getListingById(id: Int): Flow<MessListing?> = messDao.getListingById(id)

    fun getListingsByOwner(email: String): Flow<List<MessListing>> = messDao.getListingsByOwner(email)

    suspend fun insertListing(listing: MessListing): Long = messDao.insertListing(listing)

    suspend fun deleteListing(id: Int) = messDao.deleteListingById(id)

    // Users
    suspend fun getUserByEmail(email: String): User? = messDao.getUserByEmail(email)

    suspend fun registerUser(user: User): Long = messDao.insertUser(user)

    // Saved Listings
    fun getSavedListingsForUser(userEmail: String): Flow<List<MessListing>> =
        messDao.getSavedListingsForUser(userEmail)

    suspend fun saveListing(userEmail: String, listingId: Int) {
        messDao.insertSavedListing(SavedListing(userEmail, listingId))
    }

    suspend fun unsaveListing(userEmail: String, listingId: Int) {
        messDao.deleteSavedListing(userEmail, listingId)
    }

    fun isListingSaved(userEmail: String, listingId: Int): Flow<Boolean> =
        messDao.isListingSaved(userEmail, listingId)

    // Inquiries
    fun getInquiriesForOwner(ownerEmail: String): Flow<List<Inquiry>> =
        messDao.getInquiriesForOwner(ownerEmail)

    fun getInquiriesForSender(senderEmail: String): Flow<List<Inquiry>> =
        messDao.getInquiriesForSender(senderEmail)

    suspend fun sendInquiry(inquiry: Inquiry) {
        messDao.insertInquiry(inquiry)
    }

    // Pre-populate mock listings if empty
    suspend fun prepopulateIfEmpty() {
        val currentListings = messDao.getAllListings().first()
        if (currentListings.isEmpty()) {
            val sampleListings = listOf(
                MessListing(
                    title = "Premium Bachelor Room near Metro",
                    city = "Dhaka",
                    area = "Mirpur",
                    address = "Block D, Road 4, Mirpur-12, Dhaka",
                    price = 6500,
                    genderPreference = "male",
                    roomType = "single",
                    seatsAvailable = 1,
                    totalSeats = 1,
                    isVerified = true,
                    hasWiFi = true,
                    hasAC = false,
                    hasAttachedBath = true,
                    hasMealService = true,
                    hasGym = false,
                    hasSecurity = true,
                    description = "Cozy single room for a student or executive. Very close to Mirpur-12 Metro Rail station. Rent includes electricity, water, high-speed internet, and 3 meals/day.",
                    ownerName = "Naimur Rahman",
                    ownerPhone = "01712345678",
                    ownerEmail = "naimur@gmail.com",
                    imageId = 1
                ),
                MessListing(
                    title = "Spacious Sublet for University Students",
                    city = "Dhaka",
                    area = "Dhanmondi",
                    address = "Road 9A, Dhanmondi R/A, Dhaka",
                    price = 9500,
                    genderPreference = "male",
                    roomType = "shared",
                    seatsAvailable = 2,
                    totalSeats = 4,
                    isVerified = true,
                    hasWiFi = true,
                    hasAC = true,
                    hasAttachedBath = true,
                    hasMealService = false,
                    hasGym = false,
                    hasSecurity = true,
                    description = "Premium shared room for university students. Extremely close to Star Kabab and major universities. Looking for non-smoking, decent roommates. High-speed internet, AC, and regular cleaning services are available.",
                    ownerName = "Tanvir Ahmed",
                    ownerPhone = "01823456789",
                    ownerEmail = "tanvir@gmail.com",
                    imageId = 2
                ),
                MessListing(
                    title = "Quiet Single Room for Job Holders",
                    city = "Dhaka",
                    area = "Mohammadpur",
                    address = "Kaderabad Housing, Mohammadpur, Dhaka",
                    price = 5000,
                    genderPreference = "male",
                    roomType = "single",
                    seatsAvailable = 1,
                    totalSeats = 1,
                    isVerified = false,
                    hasWiFi = true,
                    hasAC = false,
                    hasAttachedBath = false,
                    hasMealService = true,
                    hasGym = false,
                    hasSecurity = false,
                    description = "Single room in a quiet bachelors flat. Ideal for job holders. Clean environment. Friendly flatmates. Maid service and shared kitchen included.",
                    ownerName = "Sabbir Hossain",
                    ownerPhone = "01934567890",
                    ownerEmail = "sabbir@gmail.com",
                    imageId = 3
                ),
                MessListing(
                    title = "Executive Female Mess Seat",
                    city = "Dhaka",
                    area = "Uttara",
                    address = "Sector 4, Road 12, Uttara, Dhaka",
                    price = 4800,
                    genderPreference = "female",
                    roomType = "shared",
                    seatsAvailable = 1,
                    totalSeats = 3,
                    isVerified = true,
                    hasWiFi = true,
                    hasAC = false,
                    hasAttachedBath = true,
                    hasMealService = true,
                    hasGym = true,
                    hasSecurity = true,
                    description = "Verified executive female mess seat. Highly safe and secure building with 24/7 CCTV, guard and lift. Includes laundry, wifi, filtered drinking water, and quality home-cooked meals.",
                    ownerName = "Farhana Islam",
                    ownerPhone = "01545678901",
                    ownerEmail = "farhana@gmail.com",
                    imageId = 4
                ),
                MessListing(
                    title = "Cozy Bachelor Seat near GEC Circle",
                    city = "Chattogram",
                    area = "GEC Circle",
                    address = "O.R. Nizam Road, GEC, Chattogram",
                    price = 4500,
                    genderPreference = "male",
                    roomType = "shared",
                    seatsAvailable = 2,
                    totalSeats = 3,
                    isVerified = true,
                    hasWiFi = true,
                    hasAC = false,
                    hasAttachedBath = true,
                    hasMealService = true,
                    hasGym = false,
                    hasSecurity = true,
                    description = "Comfortable shared seat near GEC circle, perfect for students of IIUC or Chittagong University. WiFi and meal service included. Polite roommates and 24/7 water supply.",
                    ownerName = "Imran Khan",
                    ownerPhone = "01723456780",
                    ownerEmail = "imran@gmail.com",
                    imageId = 5
                ),
                MessListing(
                    title = "Luxury Sublet for Students near SUST",
                    city = "Sylhet",
                    area = "Amberkhana",
                    address = "Amberkhana Point, Sylhet",
                    price = 7500,
                    genderPreference = "male",
                    roomType = "single",
                    seatsAvailable = 1,
                    totalSeats = 1,
                    isVerified = true,
                    hasWiFi = true,
                    hasAC = true,
                    hasAttachedBath = true,
                    hasMealService = false,
                    hasGym = false,
                    hasSecurity = true,
                    description = "Private single room with AC and attached bath. Perfect for students studying at SUST or job holders in Sylhet. High speed internet included. Quiet neighborhood.",
                    ownerName = "Rayhan Ahmed",
                    ownerPhone = "01834567891",
                    ownerEmail = "rayhan@gmail.com",
                    imageId = 6
                ),
                MessListing(
                    title = "Budget Room near Rajshahi University",
                    city = "Rajshahi",
                    area = "Kazla",
                    address = "Kazla Gate, Rajshahi",
                    price = 3000,
                    genderPreference = "male",
                    roomType = "shared",
                    seatsAvailable = 3,
                    totalSeats = 4,
                    isVerified = false,
                    hasWiFi = true,
                    hasAC = false,
                    hasAttachedBath = false,
                    hasMealService = true,
                    hasGym = false,
                    hasSecurity = false,
                    description = "Very affordable shared mess seat right next to Rajshahi University Kazla gate. Friendly environment, perfect for students. Includes wifi and daily meal system.",
                    ownerName = "Mizanur Rahman",
                    ownerPhone = "01945678902",
                    ownerEmail = "mizan@gmail.com",
                    imageId = 1
                ),
                MessListing(
                    title = "Single Seat near Shib Bari Circle",
                    city = "Khulna",
                    area = "Shib Bari",
                    address = "Shib Bari More, Khulna",
                    price = 4000,
                    genderPreference = "male",
                    roomType = "single",
                    seatsAvailable = 1,
                    totalSeats = 1,
                    isVerified = true,
                    hasWiFi = true,
                    hasAC = false,
                    hasAttachedBath = false,
                    hasMealService = false,
                    hasGym = false,
                    hasSecurity = true,
                    description = "Clean, private single room for job holders or KUET students. Very close to Shib Bari more. Transport is easily available. Rent is highly competitive.",
                    ownerName = "Asif Iqbal",
                    ownerPhone = "01556789013",
                    ownerEmail = "asif@gmail.com",
                    imageId = 2
                ),
                MessListing(
                    title = "Comfortable Sublet near Rupatali",
                    city = "Barishal",
                    area = "Rupatali",
                    address = "Rupatali Housing, Barishal",
                    price = 3500,
                    genderPreference = "male",
                    roomType = "single",
                    seatsAvailable = 1,
                    totalSeats = 1,
                    isVerified = true,
                    hasWiFi = true,
                    hasAC = false,
                    hasAttachedBath = true,
                    hasMealService = false,
                    hasGym = false,
                    hasSecurity = false,
                    description = "Sublet single room with private bathroom near Rupatali bus stand. Open to decent bachelors. 24 hours water and gas included in rent.",
                    ownerName = "Sujon Islam",
                    ownerPhone = "01667890124",
                    ownerEmail = "sujon@gmail.com",
                    imageId = 3
                ),
                MessListing(
                    title = "Spacious Room near Medical More",
                    city = "Rangpur",
                    area = "Medical More",
                    address = "Medical East Gate, Rangpur",
                    price = 3200,
                    genderPreference = "female",
                    roomType = "shared",
                    seatsAvailable = 2,
                    totalSeats = 3,
                    isVerified = true,
                    hasWiFi = true,
                    hasAC = false,
                    hasAttachedBath = true,
                    hasMealService = true,
                    hasGym = false,
                    hasSecurity = true,
                    description = "Safe and secure shared room for female students of Rangpur Medical College or job holders. Guard, wifi, and laundry service available on-site.",
                    ownerName = "Tahmina Akter",
                    ownerPhone = "01378901235",
                    ownerEmail = "tahmina@gmail.com",
                    imageId = 4
                ),
                MessListing(
                    title = "Cozy Single Mess Seat near Ganginarpar",
                    city = "Mymensingh",
                    area = "Ganginarpar",
                    address = "Ganginarpar Road, Mymensingh",
                    price = 3000,
                    genderPreference = "male",
                    roomType = "single",
                    seatsAvailable = 1,
                    totalSeats = 1,
                    isVerified = false,
                    hasWiFi = true,
                    hasAC = false,
                    hasAttachedBath = false,
                    hasMealService = true,
                    hasGym = false,
                    hasSecurity = false,
                    description = "Single room in a bachelor mess close to Ganginarpar. Clean, spacious, and very close to local transport and markets. Shared kitchen and maid services available.",
                    ownerName = "Mamun Rashid",
                    ownerPhone = "01489012346",
                    ownerEmail = "mamun@gmail.com",
                    imageId = 5
                )
            )
            for (listing in sampleListings) {
                messDao.insertListing(listing)
            }
        }
    }
}
