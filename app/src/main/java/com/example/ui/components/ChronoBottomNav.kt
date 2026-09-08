package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChronoTab
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun ChronoBottomNav(
    currentTab: ChronoTab,
    onTabSelected: (ChronoTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest.copy(alpha = 0.95f))
            .navigationBarsPadding()
            .height(72.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            tab = ChronoTab.TODAY_GRID,
            label = "Today Grid",
            icon = Icons.Default.CalendarToday,
            isSelected = currentTab == ChronoTab.TODAY_GRID,
            onClick = { onTabSelected(ChronoTab.TODAY_GRID) },
            testTag = "nav_today_grid"
        )

        BottomNavItem(
            tab = ChronoTab.GOALS_90,
            label = "90-Day Goals",
            icon = Icons.Default.Flag,
            isSelected = currentTab == ChronoTab.GOALS_90,
            onClick = { onTabSelected(ChronoTab.GOALS_90) },
            testTag = "nav_goals_90"
        )

        BottomNavItem(
            tab = ChronoTab.ANALYTICS,
            label = "Analytics",
            icon = Icons.Default.Analytics,
            isSelected = currentTab == ChronoTab.ANALYTICS,
            onClick = { onTabSelected(ChronoTab.ANALYTICS) },
            testTag = "nav_analytics"
        )
    }
}

@Composable
fun BottomNavItem(
    tab: ChronoTab,
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(if (isSelected) Primary.copy(alpha = 0.20f) else androidx.compose.ui.graphics.Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Primary else OnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = label,
            color = if (isSelected) OnSurface else OnSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
