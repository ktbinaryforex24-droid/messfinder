package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.MessRepository
import com.example.data.User
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalConfiguration
import com.example.R
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarmBackground
import com.example.ui.theme.CoralAccent
import com.example.viewmodel.MessViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Room DB & Repository
        val database = AppDatabase.getDatabase(this)
        val repository = MessRepository(database.messDao())

        // 2. Initialize ViewModel with Factory
        val viewModel = ViewModelProvider(
            this,
            MessViewModel.provideFactory(application, repository)
        )[MessViewModel::class.java]

        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val currentUser by viewModel.currentUser.collectAsState()
                val configuration = LocalConfiguration.current
                val isDesktop = configuration.screenWidthDp >= 600

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (!isDesktop) {
                            // Persistent Glassmorphic Floating Bottom Bar
                            GlassFloatingBottomBar(
                                currentScreen = currentScreen,
                                currentUser = currentUser,
                                onTabSelected = { screen ->
                                    viewModel.navigateTo(screen)
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    val contentPadding = if (isDesktop) PaddingValues(0.dp) else innerPadding

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(WarmBackground)
                    ) {
                        if (isDesktop) {
                            GlassSideNavigationBar(
                                currentScreen = currentScreen,
                                currentUser = currentUser,
                                onTabSelected = { screen ->
                                    viewModel.navigateTo(screen)
                                }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            // Smooth Screen Crossfade Transitions
                            Crossfade(
                                targetState = currentScreen,
                                label = "screen_transition"
                            ) { screen ->
                                when (screen) {
                                    Screen.HOME -> HomeScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(contentPadding)
                                    )
                                    Screen.DETAIL -> DetailScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(contentPadding)
                                    )
                                    Screen.ADD_LISTING -> AddListingScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(contentPadding)
                                    )
                                    Screen.DASHBOARD -> DashboardScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(contentPadding)
                                    )
                                    Screen.AUTH -> AuthScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(contentPadding)
                                    )
                                    Screen.SAVED -> SavedScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(contentPadding)
                                    )
                                    Screen.MESSAGES -> MessagesScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(contentPadding)
                                    )
                                    Screen.NOTIFICATIONS -> NotificationsScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(contentPadding)
                                    )
                                    Screen.COMPARE -> CompareScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(contentPadding)
                                    )
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
fun GlassSideNavigationBar(
    currentScreen: Screen,
    currentUser: User?,
    onTabSelected: (Screen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(260.dp)
            .background(Color.White)
            .border(
                width = 1.dp,
                color = TealPrimary.copy(alpha = 0.1f)
            )
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top: Brand Identity / Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_no_bg),
                contentDescription = "MessFinder BD Logo",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "MessFinder BD",
                color = TealPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Navigation Items
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SideBarTabItem(
                label = "Explore",
                iconSelected = Icons.Filled.Search,
                iconUnselected = Icons.Outlined.Search,
                isSelected = currentScreen == Screen.HOME || currentScreen == Screen.DETAIL || currentScreen == Screen.COMPARE,
                onClick = { onTabSelected(Screen.HOME) },
                testTag = "sidebar_explore"
            )

            SideBarTabItem(
                label = "Saved",
                iconSelected = Icons.Filled.Favorite,
                iconUnselected = Icons.Outlined.FavoriteBorder,
                isSelected = currentScreen == Screen.SAVED,
                onClick = { onTabSelected(Screen.SAVED) },
                testTag = "sidebar_saved"
            )

            SideBarTabItem(
                label = "Messages",
                iconSelected = Icons.Filled.Chat,
                iconUnselected = Icons.Outlined.Chat,
                isSelected = currentScreen == Screen.MESSAGES,
                onClick = { onTabSelected(Screen.MESSAGES) },
                testTag = "sidebar_messages"
            )

            val showAccountLabel = currentUser != null
            val dashboardTarget = if (currentUser == null) Screen.AUTH else Screen.DASHBOARD
            SideBarTabItem(
                label = if (showAccountLabel) "Dashboard" else "Account",
                iconSelected = if (showAccountLabel) Icons.Filled.Dashboard else Icons.Filled.AccountCircle,
                iconUnselected = if (showAccountLabel) Icons.Outlined.Dashboard else Icons.Outlined.AccountCircle,
                isSelected = currentScreen == Screen.DASHBOARD || currentScreen == Screen.AUTH,
                onClick = { onTabSelected(dashboardTarget) },
                testTag = "sidebar_dashboard"
            )
        }

        // Bottom: Add Listing Button (represented beautifully as a full width button)
        TactileButton(
            onClick = { onTabSelected(Screen.ADD_LISTING) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("sidebar_list_mess"),
            containerColor = CoralAccent
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "List Your Mess",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SideBarTabItem(
    label: String,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = if (isSelected) TealPrimary.copy(alpha = 0.08f) else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSelected) iconSelected else iconUnselected,
            contentDescription = label,
            tint = if (isSelected) TealPrimary else Color.Gray.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) TealPrimary else Color.Gray
        )
    }
}

@Composable
fun GlassFloatingBottomBar(
    currentScreen: Screen,
    currentUser: User?,
    onTabSelected: (Screen) -> Unit
) {
    // Standard M3 Bottom Navigation styled as a floating glass card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false,
                ambientColor = TealPrimary.copy(alpha = 0.2f),
                spotColor = TealPrimary.copy(alpha = 0.3f)
            )
            .background(
                color = Color.White.copy(alpha = 0.92f), // High opacity for perfect readability
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = TealPrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Explore (Home)
            BottomTabItem(
                label = "Explore",
                iconSelected = Icons.Filled.Search,
                iconUnselected = Icons.Outlined.Search,
                isSelected = currentScreen == Screen.HOME || currentScreen == Screen.DETAIL || currentScreen == Screen.COMPARE,
                onClick = { onTabSelected(Screen.HOME) },
                testTag = "tab_explore"
            )

            // Tab 2: Saved (Heart)
            BottomTabItem(
                label = "Saved",
                iconSelected = Icons.Filled.Favorite,
                iconUnselected = Icons.Outlined.FavoriteBorder,
                isSelected = currentScreen == Screen.SAVED,
                onClick = { onTabSelected(Screen.SAVED) },
                testTag = "tab_saved"
            )

            // Tab 3: Elevated Center Add Button
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = (-14).dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = CoralAccent.copy(alpha = 0.4f),
                            spotColor = CoralAccent.copy(alpha = 0.5f)
                        )
                        .background(CoralAccent, CircleShape)
                        .size(48.dp)
                        .clickable { onTabSelected(Screen.ADD_LISTING) }
                        .testTag("tab_list_mess"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "List Mess",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Tab 4: Messages (Chat bubble)
            BottomTabItem(
                label = "Messages",
                iconSelected = Icons.Filled.Chat,
                iconUnselected = Icons.Outlined.Chat,
                isSelected = currentScreen == Screen.MESSAGES,
                onClick = { onTabSelected(Screen.MESSAGES) },
                testTag = "tab_messages"
            )

            // Tab 5: Account/Dashboard
            val showAccountLabel = currentUser != null
            val dashboardTarget = if (currentUser == null) Screen.AUTH else Screen.DASHBOARD
            
            BottomTabItem(
                label = if (showAccountLabel) "Dashboard" else "Account",
                iconSelected = if (showAccountLabel) Icons.Filled.Dashboard else Icons.Filled.AccountCircle,
                iconUnselected = if (showAccountLabel) Icons.Outlined.Dashboard else Icons.Outlined.AccountCircle,
                isSelected = currentScreen == Screen.DASHBOARD || currentScreen == Screen.AUTH,
                onClick = { onTabSelected(dashboardTarget) },
                testTag = "tab_dashboard"
            )
        }
    }
}

@Composable
fun RowScope.BottomTabItem(
    label: String,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
            .testTag(testTag)
    ) {
        // Active indicator capsule
        Box(
            modifier = Modifier
                .background(
                    color = if (isSelected) TealPrimary.copy(alpha = 0.1f) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) iconSelected else iconUnselected,
                contentDescription = label,
                tint = if (isSelected) TealPrimary else Color.Gray.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isSelected) TealPrimary else Color.Gray
        )
    }
}
