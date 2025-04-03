package com.example.fitlog.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.R
import com.example.fitlog.data.model.DailyPlan
import com.example.fitlog.ui.components.ButtonUI
import com.example.fitlog.ui.theme.LightPurple2
import com.example.fitlog.ui.theme.PrimaryPurple


@Composable
fun HomeScreen() {
}

//@Composable
//fun HomeScreen(
//    homeUiState: HomeUiState,
//    onCheckWorkoutClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Surface(
//        modifier = modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        when (homeUiState) {
//            is HomeUiState.Loading -> LoadingScreen()
//            is HomeUiState.Success -> HomeContent(
//                userName = homeUiState.userName,
//                currentDate = homeUiState.currentDate,
//                dailyPlan = homeUiState.dailyPlan,
//                activityStats = homeUiState.activityStats,
//                recentWorkouts = homeUiState.recentWorkouts,
//                onCheckWorkoutClick = onCheckWorkoutClick
//            )
//            is HomeUiState.Error -> ErrorScreen(homeUiState.message)
//        }
//    }
//}

@Composable
private fun HomeContent(
    userName: String,
    currentDate: String,
    dailyPlan: DailyPlan,
//    activityStats: ActivityStats,
//    recentWorkouts: List<WorkoutSummary>,
    onCheckWorkoutClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        UserGreetingSection(userName, currentDate)

        Spacer(modifier = Modifier.height(24.dp))

        DailyPlanSection(dailyPlan, onCheckWorkoutClick)

        Spacer(modifier = Modifier.height(24.dp))

        //ActivityStatusSection(activityStats)

        Spacer(modifier = Modifier.height(24.dp))

        //RecentWorkoutsSection(recentWorkouts)
    }
}

@Composable
fun DailyPlanSection(dailyPlan: DailyPlan, onCheckWorkoutClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = "My Plan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = dailyPlan.day,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightPurple2.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_energy),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = dailyPlan.workoutType,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Text(
                            text =  "${dailyPlan.dayOfWeek} of week",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }

                Button(
                    onClick = { onCheckWorkoutClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple.copy(alpha = 0.9f)
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .fillMaxWidth(0.85f),
                    shape = RoundedCornerShape(20.dp), // Tam yuvarlak buton
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Check Workout",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color.White
                    )
                }

            }
        }

    }
}

@Composable
fun UserGreetingSection(
    userName: String,
    currentDate: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(60.dp),
                    alignment = Alignment.Center
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "Hello, $userName!",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = currentDate,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        IconButton(
            onClick = {},
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserGreetingSectionPreview() {
    UserGreetingSection(userName = "John Doe", currentDate = "Thursday, 08 July")
}

@Preview(showBackground = true)
@Composable
fun DailyPlanSectionPreview() {
    DailyPlanSection(dailyPlan = DailyPlan("Monday", "Upper Body", "2"), onCheckWorkoutClick = {})
}
