package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.DailySummary
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.TertiaryCyan

enum class ExportFormat(val label: String, val ext: String) {
    MARKDOWN("Markdown (.md)", "md"),
    JSON("Structured JSON", "json"),
    CSV("Spreadsheet (.csv)", "csv"),
    EXECUTIVE_BRIEF("Executive Brief", "txt")
}

@Composable
fun ExportReportDialog(
    selectedRange: String,
    dailySummary: DailySummary,
    onDismiss: () -> Unit,
    onShare: (String, String) -> Unit = { _, _ -> }
) {
    var selectedFormat by remember { mutableStateOf(ExportFormat.MARKDOWN) }
    var copiedToClipboard by remember { mutableStateOf(false) }

    val reportPreview = remember(selectedFormat, selectedRange, dailySummary) {
        generateExportReport(selectedFormat, selectedRange, dailySummary)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryContainer.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.IosShare,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Export Sprint Report",
                                color = OnSurface,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$selectedRange Performance Audit",
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = OnSurfaceVariant
                        )
                    }
                }

                // Format Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainer)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ExportFormat.values().forEach { fmt ->
                        val isSelected = selectedFormat == fmt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Primary else Color.Transparent)
                                .clickable {
                                    selectedFormat = fmt
                                    copiedToClipboard = false
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fmt.ext.uppercase(),
                                color = if (isSelected) OnPrimary else OnSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Report Preview Box
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PREVIEW (${selectedFormat.label})",
                            color = OnSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        if (copiedToClipboard) {
                            Text(
                                text = "✓ Copied to clipboard",
                                color = SecondaryGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainer)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = reportPreview,
                            color = OnSurface.copy(alpha = 0.88f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                }

                // Quick Audit Highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerHighest)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Execution", color = OnSurfaceVariant, fontSize = 9.sp)
                            Text("81.4% Rate", color = SecondaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerHighest)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Pacing", color = OnSurfaceVariant, fontSize = 9.sp)
                            Text("+4 Days Ahead", color = TertiaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerHighest)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Purity", color = OnSurfaceVariant, fontSize = 9.sp)
                            Text("86% Efficiency", color = Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            copiedToClipboard = true
                            onShare(reportPreview, "Copy")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_report_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceContainerHighest,
                            contentColor = OnSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Copy", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            onShare(reportPreview, "Share")
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("share_report_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = OnPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Share Report", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun generateExportReport(
    format: ExportFormat,
    range: String,
    summary: DailySummary
): String {
    val currentDay = summary.sprintDay
    val totalDays = summary.totalSprintDays
    val cycle = summary.sprintCycle
    val phase = summary.sprintPhase
    val score = summary.dailyScorePercent
    val nowFormatted = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())

    return when (format) {
        ExportFormat.MARKDOWN -> """
# Sprint Performance Audit
**Horizon:** $range | **Cycle:** $phase (Day $currentDay / $totalDays)
**Generated:** $nowFormatted

## Summary Key Metrics
- **Active Focus Logged:** ${summary.activeHours}h
- **Planned Allocation:** ${summary.plannedHours}h
- **Unplanned / Leaks:** ${summary.wastedHours}h
- **Missed / Rescheduled:** ${summary.missedHours}h
- **Execution Score:** $score%

## Sprint Trajectory
- **Current Day:** Day $currentDay of $totalDays
- **Sprint Phase:** $phase (Cycle $cycle)
- **Status:** ${summary.sprintStatus}
        """.trimIndent()

        ExportFormat.JSON -> """
{
  "audit_range": "$range",
  "sprint_day": $currentDay,
  "total_sprint_days": $totalDays,
  "sprint_cycle": $cycle,
  "sprint_phase": "$phase",
  "metrics": {
    "active_hours": ${summary.activeHours},
    "missed_hours": ${summary.missedHours},
    "wasted_hours": ${summary.wastedHours},
    "planned_hours": ${summary.plannedHours},
    "execution_score_pct": $score
  },
  "status": "${summary.sprintStatus}"
}
        """.trimIndent()

        ExportFormat.CSV -> """
Metric,Value,Unit
Active Focus,${summary.activeHours},Hours
Planned Focus,${summary.plannedHours},Hours
Unplanned Leaks,${summary.wastedHours},Hours
Missed Slots,${summary.missedHours},Hours
Execution Score,$score,%
Sprint Day,$currentDay,Days
        """.trimIndent()

        ExportFormat.EXECUTIVE_BRIEF -> """
EXECUTIVE PERFORMANCE BRIEF: $range
========================================
STATUS: ${summary.sprintStatus.uppercase()}
Day $currentDay of $totalDays • $phase

Key Highlights:
1. $score% execution score logged for current horizon.
2. Active focus logged: ${summary.activeHours}h (Planned: ${summary.plannedHours}h).
3. Unplanned leakage restricted to ${summary.wastedHours}h.
4. Sprint Cycle $cycle active.
        """.trimIndent()
    }
}
