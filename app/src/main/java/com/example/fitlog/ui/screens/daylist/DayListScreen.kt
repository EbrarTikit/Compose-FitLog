package com.example.fitlog.ui.screens.daylist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.ui.theme.LightPurple
import com.example.fitlog.ui.theme.LightPurple1
import com.example.fitlog.ui.theme.LightPurple4
import com.example.fitlog.ui.theme.PrimaryPurple
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun DayListScreen(
    initialDate: LocalDate = LocalDate.now(),
    workoutsForDate: (LocalDate) -> List<Pair<String, String>>,
    onSeeAllClick: (LocalDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var currentMonth by remember { mutableStateOf(YearMonth.from(initialDate)) }
    var showYearDialog by remember { mutableStateOf(false) }
    var showMonthDialog by remember { mutableStateOf(false) }

    val workouts = workoutsForDate(selectedDate)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonth = currentMonth.minusMonths(1)
                selectedDate = currentMonth.atDay(1)
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }

            TextButton(onClick = { showYearDialog = true }) {
                Text(
                    text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
                            " " + currentMonth.year,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            }

            IconButton(onClick = {
                currentMonth = currentMonth.plusMonths(1)
                selectedDate = currentMonth.atDay(1)
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        if (showYearDialog) {
            AlertDialog(
                onDismissRequest = { showYearDialog = false },
                confirmButton = {},
                title = { Text("Select Year") },
                text = {
                    Column {
                        (currentMonth.year - 5..currentMonth.year + 5).forEach { year ->
                            Text(
                                text = year.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentMonth = YearMonth.of(year, currentMonth.month)
                                        showYearDialog = false
                                        showMonthDialog = true
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            )
        }

        if (showMonthDialog) {
            AlertDialog(
                onDismissRequest = { showMonthDialog = false },
                confirmButton = {},
                title = { Text("Select Month") },
                text = {
                    Column {
                        Month.values().forEach { month ->
                            Text(
                                text = month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentMonth = YearMonth.of(currentMonth.year, month)
                                        selectedDate = currentMonth.atDay(1)
                                        showMonthDialog = false
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
        val dates = (1..daysInMonth).map { day -> currentMonth.atDay(day) }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DayOfWeek.values().forEach {
                Text(
                    text = it.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val weeks = mutableListOf<List<LocalDate?>>()
        var week = MutableList(7) { null as LocalDate? }
        var dayCounter = 0

        for (i in firstDayOfWeek until 7) {
            week[i] = dates[dayCounter++]
        }
        weeks.add(week.toList())

        while (dayCounter < dates.size) {
            week = MutableList(7) { null }
            for (i in 0 until 7) {
                if (dayCounter < dates.size) {
                    week[i] = dates[dayCounter++]
                }
            }
            weeks.add(week)
        }

        weeks.forEach { weekDates ->
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDates.forEach { date ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                if (date == selectedDate) LightPurple4
                                else Color.Transparent
                            )
                            .clickable(enabled = date != null) {
                                date?.let { selectedDate = it }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = date?.dayOfMonth?.toString() ?: "")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${workouts.size} workouts",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )

            TextButton(onClick = { onSeeAllClick(selectedDate) }) {
                Text("See all >>", color = PrimaryPurple)
            }
        }

        workouts.take(3).forEach { (name, sets) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = LightPurple)
            ) {
                Column(modifier = Modifier.padding(50.dp, 20.dp)) {
                    Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = sets, fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DayListScreenPreview() {
    DayListScreen(
        initialDate = LocalDate.now(),
        workoutsForDate = { date ->
            listOf(
                "Push-ups" to "3 sets",
                "Pull-ups" to "4 sets",
                "Squats" to "5 sets"
            )
        },
        onSeeAllClick = {}
    )
}