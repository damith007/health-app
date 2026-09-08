package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SurfaceBright
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TertiaryContainer
import com.example.ui.theme.TertiaryCyan

@Composable
fun AnalyticsScreen(
    selectedRange: String,
    analyticsData: com.example.data.model.RealtimeAnalytics = com.example.data.model.RealtimeAnalytics(),
    analysisCards: List<com.example.ui.components.DeepAnalysisCard> = emptyList(),
    onRangeChange: (String) -> Unit,
    onFilterClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Range Filter
        item {
            AnalyticsHeaderSection(
                selectedRange = selectedRange,
                analyticsData = analyticsData,
                onRangeChange = onRangeChange,
                onFilterClick = onFilterClick,
                onExportClick = onExportClick
            )
        }

        // 2. 4-Tile High Impact KPI Matrix
        item {
            KpiMatrixSection(analyticsData)
        }

        // 3. Circadian Chronotype Heatmap
        item {
            CircadianHeatmapSection(analyticsData)
        }

        // 4. Category Time Allocation Breakdown
        item {
            CategoryAllocationSection(analyticsData)
        }

        // 5. Friction & Leakage Audit
        item {
            FrictionAuditSection(analyticsData)
        }

        // 6. 90-Day Trajectory Forecast
        item {
            TrajectoryForecastSection(analyticsData)
        }

        // 7. Deep Analysis & AI Recommendations
        item {
            com.example.ui.components.DeepAnalysisSection(analysisCards = analysisCards)
        }

        // 8. Footer Engine Badge
        item {
            FooterBadgeSection()
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
fun AnalyticsHeaderSection(
    selectedRange: String,
    analyticsData: com.example.data.model.RealtimeAnalytics = com.example.data.model.RealtimeAnalytics(),
    onRangeChange: (String) -> Unit,
    onFilterClick: () -> Unit = {},
    onExportClick: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Focus Analytics",
                        color = OnSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(SecondaryContainer.copy(alpha = 0.20f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SPRINT 1",
                            color = SecondaryGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "Day ${analyticsData.currentSprintDay} of ${analyticsData.totalSprintDays} • Sprint Horizon",
                    color = OnSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHigh)
                        .clickable { onFilterClick() }
                        .testTag("filter_metrics_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filter Metrics",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHigh)
                        .clickable { onExportClick() }
                        .testTag("export_report_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.IosShare,
                        contentDescription = "Export Report",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Segmented Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLowest)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("7 Days", "30 Days", "90D Sprint").forEach { range ->
                val isSelected = selectedRange == range
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Primary else Color.Transparent)
                        .clickable { onRangeChange(range) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = range,
                        color = if (isSelected) OnPrimary else OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun KpiMatrixSection(analyticsData: com.example.data.model.RealtimeAnalytics) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // KPI 1: Deep Focus Logged
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer)
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryContainer.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(SecondaryContainer.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "+12%",
                                color = SecondaryGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "FOCUS LOGGED",
                        color = OnSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    val focusHoursFormatted = if (analyticsData.focusLoggedHours % 1f == 0f) "${analyticsData.focusLoggedHours.toInt()}" else String.format(java.util.Locale.US, "%.1f", analyticsData.focusLoggedHours)
                    val targetHoursFormatted = if (analyticsData.targetFocusHours % 1f == 0f) "${analyticsData.targetFocusHours.toInt()}h" else String.format(java.util.Locale.US, "%.1fh", analyticsData.targetFocusHours)

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = focusHoursFormatted,
                            color = OnSurface,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "/ $targetHoursFormatted",
                            color = OnSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Progress Track
                    val focusProgress = if (analyticsData.targetFocusHours > 0) (analyticsData.focusLoggedHours / analyticsData.targetFocusHours).coerceIn(0f, 1f) else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(SurfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(focusProgress)
                                .fillMaxHeight()
                                .background(Primary)
                        )
                    }
                }
            }

            // KPI 2: Execution Rate
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer)
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SecondaryGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = SecondaryGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = SecondaryGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = analyticsData.executionRateDelta,
                                color = SecondaryGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "EXECUTION RATE",
                        color = OnSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    val execFormatted = String.format(java.util.Locale.US, "%.1f%%", analyticsData.executionRatePct)
                    val execRating = when {
                        analyticsData.executionRatePct >= 80f -> "Elite"
                        analyticsData.executionRatePct >= 65f -> "Solid"
                        else -> "Needs Focus"
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = execFormatted,
                            color = OnSurface,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = execRating,
                            color = SecondaryGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Sparkline
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    ) {
                        val path = Path().apply {
                            moveTo(0f, size.height * 0.8f)
                            lineTo(size.width * 0.2f, size.height * 0.6f)
                            lineTo(size.width * 0.4f, size.height * 0.9f)
                            lineTo(size.width * 0.6f, size.height * 0.4f)
                            lineTo(size.width * 0.8f, size.height * 0.5f)
                            lineTo(size.width, size.height * 0.1f)
                        }
                        drawPath(
                            path = path,
                            color = SecondaryGreen,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // KPI 3: Focus Efficiency
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer)
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TertiaryContainer.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = TertiaryCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(TertiaryContainer.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Deep vs Gap",
                                color = TertiaryCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "FOCUS EFFICIENCY",
                        color = OnSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "${analyticsData.focusEfficiencyPct}%",
                            color = OnSurface,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "purity",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Segmented line
                    val purityWeight = (analyticsData.focusEfficiencyPct / 100f).coerceIn(0.05f, 1f)
                    val remainingWeight = (1f - purityWeight).coerceAtLeast(0.01f)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(SurfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(purityWeight)
                                .fillMaxHeight()
                                .background(TertiaryCyan)
                        )
                        Box(
                            modifier = Modifier
                                .weight(remainingWeight)
                                .fillMaxHeight()
                                .background(OutlineVariant)
                        )
                    }
                }
            }

            // KPI 4: Unplanned Time
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer)
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ErrorContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = ErrorRose,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(SecondaryContainer.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${analyticsData.unplannedIncidents} gaps",
                                color = if (analyticsData.unplannedIncidents == 0) SecondaryGreen else ErrorRose,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "UNPLANNED TIME",
                        color = OnSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = analyticsData.unplannedHoursStr,
                        color = if (analyticsData.unplannedIncidents == 0) SecondaryGreen else ErrorRose,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SecondaryGreen)
                        )
                        Text(
                            text = "Target under 5h",
                            color = SecondaryGreen,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircadianHeatmapSection(analyticsData: com.example.data.model.RealtimeAnalytics) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Circadian Chronotype Heatmap",
                            color = OnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = TertiaryCyan,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Text(
                        text = "Average productivity density by hour (06:00 - 23:00)",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Primary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = analyticsData.chronotypeName,
                        color = Primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Peak Window Callout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerHigh)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = OnPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Peak Window: ${analyticsData.peakFocusWindow}",
                            color = OnSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(SecondaryContainer.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${analyticsData.peakFocusPercent}%",
                                color = SecondaryGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Optimal architecture and system design output",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Grid of hours from real hourly heat data
            val defaultHourDensities = listOf(
                "06:00" to 42, "07:00" to 58, "08:00" to 74, "09:00" to 96, "10:00" to 94, "11:00" to 92,
                "12:00" to 81, "13:00" to 48, "14:00" to 68, "15:00" to 78, "16:00" to 70, "17:00" to 52,
                "18:00" to 65, "19:00" to 72, "20:00" to 84, "21:00" to 59, "22:00" to 35, "23:00" to 20
            )
            val hourlyPairs = if (analyticsData.hourlyHeatmap.isNotEmpty()) {
                analyticsData.hourlyHeatmap.map { it.hourLabel to it.densityPercent }
            } else {
                defaultHourDensities
            }

            val row1 = hourlyPairs.take(6)
            val row2 = hourlyPairs.drop(6).take(6)
            val row3 = hourlyPairs.drop(12).take(6)

            if (row1.isNotEmpty()) HeatmapRow(row1)
            if (row2.isNotEmpty()) HeatmapRow(row2)
            if (row3.isNotEmpty()) HeatmapRow(row3)

            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(Primary, RoundedCornerShape(2.dp)))
                    Text("Flow (>90%)", color = OnSurfaceVariant, fontSize = 10.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(SecondaryContainer, RoundedCornerShape(2.dp)))
                    Text("Deep (70-89%)", color = OnSurfaceVariant, fontSize = 10.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(SurfaceContainerHighest, RoundedCornerShape(2.dp)))
                    Text("Shallow (<50%)", color = OnSurfaceVariant, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun HeatmapRow(items: List<Pair<String, Int>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.forEach { (hour, pct) ->
            val (bgColor, textColor) = when {
                pct >= 90 -> Primary to OnPrimary
                pct >= 70 -> SecondaryContainer.copy(alpha = 0.5f) to SecondaryGreen
                pct >= 60 -> TertiaryContainer.copy(alpha = 0.4f) to TertiaryCyan
                else -> SurfaceContainerHighest to OnSurfaceVariant
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$pct%",
                        color = textColor,
                        fontSize = 10.sp,
                        fontWeight = if (pct >= 90) FontWeight.Bold else FontWeight.Medium
                    )
                }
                Text(
                    text = hour,
                    color = if (pct >= 90) Primary else OnSurfaceVariant,
                    fontSize = 9.sp,
                    fontWeight = if (pct >= 90) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun CategoryAllocationSection(analyticsData: com.example.data.model.RealtimeAnalytics) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Category Allocation",
                        color = OnSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val loggedFormatted = String.format(java.util.Locale.US, "%.1f", analyticsData.focusLoggedHours)
                    val budgetFormatted = String.format(java.util.Locale.US, "%.1f", analyticsData.sprintTargetHours)
                    Text(
                        text = "$loggedFormatted logged of ${budgetFormatted}h sprint budget",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SecondaryContainer.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "+3.2h Delta",
                        color = SecondaryGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Multi-tone distribution bar from categories
            val categories = analyticsData.categoryBreakdowns
            if (categories.isNotEmpty()) {
                val totalPercent = categories.sumOf { it.percentage.toDouble() }.toFloat().coerceAtLeast(1f)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SurfaceContainerHighest)
                ) {
                    categories.forEach { cat ->
                        val weight = (cat.percentage / totalPercent).coerceAtLeast(0.02f)
                        Box(
                            modifier = Modifier
                                .weight(weight)
                                .fillMaxHeight()
                                .background(cat.dotColor)
                        )
                    }
                }

                // Category detail items
                categories.forEach { cat ->
                    CategoryItem(
                        dotColor = cat.dotColor,
                        title = cat.name,
                        hours = cat.hoursFormatted,
                        pct = "(${cat.percentage}%)",
                        status = cat.status,
                        budget = cat.budget,
                        isPositive = cat.isPositive,
                        isError = cat.isError
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    dotColor: Color,
    title: String,
    hours: String,
    pct: String,
    status: String,
    budget: String,
    isPositive: Boolean = false,
    isError: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isError) ErrorContainer.copy(alpha = 0.15f) else SurfaceContainerLow)
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(dotColor))
                    Text(
                        text = title,
                        color = if (isError) ErrorRose else OnSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = hours,
                        color = if (isError) ErrorRose else OnSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pct,
                        color = if (isError) ErrorRose.copy(alpha = 0.8f) else OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = status,
                    color = when {
                        isError -> ErrorRose
                        isPositive -> SecondaryGreen
                        else -> OnSurfaceVariant
                    },
                    fontSize = 10.sp,
                    fontWeight = if (isPositive || isError) FontWeight.SemiBold else FontWeight.Normal
                )
                Text(
                    text = budget,
                    color = OnSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun FrictionAuditSection(analyticsData: com.example.data.model.RealtimeAnalytics) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = ErrorRose,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Friction & Leakage Audit",
                        color = OnSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                val frictionImpact = if (analyticsData.unplannedIncidents > 0) "-${(analyticsData.unplannedHours * 3).toInt()}% Output" else "0% Leak"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (analyticsData.unplannedIncidents > 0) ErrorContainer.copy(alpha = 0.3f) else SecondaryContainer.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = frictionImpact,
                        color = if (analyticsData.unplannedIncidents > 0) ErrorRose else SecondaryGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Time Leak
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "TOP TIME LEAK",
                            color = OnSurfaceVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = analyticsData.topTimeLeakTitle,
                            color = OnSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${analyticsData.unplannedHoursStr} (${analyticsData.unplannedIncidents} incidents)",
                            color = if (analyticsData.unplannedIncidents > 0) ErrorRose else SecondaryGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (analyticsData.unplannedIncidents > 0) "Post-lunch trigger" else "Zero leaks recorded",
                            color = OnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                // Context Switches
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "CONTEXT SWITCHES",
                            color = OnSurfaceVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val contextSwitchStr = String.format(java.util.Locale.US, "%.1f", analyticsData.contextSwitchesPerDay)
                            Text(
                                text = contextSwitchStr,
                                color = OnSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "/ day",
                                color = OnSurfaceVariant,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = if (analyticsData.contextSwitchesPerDay <= 3.0f) "Within Target" else "Target < 3.0",
                            color = if (analyticsData.contextSwitchesPerDay <= 3.0f) SecondaryGreen else ErrorRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Down from 6.8",
                            color = SecondaryGreen,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Leverage Pattern Callout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryContainer.copy(alpha = 0.15f))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = OnPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "Leverage Pattern: Work blocks >90m with zero tab switching yielded 3.2x higher feature velocity this week.",
                    color = OnSurface,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun TrajectoryForecastSection(analyticsData: com.example.data.model.RealtimeAnalytics) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "90-Day Trajectory Forecast",
                        color = OnSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Paced for early completion at Day ${analyticsData.projectedCompletionDay}",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SecondaryContainer.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SecondaryGreen))
                        Text(
                            text = "${analyticsData.daysAheadOfSchedule} Days Ahead",
                            color = SecondaryGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Trajectory Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLow)
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid lines
                    drawLine(OutlineVariant.copy(alpha = 0.3f), Offset(0f, h * 0.25f), Offset(w, h * 0.25f), 1f)
                    drawLine(OutlineVariant.copy(alpha = 0.3f), Offset(0f, h * 0.5f), Offset(w, h * 0.5f), 1f)
                    drawLine(OutlineVariant.copy(alpha = 0.3f), Offset(0f, h * 0.75f), Offset(w, h * 0.75f), 1f)

                    // Target Pace (dashed line)
                    val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    drawLine(
                        color = OutlineVariant,
                        start = Offset(10f, h * 0.9f),
                        end = Offset(w - 10f, h * 0.15f),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = dashedEffect
                    )

                    // Actual Pace (solid violet curve)
                    val actualPath = Path().apply {
                        moveTo(10f, h * 0.9f)
                        quadraticTo(w * 0.25f, h * 0.8f, w * 0.45f, h * 0.5f)
                    }
                    drawPath(
                        path = actualPath,
                        color = Primary,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Forecast trend (dashed emerald)
                    val forecastPath = Path().apply {
                        moveTo(w * 0.45f, h * 0.5f)
                        lineTo(w * 0.88f, h * 0.12f)
                    }
                    drawPath(
                        path = forecastPath,
                        color = SecondaryGreen,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, pathEffect = dashedEffect)
                    )

                    // Current Point Marker
                    drawCircle(color = SecondaryGreen.copy(alpha = 0.3f), radius = 9.dp.toPx(), center = Offset(w * 0.45f, h * 0.5f))
                    drawCircle(color = SecondaryGreen, radius = 4.5.dp.toPx(), center = Offset(w * 0.45f, h * 0.5f))

                    // Projected completion point
                    drawCircle(color = TertiaryCyan, radius = 3.5.dp.toPx(), center = Offset(w * 0.88f, h * 0.12f))
                }
            }

            // Trajectory Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Day 1 (Start)", color = OnSurfaceVariant, fontSize = 9.sp)
                Text("Day ${analyticsData.currentSprintDay} (Now)", color = SecondaryGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("Day ${analyticsData.projectedCompletionDay} (Est.)", color = TertiaryCyan, fontSize = 9.sp)
                Text("Day ${analyticsData.totalSprintDays}", color = OnSurfaceVariant, fontSize = 9.sp)
            }

            // Bottom 3 Metric Columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Logged to Date", color = OnSurfaceVariant, fontSize = 10.sp)
                    val loggedTotal = String.format(java.util.Locale.US, "%.1fh", analyticsData.focusLoggedHours)
                    Text(loggedTotal, color = OnSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Target Sprint", color = OnSurfaceVariant, fontSize = 10.sp)
                    val targetTotal = String.format(java.util.Locale.US, "%.1fh", analyticsData.sprintTargetHours)
                    Text(targetTotal, color = OnSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Forecast Finish", color = OnSurfaceVariant, fontSize = 10.sp)
                    Text(analyticsData.forecastFinishDate, color = SecondaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FooterBadgeSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_chrono90_logo),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "DTRACK PRECISION ENGINE • SPRINT VERIFIED",
            color = OnSurfaceVariant,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp
        )
    }
}
