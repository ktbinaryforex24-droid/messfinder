package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MessViewModel
import com.example.viewmodel.Screen

@Composable
fun AuthScreen(
    viewModel: MessViewModel,
    modifier: Modifier = Modifier
) {
    val isSignUpMode by viewModel.isSignUpMode.collectAsState()
    val isOwnerToggle by viewModel.isOwnerToggle.collectAsState() // false = seeker, true = owner

    val authUsername by viewModel.authUsername.collectAsState()
    val authEmail by viewModel.authEmail.collectAsState()
    val authPassword by viewModel.authPassword.collectAsState()
    val authPhone by viewModel.authPhone.collectAsState()
    val authError by viewModel.authError.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Identity Header
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "MessFinder BD Logo",
            modifier = Modifier
                .size(80.dp)
                .shadow(8.dp, RoundedCornerShape(20.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MessFinder BD",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TealPrimary
        )

        Text(
            text = "Bachelor Mess & Sublet Finder Platform",
            fontSize = 13.sp,
            color = TextLight,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Form Container Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.White
        ) {
            // Mode Select: Login vs Sign Up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isSignUpMode) TealPrimary else Color.Transparent)
                        .clickable { viewModel.setSignUpMode(false) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log In",
                        color = if (!isSignUpMode) Color.White else TextLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSignUpMode) TealPrimary else Color.Transparent)
                        .clickable { viewModel.setSignUpMode(true) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign Up",
                        color = if (isSignUpMode) Color.White else TextLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Role Selector: Seeker vs Owner
            Column {
                Text(
                    text = "Account Type",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TealPrimary.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isOwnerToggle) TealPrimary.copy(alpha = 0.08f) else Color.Transparent)
                            .border(
                                width = if (!isOwnerToggle) 1.dp else 0.dp,
                                color = if (!isOwnerToggle) TealPrimary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.isOwnerToggle.value = false }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = if (!isOwnerToggle) TealPrimary else TextLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Seeker",
                                color = if (!isOwnerToggle) TealPrimary else TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isOwnerToggle) TealPrimary.copy(alpha = 0.08f) else Color.Transparent)
                            .border(
                                width = if (isOwnerToggle) 1.dp else 0.dp,
                                color = if (isOwnerToggle) TealPrimary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.isOwnerToggle.value = true }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = if (isOwnerToggle) TealPrimary else TextLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Owner",
                                color = if (isOwnerToggle) TealPrimary else TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error Prompt
            if (authError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CoralAccent.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = authError!!,
                        color = CoralAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Input Fields
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AnimatedVisibility(visible = isSignUpMode) {
                    OutlinedTextField(
                        value = authUsername,
                        onValueChange = { viewModel.authUsername.value = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("auth_name_input"),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = authEmail,
                    onValueChange = { viewModel.authEmail.value = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TealPrimary) },
                    modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                AnimatedVisibility(visible = isSignUpMode) {
                    OutlinedTextField(
                        value = authPhone,
                        onValueChange = { viewModel.authPhone.value = it },
                        label = { Text("Phone Number (e.g. 01712...)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TealPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("auth_phone_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }

                OutlinedTextField(
                    value = authPassword,
                    onValueChange = { viewModel.authPassword.value = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TealPrimary) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().testTag("auth_pass_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            TactileButton(
                onClick = { viewModel.handleAuth() },
                modifier = Modifier.fillMaxWidth().testTag("auth_submit_btn")
            ) {
                Text(
                    text = if (isSignUpMode) "Create Account" else "Log In",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }
        }
    }
}
