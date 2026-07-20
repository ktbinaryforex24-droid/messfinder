package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MessViewModel
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddListingScreen(
    viewModel: MessViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()

    val formStep by viewModel.formStep.collectAsState()
    val formTitle by viewModel.formTitle.collectAsState()
    val formCity by viewModel.formCity.collectAsState()
    val formArea by viewModel.formArea.collectAsState()
    val formAddress by viewModel.formAddress.collectAsState()
    val formPrice by viewModel.formPrice.collectAsState()
    val formGenderPreference by viewModel.formGenderPreference.collectAsState()
    val formRoomType by viewModel.formRoomType.collectAsState()
    val formSeatsAvailable by viewModel.formSeatsAvailable.collectAsState()
    val formTotalSeats by viewModel.formTotalSeats.collectAsState()

    val formWiFi by viewModel.formWiFi.collectAsState()
    val formAC by viewModel.formAC.collectAsState()
    val formAttachedBath by viewModel.formAttachedBath.collectAsState()
    val formMealService by viewModel.formMealService.collectAsState()
    val formGym by viewModel.formGym.collectAsState()
    val formSecurity by viewModel.formSecurity.collectAsState()

    val formDescription by viewModel.formDescription.collectAsState()
    val formError by viewModel.formError.collectAsState()

    val citiesList = listOf("Dhaka", "Chattogram", "Sylhet", "Rajshahi", "Khulna", "Barishal", "Rangpur", "Mymensingh")
    val cityAreasMap = mapOf(
        "Dhaka" to listOf("Mirpur", "Dhanmondi", "Mohammadpur", "Uttara", "Badda", "Farmgate", "Khilgaon", "Banani"),
        "Chattogram" to listOf("GEC Circle", "Halishahar", "Panchlaish", "Agrabad", "Chawkbazar", "Nasirabad"),
        "Sylhet" to listOf("Zindabazar", "Uposhahar", "Amberkhana", "Shibgonj", "Pathantula"),
        "Rajshahi" to listOf("Shaheb Bazar", "Motihar", "Kazla", "Sopura"),
        "Khulna" to listOf("Khalishpur", "Boyra", "Sonadanga", "Shib Bari"),
        "Barishal" to listOf("Sadat Road", "Rupatali", "Natullabad"),
        "Rangpur" to listOf("Dhapi", "Modern More", "Medical More"),
        "Mymensingh" to listOf("Ganginarpar", "Charpara", "Valuka")
    )
    val areasList = cityAreasMap[formCity] ?: listOf("Mirpur", "Dhanmondi")

    // If user is not logged in or is a seeker, prompt them to login/register as an owner
    if (currentUser == null || currentUser!!.role != "owner") {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(WarmBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CoralAccent.copy(alpha = 0.25f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AddBusiness,
                        contentDescription = "Lock",
                        tint = CoralAccent,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Owner Access Required",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Only registered owners or sublet managers can list new bachelor mess units. Please log in or register as an Owner.",
                        fontSize = 14.sp,
                        color = TextLight,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TactileButton(
                        onClick = {
                            viewModel.isOwnerToggle.value = true
                            viewModel.setSignUpMode(false)
                            viewModel.navigateTo(Screen.AUTH)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("add_listing_auth_btn")
                    ) {
                        Text("Log In as Owner", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
    ) {
        // Top static header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "List Your Mess Unit",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TealPrimary
            )
        }

        // Progress Indicator Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TealPrimary.copy(alpha = 0.05f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(if (formStep >= 1) TealPrimary else Color.Gray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Details",
                    color = if (formStep >= 1) TealPrimary else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Divider(modifier = Modifier.width(30.dp), color = Color.LightGray)

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(if (formStep >= 2) TealPrimary else Color.Gray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Amenities & Desc",
                    color = if (formStep >= 2) TealPrimary else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Error message if any
        if (formError != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CoralAccent.copy(alpha = 0.1f))
                    .padding(vertical = 10.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = formError!!,
                    color = CoralAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Form fields scrollable area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (formStep == 1) {
                // STEP 1: Details
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                ) {
                    Text(
                        text = "1. Basic Specifications",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = formTitle,
                        onValueChange = { viewModel.formTitle.value = it },
                        label = { Text("Listing Title (e.g. Cozy Sublet Room)") },
                        leadingIcon = { Icon(Icons.Default.Title, contentDescription = null, tint = TealPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("form_title_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // City / Division Selector Dropdown
                    Column {
                        Text("Division / City", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Spacer(modifier = Modifier.height(4.dp))
                        var cityExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(
                                onClick = { cityExpanded = true },
                                modifier = Modifier.fillMaxWidth().testTag("form_city_dropdown"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(formCity, fontWeight = FontWeight.Bold, color = TealPrimary)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TealPrimary)
                            }
                            DropdownMenu(expanded = cityExpanded, onDismissRequest = { cityExpanded = false }) {
                                citiesList.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text(city) },
                                        onClick = {
                                            viewModel.formCity.value = city
                                            viewModel.formArea.value = cityAreasMap[city]?.firstOrNull() ?: ""
                                            cityExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Area selector dropdown
                    Column {
                        Text("Area / Zone", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Spacer(modifier = Modifier.height(4.dp))
                        var areaExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(
                                onClick = { areaExpanded = true },
                                modifier = Modifier.fillMaxWidth().testTag("form_area_dropdown"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(formArea, fontWeight = FontWeight.Bold, color = TealPrimary)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TealPrimary)
                            }
                            DropdownMenu(expanded = areaExpanded, onDismissRequest = { areaExpanded = false }) {
                                areasList.forEach { zone ->
                                    DropdownMenuItem(
                                        text = { Text(zone) },
                                        onClick = {
                                            viewModel.formArea.value = zone
                                            areaExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = formAddress,
                        onValueChange = { viewModel.formAddress.value = it },
                        label = { Text("Exact Address (Road, House, Flat no)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = TealPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("form_address_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = formPrice,
                        onValueChange = { viewModel.formPrice.value = it },
                        label = { Text("Monthly Rent (৳)") },
                        leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = TealPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("form_price_input")
                    )
                }

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                ) {
                    Text(
                        text = "2. Tenant & Bed Configurations",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Gender selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Gender Preference", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight)
                            Spacer(modifier = Modifier.height(4.dp))
                            val genders = listOf("male", "female", "family")
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier.fillMaxWidth().testTag("form_gender_btn"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(formGenderPreference.capitalize(), maxLines = 1)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    genders.forEach { g ->
                                        DropdownMenuItem(
                                            text = { Text(g.capitalize()) },
                                            onClick = {
                                                viewModel.formGenderPreference.value = g
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
                            val roomTypes = listOf("single", "shared")
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier.fillMaxWidth().testTag("form_room_type_btn"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(formRoomType.capitalize(), maxLines = 1)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    roomTypes.forEach { r ->
                                        DropdownMenuItem(
                                            text = { Text(r.capitalize()) },
                                            onClick = {
                                                viewModel.formRoomType.value = r
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = formSeatsAvailable,
                            onValueChange = { viewModel.formSeatsAvailable.value = it },
                            label = { Text("Seats Left") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = formTotalSeats,
                            onValueChange = { viewModel.formTotalSeats.value = it },
                            label = { Text("Total Seats") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // STEP 2: Amenities & Description
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                ) {
                    Text(
                        text = "3. Select Amenities Included",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            FilterCheckbox(label = "WiFi Internet", checked = formWiFi) { viewModel.formWiFi.value = it }
                            FilterCheckbox(label = "Attached Bath", checked = formAttachedBath) { viewModel.formAttachedBath.value = it }
                            FilterCheckbox(label = "Gym Access", checked = formGym) { viewModel.formGym.value = it }
                        }
                        Column {
                            FilterCheckbox(label = "Air Conditioner (AC)", checked = formAC) { viewModel.formAC.value = it }
                            FilterCheckbox(label = "Meal Facility", checked = formMealService) { viewModel.formMealService.value = it }
                            FilterCheckbox(label = "CCTV/Security Guard", checked = formSecurity) { viewModel.formSecurity.value = it }
                        }
                    }
                }

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                ) {
                    Text(
                        text = "4. Room Description",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = formDescription,
                        onValueChange = { viewModel.formDescription.value = it },
                        label = { Text("Detailed Description (Rent details, nearby locations, entry rules)") },
                        minLines = 5,
                        modifier = Modifier.fillMaxWidth().testTag("form_desc_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TealPrimary.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Note: Listing with correct information ensures high seeker response and builds verified safety on MessFinder Bangladesh.",
                            fontSize = 11.sp,
                            color = TealPrimary,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Navigation Buttons inside the scrollable content or fixed bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (formStep == 2) {
                    OutlinedButton(
                        onClick = { viewModel.setFormStep(1) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TactileButton(
                        onClick = { viewModel.submitListing() },
                        modifier = Modifier.weight(1.5f).testTag("form_submit_btn")
                    ) {
                        Text("Publish Listing", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    Spacer(modifier = Modifier.weight(0.5f))
                    TactileButton(
                        onClick = {
                            if (formTitle.isBlank() || formAddress.isBlank() || formPrice.isBlank()) {
                                viewModel.formError.value = "Please fill in title, address, and rent"
                            } else {
                                viewModel.formError.value = null
                                viewModel.setFormStep(2)
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("form_next_btn")
                    ) {
                        Text("Next Step", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
