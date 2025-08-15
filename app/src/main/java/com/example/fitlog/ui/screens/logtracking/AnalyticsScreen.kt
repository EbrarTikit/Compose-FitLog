package com.example.fitlog.ui.screens.logtracking

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.LaunchedEffect
import kotlin.math.absoluteValue

@Composable
fun AnalyticsScreen(onBack: (() -> Unit)? = null) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Weight, 1: Reps
    var exerciseNames by remember { mutableStateOf(listOf<String>()) }
    var selectedChip by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // Load distinct exercise names for this user: users/{uid}/workouts/*/exercises/*
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val db = Firebase.firestore
            db.collection("users").document(uid).collection("workouts").get()
                .addOnSuccessListener { workoutsSnap ->
                    if (workoutsSnap.isEmpty) return@addOnSuccessListener
                    val acc = mutableSetOf<String>()
                    var pending = workoutsSnap.size()
                    workoutsSnap.documents.forEach { wDoc ->
                        wDoc.reference.collection("exercises").get()
                            .addOnSuccessListener { exSnap ->
                                exSnap.documents.forEach { d ->
                                    d.getString("name")?.let { acc.add(it) }
                                }
                            }
                            .addOnCompleteListener {
                                pending -= 1
                                if (pending <= 0) {
                                    exerciseNames = acc.toList().sorted()
                                }
                            }
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
            Text(
                text = "Analytics",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Tabs
        val tabs = listOf("Weight Lifted", "Reps Completed")
        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTab == index) Color(0xFF3A6FF8) else Color(
                                0xFF6B7280
                            ),
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Exercise chips (dynamic)
        val chipItems = listOf("All") + exerciseNames
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chipItems.withIndex().toList(), key = { it.index }) { indexed ->
                val idx = indexed.index
                val label = indexed.value
                val selected = idx == selectedChip
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) Color(0xFFE5ECFF) else Color(0xFFF3F4F6)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { selectedChip = idx },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (selected) Color(0xFF3A6FF8) else Color(0xFF374151),
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Summary + Chart card
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (selectedTab == 0) "Total Weight Lifted" else "Total Reps Completed",
                    color = Color(0xFF6B7280),
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(6.dp))
                // Derive stable demo values from exercise name if one selected
                val selName = chipItems.getOrNull(selectedChip)?.lowercase() ?: ""
                fun stableNum(seed: String, base: Int, range: Int) =
                    (seed.hashCode().absoluteValue % range) + base

                val baseWeight = if (selectedChip == 0) 12500 else stableNum(selName, 700, 900)
                val baseReps = if (selectedChip == 0) 3240 else stableNum(selName, 180, 220)
                val valueText = if (selectedTab == 0) "%s lbs".format(
                    if (selectedChip == 0) "12,500" else baseWeight.toString()
                ) else "%s reps".format(
                    if (selectedChip == 0) "3,240" else baseReps.toString()
                )
                Text(
                    text = valueText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF111827)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Last 30 Days +15%",
                    color = Color(0xFF10B981),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(12.dp))

                // Line chart
                val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                // Build pseudo data based on selection for demo purposes
                val seed = (chipItems.getOrNull(selectedChip) ?: "all").hashCode().absoluteValue
                fun wave(offset: Int) =
                    List(7) { i -> ((30 + ((i + offset + seed) * 13) % 50)).toFloat() }

                val data = if (selectedTab == 0) wave(3) else wave(1)
                LineChartSimple(data = data, labels = labels, strokeColor = Color(0xFF3A6FF8))
            }
        }
    }
}

@Composable
private fun LineChartSimple(data: List<Float>, labels: List<String>, strokeColor: Color) {
    Column(Modifier.fillMaxWidth()) {
        val gridColor = Color(0xFFE5E7EB)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Canvas(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                val left = 8f
                val right = size.width - 8f
                val top = 8f
                val bottom = size.height - 24f
                val width = right - left
                val height = bottom - top

                // grid lines
                repeat(4) { i ->
                    val y = top + height * i / 3f
                    drawLine(gridColor, Offset(left, y), Offset(right, y), strokeWidth = 1f)
                }

                // smooth path
                fun yPos(v: Float): Float {
                    val clamped = v.coerceIn(0f, 100f) / 100f
                    return top + (1f - clamped) * height
                }

                fun xPos(i: Int): Float {
                    if (data.size <= 1) return left
                    val step = width / (data.size - 1)
                    return left + step * i
                }

                val points = data.mapIndexed { i, v -> Offset(xPos(i), yPos(v)) }
                if (points.isNotEmpty()) {
                    val p = Path().apply { moveTo(points.first().x, points.first().y) }
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val c1 = Offset((prev.x + curr.x) / 2f, prev.y)
                        val c2 = Offset((prev.x + curr.x) / 2f, curr.y)
                        p.cubicTo(c1.x, c1.y, c2.x, c2.y, curr.x, curr.y)
                    }
                    drawPath(p, color = strokeColor, style = Stroke(width = 5f))
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            labels.forEach { Text(it, color = Color(0xFF6B7280), fontSize = 12.sp) }
        }
    }
}

@Composable
private fun BreakdownItem(name: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
        }
    }
}
