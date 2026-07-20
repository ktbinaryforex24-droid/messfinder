package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MessListing
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MessViewModel
import com.example.viewmodel.Screen

import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MessViewModel,
    modifier: Modifier = Modifier
) {
    val listings by viewModel.filteredListings.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedArea by viewModel.selectedArea.collectAsState()
    val minPrice by viewModel.minPrice.collectAsState()
    val maxPrice by viewModel.maxPrice.collectAsState()
    val selectedGender by viewModel.selectedGender.collectAsState()
    val selectedRoomType by viewModel.selectedRoomType.collectAsState()

    val filterWiFi by viewModel.filterWiFi.collectAsState()
    val filterAC by viewModel.filterAC.collectAsState()
    val filterAttachedBath by viewModel.filterAttachedBath.collectAsState()
    val filterMealService by viewModel.filterMealService.collectAsState()
    val filterGym by viewModel.filterGym.collectAsState()
    val filterSecurity by viewModel.filterSecurity.collectAsState()

    var showFiltersPanel by remember { mutableStateOf(false) }

    val isMapView by viewModel.isMapView.collectAsState()
    val isCompareMode by viewModel.isCompareMode.collectAsState()
    val selectedCompareListings by viewModel.selectedCompareListings.collectAsState()

    val areasList = when (selectedCity) {
        "Dhaka" -> listOf("All", "Mirpur", "Dhanmondi", "Mohammadpur", "Uttara", "Badda", "Farmgate", "Khilgaon", "Banani")
        "Chattogram" -> listOf("All", "GEC Circle", "Halishahar", "Panchlaish", "Agrabad", "Chawkbazar", "Nasirabad")
        "Sylhet" -> listOf("All", "Zindabazar", "Uposhahar", "Amberkhana", "Shibgonj", "Pathantula")
        "Rajshahi" -> listOf("All", "Shaheb Bazar", "Motihar", "Kazla", "Sopura")
        "Khulna" -> listOf("All", "Khalishpur", "Boyra", "Sonadanga", "Shib Bari")
        "Barishal" -> listOf("All", "Sadat Road", "Rupatali", "Natullabad")
        "Rangpur" -> listOf("All", "Dhapi", "Modern More", "Medical More")
        "Mymensingh" -> listOf("All", "Ganginarpar", "Charpara", "Valuka")
        else -> listOf("All", "Mirpur", "Dhanmondi", "Mohammadpur", "Uttara", "Badda", "GEC Circle", "Amberkhana", "Kazla")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // --- 1. HERO BANNER ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    // Banner Image
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "Bangladesh Sublets",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Linear Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        TealSecondary.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )

                    // Hero Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_logo_no_bg),
                                    contentDescription = "MessFinder BD Logo",
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "MessFinder BD",
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    lineHeight = 34.sp
                                )
                            }

                            // Notification Bell
                            val unreadNotifsCount by viewModel.unreadNotificationsCount.collectAsState()
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                    .clip(CircleShape)
                                    .clickable { viewModel.navigateTo(Screen.NOTIFICATIONS) }
                                    .testTag("notification_bell_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                if (unreadNotifsCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 2.dp, y = (-2).dp)
                                            .background(CoralAccent, CircleShape)
                                            .size(18.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = unreadNotifsCount.toString(),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                        
                        Text(
                            text = "Verified Bachelor Mess & Sublets Across Bangladesh",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Glassmorphic Search Input Overlapping Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchQuery.value = it },
                            placeholder = { Text("Search by area, road, landmark...", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_search_input")
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Map/List toggle button
                        TactileButton(
                            onClick = { viewModel.isMapView.value = !isMapView },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(38.dp)
                                .testTag("map_view_toggle"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMapView) CoralAccent else Color.White.copy(alpha = 0.2f)
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = if (isMapView) Icons.Default.List else Icons.Default.Map,
                                contentDescription = "Toggle Map",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isMapView) "List View" else "Map View",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Compare mode toggle button
                        TactileButton(
                            onClick = { 
                                viewModel.isCompareMode.value = !isCompareMode 
                                if (isCompareMode) {
                                    viewModel.clearCompareListings()
                                }
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(38.dp)
                                .testTag("compare_mode_toggle"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompareMode) CoralAccent else Color.White.copy(alpha = 0.2f)
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = "Toggle Compare",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCompareMode) "Exit Compare" else "Compare Mode",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // --- 2. QUICK FILTERS & TOGGLES ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Headline and filter button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Division",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    
                    // Filter Sidebar/Panel toggle
                    Row(
                        modifier = Modifier
                            .background(
                                color = if (showFiltersPanel) TealPrimary else TealPrimary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { showFiltersPanel = !showFiltersPanel }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter Toggle",
                            tint = if (showFiltersPanel) Color.White else TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Filter Panel",
                            color = if (showFiltersPanel) Color.White else TealPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cities/Divisions Horizontal Scroll
                val citiesList = listOf("All", "Dhaka", "Chattogram", "Sylhet", "Rajshahi", "Khulna", "Barishal", "Rangpur", "Mymensingh")
                val cityEmojis = mapOf(
                    "All" to "🇧🇩",
                    "Dhaka" to "🏙️",
                    "Chattogram" to "⚓",
                    "Sylhet" to "🍵",
                    "Rajshahi" to "🎓",
                    "Khulna" to "🏭",
                    "Barishal" to "🌾",
                    "Rangpur" to "❄️",
                    "Mymensingh" to "🏛️"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(citiesList) { city ->
                        val isSelected = selectedCity == city
                        val emoji = cityEmojis[city] ?: "📍"
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) TealPrimary else Color.White,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) TealPrimary else TealPrimary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    viewModel.selectedCity.value = city
                                    viewModel.selectedArea.value = "All"
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "$emoji ", fontSize = 14.sp)
                                Text(
                                    text = if (city == "All") "All BD" else city,
                                    color = if (isSelected) Color.White else TextDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2.5: Areas horizontal list
                Text(
                    text = if (selectedCity == "All") "Popular Areas" else "Areas in $selectedCity",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(areasList) { area ->
                        val isSelected = selectedArea == area
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) TealSecondary else Color.White,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) TealSecondary else TealSecondary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.selectedArea.value = area }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = area,
                                color = if (isSelected) Color.White else TextDark,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 3. EXPANDABLE FILTER SIDEBAR/PANEL ---
        item {
            AnimatedVisibility(
                visible = showFiltersPanel,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    borderColor = TealPrimary.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = "Refine Mess Search",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = TealPrimary.copy(alpha = 0.1f))

                    // Row 1: Gender Preference & Room Type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Gender selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Gender Preference", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight)
                            Spacer(modifier = Modifier.height(4.dp))
                            val genders = listOf("All", "male", "female", "family")
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(selectedGender.capitalize(), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    genders.forEach { g ->
                                        DropdownMenuItem(
                                            text = { Text(g.capitalize()) },
                                            onClick = {
                                                viewModel.selectedGender.value = g
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Room type selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Room Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight)
                            Spacer(modifier = Modifier.height(4.dp))
                            val roomTypes = listOf("All", "single", "shared")
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(selectedRoomType.capitalize(), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    roomTypes.forEach { r ->
                                        DropdownMenuItem(
                                            text = { Text(r.capitalize()) },
                                            onClick = {
                                                viewModel.selectedRoomType.value = r
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 2: Price Slider
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Price Filter (Monthly)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight)
                            Text("৳${minPrice.toInt()} - ৳${maxPrice.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                        Slider(
                            value = maxPrice,
                            onValueChange = { viewModel.maxPrice.value = it },
                            valueRange = 1000f..20000f,
                            colors = SliderDefaults.colors(
                                thumbColor = CoralAccent,
                                activeTrackColor = TealPrimary,
                                inactiveTrackColor = TealPrimary.copy(alpha = 0.12f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 3: Amenities checkboxes
                    Text("Amenities Required", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            FilterCheckbox(label = "WiFi", checked = filterWiFi) { viewModel.filterWiFi.value = it }
                            FilterCheckbox(label = "Attached Bath", checked = filterAttachedBath) { viewModel.filterAttachedBath.value = it }
                            FilterCheckbox(label = "Gym Access", checked = filterGym) { viewModel.filterGym.value = it }
                        }
                        Column {
                            FilterCheckbox(label = "Air Conditioner (AC)", checked = filterAC) { viewModel.filterAC.value = it }
                            FilterCheckbox(label = "Meal Service", checked = filterMealService) { viewModel.filterMealService.value = it }
                            FilterCheckbox(label = "CCTV Security", checked = filterSecurity) { viewModel.filterSecurity.value = it }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { viewModel.resetFilters() }) {
                            Text("Reset All", color = CoralAccent)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { showFiltersPanel = false },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text("Apply Filters")
                        }
                    }
                }
            }
        }

        // --- 4. LISTINGS SECTION ---
        if (listings.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "No listing found",
                        tint = TealPrimary.copy(alpha = 0.35f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Listings Match Your Filters",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try clearing search or adjusting filters.",
                        fontSize = 13.sp,
                        color = TextLight
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TactileButton(onClick = { viewModel.resetFilters() }) {
                        Text("Reset Filters")
                    }
                }
            }
        } else {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Featured Listings (${listings.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            }

            if (isMapView) {
                item {
                    MapViewContainer(
                        viewModel = viewModel,
                        listings = listings,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(listings) { listing ->
                    ListingCard(
                        listing = listing,
                        viewModel = viewModel,
                        onCardClick = { viewModel.selectListingAndNavigate(listing.id) }
                    )
                }
            }
        }
    }

    // Floating Compare Bar
    if (isCompareMode && selectedCompareListings.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 100.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp)),
                color = TealPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Compare Listings",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedCompareListings.size} of 3 selected",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { viewModel.clearCompareListings() }
                        ) {
                            Text("Clear", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(Screen.COMPARE) },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Compare Now", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapViewContainer(
    viewModel: MessViewModel,
    listings: List<MessListing>,
    modifier: Modifier = Modifier
) {
    var selectedListingForPin by remember { mutableStateOf<MessListing?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(440.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, TealPrimary.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
    ) {
        // Styled map canvas elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Render a stylized river
            val riverPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, height * 0.75f)
                quadraticTo(width * 0.4f, height * 0.65f, width, height * 0.85f)
            }
            drawPath(
                path = riverPath,
                color = Color(0xFF1E293B),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 40f)
            )

            // Render grid roads
            for (i in 1..4) {
                drawLine(
                    color = Color(0xFF334155).copy(alpha = 0.4f),
                    start = Offset(width * (i * 0.2f), 0f),
                    end = Offset(width * (i * 0.2f), height),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color(0xFF334155).copy(alpha = 0.4f),
                    start = Offset(0f, height * (i * 0.25f)),
                    end = Offset(width, height * (i * 0.25f)),
                    strokeWidth = 4f
                )
            }
        }

        // Display pins with price tags
        listings.forEachIndexed { index, listing ->
            val xOffset = 30.dp + (index % 3 * 105).dp
            val yOffset = 60.dp + (index % 4 * 85).dp

            Box(
                modifier = Modifier
                    .offset(x = xOffset, y = yOffset)
                    .background(
                        color = if (selectedListingForPin?.id == listing.id) CoralAccent else TealPrimary,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { selectedListingForPin = listing }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("map_pin_${listing.id}")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "৳${listing.price / 1000.0}K",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (selectedListingForPin == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Tap on any price pin to view details",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        selectedListingForPin?.let { selected ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp)
                    .shadow(12.dp, RoundedCornerShape(12.dp))
                    .clickable { viewModel.selectListingAndNavigate(selected.id) }
                    .testTag("map_overlay_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        ListingImage(imageId = selected.imageId, modifier = Modifier.fillMaxSize())
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selected.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${selected.area}, ${selected.city}",
                            fontSize = 11.sp,
                            color = TextLight
                        )
                        Text(
                            text = "৳${selected.price}/mo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }
                    IconButton(onClick = { selectedListingForPin = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextLight)
                    }
                }
            }
        }
    }
}

@Composable
fun ListingCard(
    listing: MessListing,
    viewModel: MessViewModel,
    onCardClick: () -> Unit
) {
    val isCompareMode by viewModel.isCompareMode.collectAsState()
    val selectedCompareListings by viewModel.selectedCompareListings.collectAsState()
    val isCompared = selectedCompareListings.contains(listing.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = TealPrimary.copy(alpha = 0.08f),
                spotColor = TealPrimary.copy(alpha = 0.12f)
            )
            .clickable(onClick = onCardClick)
            .testTag("listing_card_${listing.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                // Listing Image
                ListingImage(imageId = listing.imageId, modifier = Modifier.fillMaxSize())

                // Left: Custom compare round checkbox overlay if in compare mode
                if (isCompareMode) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.95f), CircleShape)
                            .border(1.dp, TealPrimary, CircleShape)
                            .clickable { viewModel.toggleCompareListing(listing.id) }
                            .testTag("compare_checkbox_${listing.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompared) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Compared",
                                tint = TealPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Right: Custom save overlay (heart icon)
                val isSaved by viewModel.isListingSavedFlow(listing.id).collectAsState(initial = false)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(34.dp)
                        .background(Color.White.copy(alpha = 0.95f), CircleShape)
                        .clickable { viewModel.toggleSavedListing(listing.id) }
                        .testTag("save_button_${listing.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save Listing",
                        tint = if (isSaved) CoralAccent else TextLight,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Location Badge Overlay bottom-left
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(TealSecondary.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${listing.area}, ${listing.city}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Seats leftover overlay bottom-right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${listing.seatsAvailable}/${listing.totalSeats} seats left",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Card Body Info
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = listing.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Star Rating display block
                val (rating, reviewCount) = viewModel.getListingRatingInfo(listing.id)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = String.format("%.1f", rating),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "($reviewCount reviews)",
                        fontSize = 11.sp,
                        color = TextLight
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = TextLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = listing.address,
                        fontSize = 12.sp,
                        color = TextLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Core Amenities Icons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (listing.hasWiFi) AmenityIconLabel(icon = Icons.Default.Wifi, label = "WiFi")
                    if (listing.hasAC) AmenityIconLabel(icon = Icons.Default.AcUnit, label = "AC")
                    if (listing.hasAttachedBath) AmenityIconLabel(icon = Icons.Default.Bathtub, label = "Bath")
                    if (listing.hasMealService) AmenityIconLabel(icon = Icons.Default.Restaurant, label = "Meals")
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                // Bottom row: Pricing & Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MONTHLY RENT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "৳${listing.price}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TealPrimary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .background(CoralAccent, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View Details",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Go",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = TealPrimary,
                checkmarkColor = Color.White
            )
        )
        Text(text = label, fontSize = 12.sp, color = TextDark)
    }
}

@Composable
fun AmenityIconLabel(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TealPrimary,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = label, fontSize = 10.sp, color = TextLight, fontWeight = FontWeight.Medium)
    }
}
