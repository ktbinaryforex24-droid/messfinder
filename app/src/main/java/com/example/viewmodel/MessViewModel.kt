package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Inquiry
import com.example.data.MessListing
import com.example.data.MessRepository
import com.example.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    DETAIL,
    ADD_LISTING,
    DASHBOARD,
    AUTH,
    SAVED,
    MESSAGES,
    NOTIFICATIONS,
    COMPARE
}

data class ChatMessage(
    val id: String,
    val senderEmail: String,
    val receiverEmail: String,
    val text: String,
    val timestamp: Long,
    val isSentByMe: Boolean
)

data class Conversation(
    val id: String,
    val participantName: String,
    val participantEmail: String,
    val lastMessage: String,
    val timestamp: Long,
    val unread: Boolean,
    val listingId: Int? = null,
    val listingTitle: String? = null
)

data class Review(
    val id: String,
    val listingId: Int,
    val reviewerName: String,
    val rating: Int,
    val comment: String,
    val date: String
)

data class AppNotification(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val type: String // "listing", "message", "review"
)

class MessViewModel(
    application: Application,
    private val repository: MessRepository
) : AndroidViewModel(application) {

    init {
        // Run pre-population in a coroutine
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // --- Navigation State ---
    val currentScreen = MutableStateFlow(Screen.HOME)
    val selectedListingId = MutableStateFlow<Int?>(null)

    // --- Map View State ---
    val isMapView = MutableStateFlow(false)

    // --- Compare Feature State ---
    val isCompareMode = MutableStateFlow(false)
    val selectedCompareListings = MutableStateFlow<Set<Int>>(emptySet())

    fun toggleCompareListing(listingId: Int) {
        val current = selectedCompareListings.value.toMutableSet()
        if (current.contains(listingId)) {
            current.remove(listingId)
        } else {
            if (current.size < 3) {
                current.add(listingId)
            }
        }
        selectedCompareListings.value = current
    }

    fun clearCompareListings() {
        selectedCompareListings.value = emptySet()
        isCompareMode.value = false
    }

    // --- Notifications State ---
    val notifications = MutableStateFlow<List<AppNotification>>(
        listOf(
            AppNotification("1", "New Listing in Mirpur", "A premium bachelor room was listed in your preferred area Mirpur starting at ৳5,500.", "10 min ago", false, "listing"),
            AppNotification("2", "New Reply from Nusrat", "Nusrat Jahan replied to your inquiry: 'Sure, you can visit tomorrow...'", "2 hours ago", false, "message"),
            AppNotification("3", "Rate your stay!", "Share your experience with Zahid Hasan. Leave a review for Premium Bachelor Room.", "1 day ago", true, "review"),
            AppNotification("4", "Price drop alert!", "A room in Dhanmondi was reduced from ৳10,000 to ৳9,000. Check it out!", "Yesterday", true, "listing")
        )
    )

    val unreadNotificationsCount: StateFlow<Int> = notifications
        .combine(MutableStateFlow(Unit)) { list, _ ->
            list.filter { !it.isRead }.size
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)

    fun markAllNotificationsRead() {
        notifications.value = notifications.value.map { it.copy(isRead = true) }
    }

    fun markNotificationRead(id: String) {
        notifications.value = notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun addNotification(title: String, desc: String, type: String) {
        val newNotif = AppNotification(
            id = System.currentTimeMillis().toString(),
            title = title,
            description = desc,
            timestamp = "Just now",
            isRead = false,
            type = type
        )
        notifications.value = listOf(newNotif) + notifications.value
    }

    // --- Reviews & Ratings State ---
    val listingReviews = MutableStateFlow<Map<Int, List<Review>>>(
        mapOf(
            1 to listOf(
                Review("1", 1, "Sabbir Rahman", 5, "Very clean room and extremely friendly roommates. Highly recommended!", "2026-07-15"),
                Review("2", 1, "Tanvir Ahmed", 4, "Great location right near the metro station. WiFi is super fast.", "2026-07-10")
            ),
            2 to listOf(
                Review("1", 2, "Nabila Islam", 5, "Amazing sublet, quiet and secure neighborhood. Felt very safe.", "2026-07-18")
            ),
            3 to listOf(
                Review("1", 3, "Kamrul Hasan", 4, "Affordable and clean single room. Owner is very supportive.", "2026-07-12"),
                Review("2", 3, "Sajid Khan", 3, "Good room but meal service is sometimes late.", "2026-07-08")
            ),
            4 to listOf(
                Review("1", 4, "Sadia Tasnim", 5, "Extremely secure female hostel with top notch amenities. Gym access is a huge plus!", "2026-07-16")
            ),
            5 to listOf(
                Review("1", 5, "Rakib Hossain", 4, "Excellent shared seat near GEC. Meal system is really good.", "2026-07-14")
            ),
            6 to listOf(
                Review("1", 6, "Faisal Ahmed", 5, "Luxury sublet indeed! High speed AC and beautiful view.", "2026-07-19")
            )
        )
    )

    fun getListingRatingInfo(listingId: Int): Pair<Double, Int> {
        val reviewsList = listingReviews.value[listingId] ?: emptyList()
        if (reviewsList.isEmpty()) {
            val seedRating = 4.0 + (listingId % 10) * 0.1
            return Pair(seedRating.coerceAtMost(5.0), 3 + (listingId % 5))
        }
        val avg = reviewsList.map { it.rating }.average()
        return Pair(avg, reviewsList.size)
    }

    fun addListingReview(listingId: Int, reviewerName: String, rating: Int, comment: String) {
        val newReview = Review(
            id = System.currentTimeMillis().toString(),
            listingId = listingId,
            reviewerName = reviewerName,
            rating = rating,
            comment = comment,
            date = "2026-07-20"
        )
        val current = listingReviews.value.toMutableMap()
        val list = (current[listingId] ?: emptyList()) + newReview
        current[listingId] = list
        listingReviews.value = current
        
        // Add a notification for review submitted
        addNotification(
            title = "Review Added",
            desc = "You added a $rating-star review for listing #$listingId.",
            type = "review"
        )
    }

    fun addReview(listingId: Int, rating: Int, comment: String) {
        val reviewerName = currentUser.value?.username ?: "Guest Seeker"
        addListingReview(listingId, reviewerName, rating, comment)
    }

    // --- Messages & Chat State ---
    val selectedConversationId = MutableStateFlow<String?>(null)
    val currentChatMessageText = MutableStateFlow("")

    val chatMessagesState = MutableStateFlow<Map<String, List<ChatMessage>>>(
        mapOf(
            "zahid@gmail.com" to listOf(
                ChatMessage("1", "zahid@gmail.com", "me", "Hello! Are you interested in the Premium Bachelor Room?", System.currentTimeMillis() - 3600000, false),
                ChatMessage("2", "me", "zahid@gmail.com", "Assalamu Alaikum, yes. Is the single room still available?", System.currentTimeMillis() - 1800000, true)
            ),
            "nusrat@gmail.com" to listOf(
                ChatMessage("1", "nusrat@gmail.com", "me", "Assalamu Alaikum. The seat is available.", System.currentTimeMillis() - 86400000, false),
                ChatMessage("2", "me", "nusrat@gmail.com", "Can I visit the place?", System.currentTimeMillis() - 80000000, true),
                ChatMessage("3", "nusrat@gmail.com", "me", "Sure, you can visit tomorrow at 5 PM.", System.currentTimeMillis() - 75000000, false)
            ),
            "arif@gmail.com" to listOf(
                ChatMessage("1", "me", "arif@gmail.com", "Is there any discount on the rent?", System.currentTimeMillis() - 172800000, true),
                ChatMessage("2", "arif@gmail.com", "me", "The monthly rent is fixed.", System.currentTimeMillis() - 170000000, false)
            )
        )
    )

    val conversations = MutableStateFlow<List<Conversation>>(
        listOf(
            Conversation("zahid@gmail.com", "Zahid Hasan", "zahid@gmail.com", "Assalamu Alaikum, yes. Is the single room still available?", System.currentTimeMillis() - 1800000, true, 5, "Premium Bachelor Room near Metro"),
            Conversation("nusrat@gmail.com", "Nusrat Jahan", "nusrat@gmail.com", "Sure, you can visit tomorrow at 5 PM.", System.currentTimeMillis() - 75000000, false, 6, "Elite Sublet Room with AC"),
            Conversation("arif@gmail.com", "Arif Chowdhury", "arif@gmail.com", "The monthly rent is fixed.", System.currentTimeMillis() - 170000000, false, 3, "Luxury Bachelor Flat Room")
        )
    )

    fun sendChatMessage() {
        val text = currentChatMessageText.value.trim()
        val convId = selectedConversationId.value
        if (text.isEmpty() || convId == null) return
        
        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            senderEmail = "me",
            receiverEmail = convId,
            text = text,
            timestamp = System.currentTimeMillis(),
            isSentByMe = true
        )
        
        val currentMessages = chatMessagesState.value.toMutableMap()
        val list = (currentMessages[convId] ?: emptyList()) + newMessage
        currentMessages[convId] = list
        chatMessagesState.value = currentMessages
        
        val currentConvs = conversations.value.toMutableList()
        val idx = currentConvs.indexOfFirst { it.id == convId }
        if (idx != -1) {
            val oldConv = currentConvs[idx]
            currentConvs[idx] = oldConv.copy(
                lastMessage = text,
                timestamp = System.currentTimeMillis(),
                unread = false
            )
        } else {
            currentConvs.add(0, Conversation(convId, convId, convId, text, System.currentTimeMillis(), false))
        }
        conversations.value = currentConvs.sortedByDescending { it.timestamp }
        
        currentChatMessageText.value = ""

        // Add a simulation reply from the owner after a short delay!
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            val replyText = when {
                text.contains("available", ignoreCase = true) -> "Yes, it is still available. You are welcome to visit."
                text.contains("rent", ignoreCase = true) || text.contains("price", ignoreCase = true) -> "The price is slightly negotiable if you are a quiet, non-smoking professional."
                text.contains("visit", ignoreCase = true) || text.contains("see", ignoreCase = true) -> "Sure! I am free this evening or tomorrow afternoon. What time suits you?"
                else -> "Got your message! Let me check and get back to you shortly."
            }
            val replyMessage = ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                senderEmail = convId,
                receiverEmail = "me",
                text = replyText,
                timestamp = System.currentTimeMillis(),
                isSentByMe = false
            )
            val updatedMessages = chatMessagesState.value.toMutableMap()
            updatedMessages[convId] = (updatedMessages[convId] ?: emptyList()) + replyMessage
            chatMessagesState.value = updatedMessages

            val updatedConvs = conversations.value.toMutableList()
            val cIdx = updatedConvs.indexOfFirst { it.id == convId }
            if (cIdx != -1) {
                val oldConv = updatedConvs[cIdx]
                updatedConvs[cIdx] = oldConv.copy(
                    lastMessage = replyText,
                    timestamp = System.currentTimeMillis(),
                    unread = true
                )
            }
            conversations.value = updatedConvs.sortedByDescending { it.timestamp }

            // Add notification for the reply!
            addNotification(
                title = "New message from ${currentConvs.firstOrNull { it.id == convId }?.participantName ?: convId}",
                desc = replyText,
                type = "message"
            )
        }
    }

    fun sendMessage(convId: String, text: String) {
        currentChatMessageText.value = text
        selectedConversationId.value = convId
        sendChatMessage()
    }

    fun closeConversation() {
        selectedConversationId.value = null
    }

    fun startChatWithListingOwner(listing: MessListing) {
        val convId = listing.ownerEmail
        selectedConversationId.value = convId
        
        val currentConvs = conversations.value.toMutableList()
        val exists = currentConvs.any { it.id == convId }
        if (!exists) {
            currentConvs.add(0, Conversation(
                id = convId,
                participantName = listing.ownerName,
                participantEmail = listing.ownerEmail,
                lastMessage = "Assalamu Alaikum, I am interested in ${listing.title}.",
                timestamp = System.currentTimeMillis(),
                unread = false,
                listingId = listing.id,
                listingTitle = listing.title
            ))
            conversations.value = currentConvs
            
            val currentMessages = chatMessagesState.value.toMutableMap()
            currentMessages[convId] = listOf(
                ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    senderEmail = "me",
                    receiverEmail = convId,
                    text = "Assalamu Alaikum, I am interested in ${listing.title}.",
                    timestamp = System.currentTimeMillis(),
                    isSentByMe = true
                )
            )
            chatMessagesState.value = currentMessages
        }
        
        // Mark all notifications for this conversation read
        selectedConversationId.value = convId
        val idx = currentConvs.indexOfFirst { it.id == convId }
        if (idx != -1) {
            currentConvs[idx] = currentConvs[idx].copy(unread = false)
            conversations.value = currentConvs
        }

        navigateTo(Screen.MESSAGES)
    }

    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }

    fun selectListingAndNavigate(id: Int) {
        selectedListingId.value = id
        currentScreen.value = Screen.DETAIL
    }

    // --- Auth State ---
    val currentUser = MutableStateFlow<User?>(null)
    val isSignUpMode = MutableStateFlow(false)
    val isOwnerToggle = MutableStateFlow(false) // false = seeker, true = owner
    val authUsername = MutableStateFlow("")
    val authEmail = MutableStateFlow("")
    val authPassword = MutableStateFlow("")
    val authPhone = MutableStateFlow("")
    val authError = MutableStateFlow<String?>(null)

    fun setSignUpMode(signUp: Boolean) {
        isSignUpMode.value = signUp
        authError.value = null
    }

    fun handleAuth() {
        val email = authEmail.value.trim()
        val password = authPassword.value.trim()
        val username = authUsername.value.trim()
        val phone = authPhone.value.trim()
        val isOwner = isOwnerToggle.value

        if (email.isEmpty() || password.isEmpty()) {
            authError.value = "Please fill in all fields"
            return
        }

        viewModelScope.launch {
            if (isSignUpMode.value) {
                if (username.isEmpty() || phone.isEmpty()) {
                    authError.value = "Please fill in all fields"
                    return@launch
                }
                val existing = repository.getUserByEmail(email)
                if (existing != null) {
                    authError.value = "User with this email already exists"
                } else {
                    val newUser = User(
                        username = username,
                        email = email,
                        passwordHash = password, // Simplified for mock
                        role = if (isOwner) "owner" else "seeker",
                        phone = phone
                    )
                    repository.registerUser(newUser)
                    currentUser.value = newUser
                    authError.value = null
                    // Clear inputs
                    clearAuthInputs()
                    navigateTo(Screen.HOME)
                }
            } else {
                val user = repository.getUserByEmail(email)
                if (user == null || user.passwordHash != password) {
                    authError.value = "Invalid email or password"
                } else {
                    currentUser.value = user
                    authError.value = null
                    clearAuthInputs()
                    navigateTo(Screen.HOME)
                }
            }
        }
    }

    fun logout() {
        currentUser.value = null
        navigateTo(Screen.HOME)
    }

    private fun clearAuthInputs() {
        authUsername.value = ""
        authEmail.value = ""
        authPassword.value = ""
        authPhone.value = ""
    }


    // --- Search & Filter State ---
    val searchQuery = MutableStateFlow("")
    val selectedCity = MutableStateFlow("All")
    val selectedArea = MutableStateFlow("All")
    val minPrice = MutableStateFlow(0f)
    val maxPrice = MutableStateFlow(20000f)
    val selectedGender = MutableStateFlow("All") // "All", "male", "female", "family"
    val selectedRoomType = MutableStateFlow("All") // "All", "single", "shared"
    val filterWiFi = MutableStateFlow(false)
    val filterAC = MutableStateFlow(false)
    val filterAttachedBath = MutableStateFlow(false)
    val filterMealService = MutableStateFlow(false)
    val filterGym = MutableStateFlow(false)
    val filterSecurity = MutableStateFlow(false)

    fun resetFilters() {
        searchQuery.value = ""
        selectedCity.value = "All"
        selectedArea.value = "All"
        minPrice.value = 0f
        maxPrice.value = 20000f
        selectedGender.value = "All"
        selectedRoomType.value = "All"
        filterWiFi.value = false
        filterAC.value = false
        filterAttachedBath.value = false
        filterMealService.value = false
        filterGym.value = false
        filterSecurity.value = false
    }

    // --- Listings Stream ---
    val allListings: StateFlow<List<MessListing>> = repository.allListings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Filtered Listings Stream ---
    val filteredListings: StateFlow<List<MessListing>> = combine(
        allListings,
        searchQuery,
        selectedCity,
        selectedArea,
        minPrice,
        maxPrice,
        selectedGender,
        selectedRoomType,
        filterWiFi,
        filterAC,
        filterAttachedBath,
        filterMealService,
        filterGym,
        filterSecurity
    ) { params ->
        val list = params[0] as List<MessListing>
        val query = params[1] as String
        val city = params[2] as String
        val area = params[3] as String
        val minP = params[4] as Float
        val maxP = params[5] as Float
        val gender = params[6] as String
        val rType = params[7] as String
        val wifi = params[8] as Boolean
        val ac = params[9] as Boolean
        val bath = params[10] as Boolean
        val meal = params[11] as Boolean
        val gym = params[12] as Boolean
        val sec = params[13] as Boolean

        list.filter { item ->
            val matchesQuery = query.isEmpty() || 
                    item.title.contains(query, ignoreCase = true) ||
                    item.address.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)

            val matchesCity = city == "All" || item.city.equals(city, ignoreCase = true)
            val matchesArea = area == "All" || item.area.equals(area, ignoreCase = true)
            val matchesPrice = item.price >= minP && item.price <= maxP
            val matchesGender = gender == "All" || item.genderPreference.equals(gender, ignoreCase = true)
            val matchesRoom = rType == "All" || item.roomType.equals(rType, ignoreCase = true)

            val matchesWiFi = !wifi || item.hasWiFi
            val matchesAC = !ac || item.hasAC
            val matchesBath = !bath || item.hasAttachedBath
            val matchesMeal = !meal || item.hasMealService
            val matchesGym = !gym || item.hasGym
            val matchesSec = !sec || item.hasSecurity

            matchesQuery && matchesCity && matchesArea && matchesPrice && matchesGender && matchesRoom &&
                    matchesWiFi && matchesAC && matchesBath && matchesMeal && matchesGym && matchesSec
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )


    // --- Selected Listing detail Flow ---
    val selectedListingDetail: StateFlow<MessListing?> = selectedListingId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getListingById(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    // --- Seeker Saved Listings ---
    val savedListings: StateFlow<List<MessListing>> = currentUser
        .flatMapLatest { user ->
            val email = user?.email ?: "guest"
            repository.getSavedListingsForUser(email)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleSavedListing(listingId: Int) {
        val email = currentUser.value?.email ?: "guest"
        viewModelScope.launch {
            val isSaved = repository.isListingSaved(email, listingId).first()
            if (isSaved) {
                repository.unsaveListing(email, listingId)
            } else {
                repository.saveListing(email, listingId)
            }
        }
    }

    fun isListingSavedFlow(listingId: Int): StateFlow<Boolean> {
        val email = currentUser.value?.email ?: "guest"
        return repository.isListingSaved(email, listingId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    }


    // --- Owner Listings ---
    val ownerListings: StateFlow<List<MessListing>> = currentUser
        .flatMapLatest { user ->
            if (user == null || user.role != "owner") flowOf(emptyList())
            else repository.getListingsByOwner(user.email)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteListing(listingId: Int) {
        viewModelScope.launch {
            repository.deleteListing(listingId)
        }
    }


    // --- Inquiries State ---
    val ownerInquiries: StateFlow<List<Inquiry>> = currentUser
        .flatMapLatest { user ->
            if (user == null || user.role != "owner") flowOf(emptyList())
            else repository.getInquiriesForOwner(user.email)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val seekerInquiries: StateFlow<List<Inquiry>> = currentUser
        .flatMapLatest { user ->
            if (user == null || user.role != "seeker") flowOf(emptyList())
            else repository.getInquiriesForSender(user.email)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val inquirySenderName = MutableStateFlow("")
    val inquirySenderPhone = MutableStateFlow("")
    val inquiryMessage = MutableStateFlow("Assalamu Alaikum, I am interested in your mess/sublet. Please let me know when I can visit.")
    val inquirySuccess = MutableStateFlow(false)

    fun sendInquiry(listing: MessListing) {
        val user = currentUser.value
        val name = if (user != null) user.username else inquirySenderName.value.trim()
        val phone = if (user != null) user.phone else inquirySenderPhone.value.trim()
        val email = if (user != null) user.email else "guest@gmail.com"

        if (name.isEmpty() || phone.isEmpty()) {
            return
        }

        viewModelScope.launch {
            val inquiry = Inquiry(
                listingId = listing.id,
                listingTitle = listing.title,
                ownerEmail = listing.ownerEmail,
                senderName = name,
                senderPhone = phone,
                senderEmail = email,
                message = inquiryMessage.value
            )
            repository.sendInquiry(inquiry)
            inquirySuccess.value = true
        }
    }

    fun resetInquiryStatus() {
        inquirySuccess.value = false
        inquirySenderName.value = ""
        inquirySenderPhone.value = ""
        inquiryMessage.value = "Assalamu Alaikum, I am interested in your mess/sublet. Please let me know when I can visit."
    }


    // --- "List Your Mess" Form State ---
    val formStep = MutableStateFlow(1) // 1: Basic Info, 2: Amenities & Details
    val formTitle = MutableStateFlow("")
    val formCity = MutableStateFlow("Dhaka")
    val formArea = MutableStateFlow("Mirpur")
    val formAddress = MutableStateFlow("")
    val formPrice = MutableStateFlow("")
    val formGenderPreference = MutableStateFlow("male") // "male", "female", "family"
    val formRoomType = MutableStateFlow("single") // "single", "shared"
    val formSeatsAvailable = MutableStateFlow("1")
    val formTotalSeats = MutableStateFlow("1")
    val formWiFi = MutableStateFlow(false)
    val formAC = MutableStateFlow(false)
    val formAttachedBath = MutableStateFlow(false)
    val formMealService = MutableStateFlow(false)
    val formGym = MutableStateFlow(false)
    val formSecurity = MutableStateFlow(false)
    val formDescription = MutableStateFlow("")
    val formError = MutableStateFlow<String?>(null)

    fun setFormStep(step: Int) {
        formStep.value = step
    }

    fun submitListing() {
        val user = currentUser.value
        if (user == null || user.role != "owner") {
            navigateTo(Screen.AUTH)
            return
        }

        val title = formTitle.value.trim()
        val city = formCity.value
        val area = formArea.value
        val address = formAddress.value.trim()
        val priceStr = formPrice.value.trim()
        val seatsAvailStr = formSeatsAvailable.value.trim()
        val totalSeatsStr = formTotalSeats.value.trim()
        val desc = formDescription.value.trim()

        if (title.isEmpty() || address.isEmpty() || priceStr.isEmpty() || desc.isEmpty()) {
            formError.value = "Please fill in all fields"
            return
        }

        val price = priceStr.toIntOrNull() ?: 0
        val seatsAvail = seatsAvailStr.toIntOrNull() ?: 1
        val totalSeats = totalSeatsStr.toIntOrNull() ?: 1

        if (price <= 0) {
            formError.value = "Please enter a valid price"
            return
        }

        viewModelScope.launch {
            val randomImageId = (1..6).random()
            val newListing = MessListing(
                title = title,
                city = city,
                area = area,
                address = address,
                price = price,
                genderPreference = formGenderPreference.value,
                roomType = formRoomType.value,
                seatsAvailable = seatsAvail,
                totalSeats = totalSeats,
                isVerified = true, // Owner-submitted on this app are verified by default for premium feel
                hasWiFi = formWiFi.value,
                hasAC = formAC.value,
                hasAttachedBath = formAttachedBath.value,
                hasMealService = formMealService.value,
                hasGym = formGym.value,
                hasSecurity = formSecurity.value,
                description = desc,
                ownerName = user.username,
                ownerPhone = user.phone,
                ownerEmail = user.email,
                imageId = randomImageId
            )
            repository.insertListing(newListing)
            formError.value = null
            resetListingForm()
            navigateTo(Screen.DASHBOARD)
        }
    }

    private fun resetListingForm() {
        formStep.value = 1
        formTitle.value = ""
        formCity.value = "Dhaka"
        formArea.value = "Mirpur"
        formAddress.value = ""
        formPrice.value = ""
        formGenderPreference.value = "male"
        formRoomType.value = "single"
        formSeatsAvailable.value = "1"
        formTotalSeats.value = "1"
        formWiFi.value = false
        formAC.value = false
        formAttachedBath.value = false
        formMealService.value = false
        formGym.value = false
        formSecurity.value = false
        formDescription.value = ""
        formError.value = null
    }

    // ViewModel Factory
    companion object {
        fun provideFactory(
            application: Application,
            repository: MessRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MessViewModel(application, repository) as T
            }
        }
    }
}
