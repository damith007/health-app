package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.DailySummary
import com.example.ui.theme.AmberWarning
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
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.TertiaryCyan

@Composable
fun TodayOverviewMatrix(
    summary: DailySummary,
    displayDate: String,
    sprintDayInfo: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onJumpToNow: () -> Unit,
    onSprintClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Top Identity Pill & Sprint Banner
        SprintBannerCard(
            summary = summary,
            onSprintClick = onSprintClick
        )

        // 2. Dual Metric Cards (90-Day Sprint Gauge & Daily Score)
        DualMetricRow(
            summary = summary,
            onSprintClick = onSprintClick
        )

        // 3. Execution Status Distribution Strip
        ExecutionStatusStrip(summary)

        // 4. Date Switcher & Jump to Now Navigation
        DateSwitcherRow(
            displayDate = displayDate,
            sprintDayInfo = sprintDayInfo,
            onPreviousDay = onPreviousDay,
            onNextDay = onNextDay,
            onJumpToNow = onJumpToNow
        )
    }
}

@Composable
fun SprintBannerCard(
    summary: DailySummary,
    onSprintClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerLow)
            .clickable { onSprintClick() }
            .testTag("sprint_banner_card")
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_chrono90_logo),
                    contentDescription = "Chrono90 Logo",
                    modifier = Modifier.size(36.dp)
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "DTrack Sprint",
                        color = OnSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Primary.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "CYCLE ${summary.sprintCycle}",
                            color = Primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                val daysRemaining = (summary.totalSprintDays - summary.sprintDay).coerceAtLeast(0)
                Text(
                    text = "${summary.sprintPhase} • ${daysRemaining}d remaining",
                    color = OnSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceContainer)
                .padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PulsingDot(color = SecondaryGreen, sizeDp = 7)
            Text(
                text = "SYNCHRONIZED",
                color = SecondaryGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun DualMetricRow(
    summary: DailySummary,
    onSprintClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: 90-Day Sprint Gauge
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainer)
                .clickable { onSprintClick() }
                .testTag("sprint_gauge_card")
                .padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "90-DAY SPRINT",
                            color = OnSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Sprint",
                            tint = Primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(11.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Sprint Target",
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                val safeTotal = summary.totalSprintDays.coerceAtLeast(1)
                val progressFraction = (summary.sprintDay.toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f)
                val progressPercent = (progressFraction * 100).toInt()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Circular Gauge
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(48.dp)) {
                            val strokeWidth = 5.dp.toPx()
                            val radius = (size.minDimension - strokeWidth) / 2
                            val center = Offset(size.width / 2, size.height / 2)

                            // Track
                            drawCircle(
                                color = SurfaceContainerHighest,
                                radius = radius,
                                center = center,
                                style = Stroke(strokeWidth)
                            )

                            // Progress Arc
                            val progressSweep = 360f * progressFraction
                            drawArc(
                                color = Primary,
                                startAngle = -90f,
                                sweepAngle = progressSweep,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = "$progressPercent%",
                            color = OnSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = "Day ${summary.sprintDay}",
                            color = OnSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "of ${summary.totalSprintDays} Days",
                            color = OnSurfaceVariant,
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
                        text = "Target: ${summary.sprintTargetPercent}%",
                        color = OnSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Text(
                        text = summary.sprintStatus,
                        color = when (summary.sprintStatus) {
                            "Ahead of Pace" -> TertiaryCyan
                            "Needs Focus" -> AmberWarning
                            "Behind" -> ErrorRose
                            else -> SecondaryGreen
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Card 2: Today's Execution Score
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainer)
                .padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAILY SCORE",
                        color = OnSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Daily Score Verified",
                        tint = SecondaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "${summary.dailyScorePercent}",
                            color = OnSurface,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "%",
                            color = SecondaryGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .padding(start = 3.dp, bottom = 4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SecondaryGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "+4% vs avg",
                                color = SecondaryGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    val activeHoursFormatted = if (summary.activeHours % 1f == 0f) "${summary.activeHours.toInt()}h" else String.format(java.util.Locale.US, "%.1fh", summary.activeHours)
                    Text(
                        text = "$activeHoursFormatted active recorded",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                // Mini breakdown segmented line with dynamic weights
                val totalHours = (summary.activeHours + summary.missedHours + summary.wastedHours + summary.plannedHours).coerceAtLeast(0.1f)
                val activeWeight = (summary.activeHours / totalHours).coerceAtLeast(0.01f)
                val missedWeight = (summary.missedHours / totalHours).coerceAtLeast(0.01f)
                val wastedWeight = (summary.wastedHours / totalHours).coerceAtLeast(0.01f)
                val plannedWeight = (summary.plannedHours / totalHours).coerceAtLeast(0.01f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SurfaceContainerHighest)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(activeWeight)
                            .fillMaxHeight()
                            .background(SecondaryGreen)
                    )
                    Box(
                        modifier = Modifier
                            .weight(missedWeight)
                            .fillMaxHeight()
                            .background(TertiaryCyan)
                    )
                    Box(
                        modifier = Modifier
                            .weight(wastedWeight)
                            .fillMaxHeight()
                            .background(ErrorRose)
                    )
                    Box(
                        modifier = Modifier
                            .weight(plannedWeight)
                            .fillMaxHeight()
                            .background(Primary)
                    )
                }
            }
        }
    }
}

@Composable
fun ExecutionStatusStrip(summary: DailySummary) {
    fun formatHours(h: Float): String {
        return if (h % 1f == 0f) "${h.toInt()}h" else String.format(java.util.Locale.US, "%.1fh", h)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        StatusCountCell(
            label = "Active",
            hoursStr = formatHours(summary.activeHours),
            color = SecondaryGreen,
            modifier = Modifier.weight(1f)
        )
        StatusCountCell(
            label = "Missed",
            hoursStr = formatHours(summary.missedHours),
            color = TertiaryCyan,
            modifier = Modifier.weight(1f)
        )
        StatusCountCell(
            label = "Wasted",
            hoursStr = formatHours(summary.wastedHours),
            color = ErrorRose,
            modifier = Modifier.weight(1f)
        )
        StatusCountCell(
            label = "Planned",
            hoursStr = formatHours(summary.plannedHours),
            color = Primary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatusCountCell(
    label: String,
    hoursStr: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainer)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = hoursStr,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = OnSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DateSwitcherRow(
    displayDate: String,
    sprintDayInfo: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onJumpToNow: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHigh)
                    .clickable { onPreviousDay() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous Day",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = displayDate,
                    color = OnSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sprintDayInfo,
                    color = SecondaryGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHigh)
                    .clickable { onNextDay() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next Day",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Jump to Now Button
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(Primary.copy(alpha = 0.18f))
                .clickable { onJumpToNow() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("jump_to_now_button"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = "JUMP TO NOW",
                color = Primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}
