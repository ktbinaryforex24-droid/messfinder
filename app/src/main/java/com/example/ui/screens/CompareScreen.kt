package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.data.MessListing
import com.example.ui.components.*
import androidx.compose.foundation.shape.CircleShape
import com.example.viewmodel.MessViewModel
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    viewModel: MessViewModel,
    modifier: Modifier = Modifier
) {
    val listings by viewModel.allListings.collectAsState()
    val selectedIds by viewModel.selectedCompareListings.collectAsState()

    val compareListings = remember(selectedIds, listings) {
        listings.filter { selectedIds.contains(it.id) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo_no_bg),
                            contentDescription = "MessFinder BD Logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compare Sublets",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (compareListings.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearCompareListings() }) {
                            Text("Clear All", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TealPrimary
                ),
                modifier = Modifier.shadow(4.dp)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmBackground)
                .padding(innerPadding)
        ) {
            if (compareListings.size < 2) {
                // Minimum comparison requirement empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(TealSecondary.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Select Listings to Compare",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Turn on 'Compare Mode' on the Home screen and select 2 or 3 bachelor sublets across Bangladesh to view them side-by-side.",
                        fontSize = 14.sp,
                        color = TextLight,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Select Listings Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                val horizontalScrollState = rememberScrollState()
                val verticalScrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(verticalScrollState)
                        .padding(bottom = 30.dp)
                ) {
                    // Title and Instructions
                    Text(
                        text = "Comparing ${compareListings.size} Sublets side-by-side",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight,
                        modifier = Modifier.padding(16.dp)
                    )

                    // Side Scrollable Table
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScrollState)
                            .padding(horizontal = 16.dp)
                    ) {
                        // Features Labels Column
                        Column(
                            modifier = Modifier.width(110.dp)
                        ) {
                            Spacer(modifier = Modifier.height(130.dp)) // Aligns labels with card images
                            CompareLabelRow(label = "Monthly Rent")
                            CompareLabelRow(label = "Location")
                            CompareLabelRow(label = "Room Type")
                            CompareLabelRow(label = "User Rating")
                            CompareLabelRow(label = "Gender Pref")
                            CompareLabelRow(label = "Seats Left")
                            CompareLabelRow(label = "WiFi Support")
                            CompareLabelRow(label = "Air Cond (AC)")
                            CompareLabelRow(label = "Attached Bath")
                            CompareLabelRow(label = "Meal Service")
                        }

                        // Listing Columns
                        compareListings.forEach { listing ->
                            Column(
                                modifier = Modifier
                                    .width(170.dp)
                                    .padding(horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Mini Header Card
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .shadow(3.dp, RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        ListingImage(imageId = listing.imageId, modifier = Modifier.fillMaxSize())
                                        
                                        // Remove Button
                                        IconButton(
                                            onClick = { viewModel.toggleCompareListing(listing.id) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(24.dp)
                                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }

                                        // Title overlay at the bottom
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .background(Color.Black.copy(alpha = 0.6f))
                                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = listing.title,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Monthly Rent
                                CompareValueRow(modifier = Modifier.background(TealSecondary.copy(alpha = 0.05f))) {
                                    Text(
                                        text = "৳${listing.price}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TealPrimary
                                    )
                                }

                                // Location
                                CompareValueRow {
                                    Text(
                                        text = listing.area,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Room Type
                                CompareValueRow {
                                    Text(
                                        text = listing.roomType.capitalize(),
                                        fontSize = 13.sp,
                                        color = TextDark
                                    )
                                }

                                // Rating Info
                                val (rating, reviewsCount) = viewModel.getListingRatingInfo(listing.id)
                                CompareValueRow {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = String.format("%.1f", rating), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(text = "($reviewsCount)", fontSize = 10.sp, color = TextLight)
                                    }
                                }

                                // Gender Pref
                                CompareValueRow {
                                    Text(
                                        text = listing.genderPreference.capitalize(),
                                        fontSize = 13.sp,
                                        color = if (listing.genderPreference == "boys") TealPrimary else CoralAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Seats Available
                                CompareValueRow {
                                    Text(
                                        text = "${listing.seatsAvailable}/${listing.totalSeats} left",
                                        fontSize = 13.sp,
                                        color = TextDark
                                    )
                                }

                                // WiFi Support
                                CompareValueRow {
                                    CheckOrCrossIcon(listing.hasWiFi)
                                }

                                // Air Cond (AC)
                                CompareValueRow {
                                    CheckOrCrossIcon(listing.hasAC)
                                }

                                // Attached Bath
                                CompareValueRow {
                                    CheckOrCrossIcon(listing.hasAttachedBath)
                                }

                                // Meal Service
                                CompareValueRow {
                                    CheckOrCrossIcon(listing.hasMealService)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompareLabelRow(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(0.5.dp, Color(0xFFE2E8F0))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextLight
        )
    }
}

@Composable
fun CompareValueRow(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(0.5.dp, Color(0xFFE2E8F0))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun CheckOrCrossIcon(isSupported: Boolean) {
    if (isSupported) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Supported",
            tint = GreenVerified,
            modifier = Modifier.size(18.dp)
        )
    } else {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Not Supported",
            tint = TextLight.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}
