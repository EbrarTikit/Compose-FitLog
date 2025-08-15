package com.example.fitlog.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.Instant
import java.time.ZoneId

@Composable
fun ProfileScreen() {
    val user = remember { FirebaseAuth.getInstance().currentUser }
    val displayName = user?.displayName ?: user?.email ?: "User"
    val joined = remember(user) {
        user?.metadata?.creationTimestamp?.let {
            val year = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).year
            "Joined $year"
        } ?: ""
    }
    var workoutCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        Firebase.firestore.collection("users").document(uid).collection("workouts")
            .get()
            .addOnSuccessListener { snap -> workoutCount = snap.size() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /* open drawer */ }) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
            Text("Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = { /* settings */ }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Avatar + Name + Subtitle
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF2F2F7)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                displayName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            if (joined.isNotEmpty()) Text(joined, fontSize = 13.sp, color = Color(0xFF3B82F6))
        }

        Spacer(Modifier.height(16.dp))

        // Big stat card
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "$workoutCount",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF111827)
                )
                Text("Workouts", color = Color(0xFF6B7280), fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Activity
        SectionHeader("Activity")
        SettingsRow(icon = Icons.Default.History, label = "Workout History") { /* open history */ }

        Spacer(Modifier.height(16.dp))

        // Settings
        SectionHeader("Settings")
        SettingsRow(icon = Icons.Default.ManageAccounts, label = "Account Settings") {}
        SettingsRow(icon = Icons.Default.Lock, label = "Password") {}
        SettingsRow(icon = Icons.Default.PrivacyTip, label = "Privacy Settings") {}
        SettingsRow(icon = Icons.Default.Brightness6, label = "Theme") {}
        SettingsRow(icon = Icons.Default.Notifications, label = "Notifications") {}
        SettingsRow(icon = Icons.Default.Apps, label = "App Settings") {}

        Spacer(Modifier.height(16.dp))

        // Help & Support
        SectionHeader("Help & Support")
        SettingsRow(icon = Icons.Default.HelpOutline, label = "FAQ") {}
        SettingsRow(icon = Icons.Default.Email, label = "Contact Support") {}

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { FirebaseAuth.getInstance().signOut() },
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C5CFA),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) { Text("Sign Out", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
}

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF3F4F6)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color(0xFF374151)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(label, modifier = Modifier.weight(1f), color = Color(0xFF111827))
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = Color(0xFF9CA3AF)
            )
        }
    }
}