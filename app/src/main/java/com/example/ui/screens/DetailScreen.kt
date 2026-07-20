package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MessViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    viewModel: MessViewModel,
    modifier: Modifier = Modifier
) {
    val listing by viewModel.selectedListingDetail.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showInquiryDialog by remember { mutableStateOf(false) }
    var showWriteReviewDialog by remember { mutableStateOf(false) }
    var selectedStarsRating by remember { mutableStateOf(5) }
    var userReviewComment by remember { mutableStateOf("") }

    val inquirySuccess by viewModel.inquirySuccess.collectAsState()
    val inquirySenderName by viewModel.inquirySenderName.collectAsState()
    val inquirySenderPhone by viewModel.inquirySenderPhone.collectAsState()
    val inquiryMessage by viewModel.inquiryMessage.collectAsState()

    if (listing == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(WarmBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TealPrimary)
        }
        return
    }

    val item = listing!!
    val isSaved by viewModel.isListingSavedFlow(item.id).collectAsState()

    // Dialog for inquiries
    if (showInquiryDialog) {
        AlertDialog(
            onDismissRequest = {
                showInquiryDialog = false
                viewModel.resetInquiryStatus()
            },
            title = {
                Text(
                    text = "Send Inquiry to Owner",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TealPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (inquirySuccess) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Inquiry Sent",
                                tint = GreenVerified,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Inquiry Sent Successfully!",
                                color = TextDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "The owner will receive your message and contact phone number.",
                                color = TextLight,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Text(
                            text = "You are inquiring about:\n\"${item.title}\"",
                            fontSize = 12.sp,
                            color = TextLight,
                            fontWeight = FontWeight.Medium
                        )

                        // If not logged in, ask for name and phone
                        if (currentUser == null) {
                            OutlinedTextField(
                                value = inquirySenderName,
                                onValueChange = { viewModel.inquirySenderName.value = it },
                                label = { Text("Your Full Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary) },
                                modifier = Modifier.fillMaxWidth().testTag("inquiry_name_input")
                            )

                            OutlinedTextField(
                                value = inquirySenderPhone,
                                onValueChange = { viewModel.inquirySenderPhone.value = it },
                                label = { Text("Phone Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TealPrimary) },
                                modifier = Modifier.fillMaxWidth().testTag("inquiry_phone_input")
                            )
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TealPrimary.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = TealPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Inquiring as: ${currentUser!!.username} (${currentUser!!.phone})",
                                    fontSize = 12.sp,
                                    color = TextDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        OutlinedTextField(
                            value = inquiryMessage,
                            onValueChange = { viewModel.inquiryMessage.value = it },
                            label = { Text("Your Message") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth().testTag("inquiry_msg_input")
                        )
                    }
                }
            },
            confirmButton = {
                if (!inquirySuccess) {
                    Button(
                        onClick = { viewModel.sendInquiry(item) },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                        enabled = currentUser != null || (inquirySenderName.isNotBlank() && inquirySenderPhone.isNotBlank()),
                        modifier = Modifier.testTag("inquiry_confirm_btn")
                    ) {
                        Text("Send Message", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            showInquiryDialog = false
                            viewModel.resetInquiryStatus()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Close")
                    }
                }
            },
            dismissButton = {
                if (!inquirySuccess) {
                    TextButton(onClick = { showInquiryDialog = false }) {
                        Text("Cancel", color = TextLight)
                    }
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Dialog for write a review
    if (showWriteReviewDialog) {
        AlertDialog(
            onDismissRequest = { showWriteReviewDialog = false },
            title = {
                Text(
                    text = "Write a Review",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Rate your experience with this bachelor mess:",
                        fontSize = 13.sp,
                        color = TextLight
                    )

                    // Five Stars selection
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star $i",
                                tint = if (i <= selectedStarsRating) Color(0xFFFFB300) else Color(0xFFE2E8F0),
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { selectedStarsRating = i }
                                    .testTag("star_rate_$i")
                            )
                        }
                    }

                    OutlinedTextField(
                        value = userReviewComment,
                        onValueChange = { userReviewComment = it },
                        placeholder = { Text("Write your honest feedback here...", fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("review_comment_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userReviewComment.isNotBlank()) {
                            viewModel.addReview(item.id, selectedStarsRating, userReviewComment)
                            userReviewComment = ""
                            selectedStarsRating = 5
                            showWriteReviewDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    enabled = userReviewComment.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_review_button")
                ) {
                    Text("Submit Review", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWriteReviewDialog = false }
                ) {
                    Text("Cancel", color = TextLight)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Visual / Top Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                ListingImage(imageId = item.imageId, modifier = Modifier.fillMaxSize())

                // Custom Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent, Color.Black.copy(alpha = 0.25f))
                            )
                        )
                )

                // Top Header Buttons: Back and Bookmark Save
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(com.example.viewmodel.Screen.HOME) },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleSavedListing(item.id) },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            .size(38.dp)
                            .testTag("detail_save_btn")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save listing",
                            tint = if (isSaved) CoralAccent else TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Title Area overlay inside image bottom
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(TealPrimary, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${item.area}, ${item.city}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (item.isVerified) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(GreenVerified, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "VERIFIED",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Listing core information block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title and basic stats
                Column {
                    Text(
                        text = item.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.address,
                            fontSize = 13.sp,
                            color = TextLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Divider(color = TealPrimary.copy(alpha = 0.08f))

                // Price and Availability specs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Monthly Rent", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Text("৳${item.price}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TealPrimary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Available Seats", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Text("${item.seatsAvailable} of ${item.totalSeats} seats", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CoralAccent)
                    }
                }

                // Specs highlights chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(0.5.dp, TealPrimary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.People, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Preference", fontSize = 10.sp, color = TextLight, fontWeight = FontWeight.Bold)
                            Text(text = item.genderPreference.capitalize(), fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(0.5.dp, TealPrimary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Room Type", fontSize = 10.sp, color = TextLight, fontWeight = FontWeight.Bold)
                            Text(text = item.roomType.capitalize(), fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Description Block
                Column {
                    Text(
                        text = "Description",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.description,
                        fontSize = 14.sp,
                        color = TextLight,
                        lineHeight = 22.sp
                    )
                }

                // Amenities Flow
                Column {
                    Text(
                        text = "Amenities Offered",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (item.hasWiFi) AmenityTag(icon = Icons.Default.Wifi, label = "High-speed WiFi")
                        if (item.hasAC) AmenityTag(icon = Icons.Default.AcUnit, label = "Air Conditioner")
                        if (item.hasAttachedBath) AmenityTag(icon = Icons.Default.Bathtub, label = "Attached Bath")
                        if (item.hasMealService) AmenityTag(icon = Icons.Default.Restaurant, label = "Meals Service")
                        if (item.hasGym) AmenityTag(icon = Icons.Default.FitnessCenter, label = "Gym Access")
                        if (item.hasSecurity) AmenityTag(icon = Icons.Default.Security, label = "24/7 CCTV & Guard")
                    }
                }

                // Map Placeholder Box ( Dhaka Area Map design)
                Column {
                    Text(
                        text = "Location & Neighborhood",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TealPrimary.copy(alpha = 0.05f))
                            .border(1.dp, TealPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Map Placeholder",
                                tint = TealPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Interactive Map Placeholder (${item.area}, Dhaka)",
                                color = TealPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Transit, nearby hospitals & restaurants mapped",
                                color = TextLight,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // Owner Info contact Card
                Column {
                    Text(
                        text = "Mess Operator / Owner",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(TealPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.ownerName.take(2).uppercase(),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.ownerName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "Verified Mess Manager",
                                    fontSize = 12.sp,
                                    color = TextLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Verified badge icon
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Verified Owner",
                                tint = GreenVerified,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- REVIEWS & RATINGS SECTION ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    val reviewsMap by viewModel.listingReviews.collectAsState()
                    val reviewsList = reviewsMap[item.id] ?: emptyList()
                    val (avgRating, reviewCount) = viewModel.getListingRatingInfo(item.id)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reviews & Ratings ($reviewCount)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        
                        TextButton(
                            onClick = { showWriteReviewDialog = true },
                            modifier = Modifier.testTag("write_review_button")
                        ) {
                            Text("Write a Review", color = CoralAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Average Rating Summary Bar
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = String.format("%.1f", avgRating),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TealPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row {
                                    for (i in 1..5) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (i <= avgRating.toInt()) Color(0xFFFFB300) else Color(0xFFE2E8F0),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Average Rating",
                                    fontSize = 11.sp,
                                    color = TextLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Divider(
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(1.dp),
                                color = Color(0xFFE2E8F0)
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .padding(start = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("Cleanliness: Excellent", fontSize = 11.sp, color = TextDark, fontWeight = FontWeight.SemiBold)
                                Text("Safety: Highly Secured", fontSize = 11.sp, color = TextDark, fontWeight = FontWeight.SemiBold)
                                Text("Wifi Speed: Super Fast", fontSize = 11.sp, color = TextDark, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Reviews List
                    if (reviewsList.isEmpty()) {
                        Text(
                            text = "No reviews yet. Be the first to write a review!",
                            color = TextLight,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            reviewsList.forEach { review ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(1.dp, RoundedCornerShape(10.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = review.reviewerName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextDark
                                            )
                                            Text(
                                                text = review.date,
                                                fontSize = 11.sp,
                                                color = TextLight
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            for (i in 1..5) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (i <= review.rating) Color(0xFFFFB300) else Color(0xFFE2E8F0),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = review.comment,
                                            fontSize = 13.sp,
                                            color = TextDark,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- STICKY BOTTOM ACTIONS PANEL ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            shadowElevation = 12.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Price / Month", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextLight)
                    Text("৳${item.price}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TealPrimary)
                }

                TactileButton(
                    onClick = { showInquiryDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .padding(paddingValues = PaddingValues(start = 24.dp))
                        .testTag("send_inquiry_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralAccent)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send Inquiry",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
