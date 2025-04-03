package com.example.fitlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitlog.ui.theme.LightPurple5


@Composable
fun PageIndicator(
    pageSize: Int,
    currentPage: Int,
    selectedColor: Color = LightPurple5,
    unselectedColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(pageSize) {
            Spacer(modifier = Modifier.size(2.5.dp))
            Box(
                modifier = Modifier
                    .height(12.dp)
                    .width(if (it == currentPage) 26.dp else 12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = if (it == currentPage) selectedColor else unselectedColor)
            )
            Spacer(modifier = Modifier.size(2.5.dp))

        }
    }
}

@Preview
@Composable
fun PageIndicatorPreview() {
    PageIndicator(
        pageSize = 3,
        currentPage = 1,
    )
}