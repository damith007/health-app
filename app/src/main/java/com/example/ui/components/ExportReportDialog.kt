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
    return when (format) {
        ExportFormat.MARKDOWN -> """
# DTrack Sprint Performance Audit
**Horizon:** $range | **Cycle:** Phase 1 (Day 14 / 90)
**Generated:** 2026-10-23T16:15:00Z

## Summary Key Metrics
- **Active Deep Work:** ${summary.activeHours}h (Logged)
- **Planned Budget:** ${summary.plannedHours}h
- **Execution Rate:** 81.4% (Elite status)
- **Focus Purity:** 86%
- **Time Leakage:** 4h 15m (-35% vs benchmark)

## Circadian Chronotype Alignment
- **Chronotype Profile:** Day Owl (09:00 - 12:30 Peak)
- **Peak Output Density:** 94% architecture velocity
- **Top Leak Flagged:** Social Rabbit Hole (1h 45m, post-lunch trigger)

## 90-Day Trajectory
- **Projected Completion:** Day 86 (+4 days ahead of schedule)
- **Sprint Forecast Finish:** Nov 28, 2026
        """.trimIndent()

        ExportFormat.JSON -> """
{
  "dtrack_version": "1.4.0",
  "audit_range": "$range",
  "sprint_day": 14,
  "total_sprint_days": 90,
  "metrics": {
    "active_hours": ${summary.activeHours},
    "missed_hours": ${summary.missedHours},
    "wasted_hours": ${summary.wastedHours},
    "planned_hours": ${summary.plannedHours},
    "execution_rate_pct": 81.4,
    "focus_purity_pct": 86.0
  },
  "trajectory": {
    "status": "AHEAD_OF_PACE",
    "days_delta": 4,
    "forecast_completion_day": 86,
    "projected_finish_date": "2026-11-28"
  },
  "top_friction": "Social Rabbit Hole (1h 45m)"
}
        """.trimIndent()

        ExportFormat.CSV -> """
Metric,Value,Benchmark,Delta,Unit
Active Deep Work,${summary.activeHours},12.0,+2.0,Hours
Planned Focus,${summary.plannedHours},8.0,-1.0,Hours
Execution Rate,81.4,75.0,+6.4,%
Focus Efficiency,86.0,80.0,+6.0,%
Unplanned Void,4.25,6.50,-2.25,Hours
Sprint Pacing,86,90,-4,Days
        """.trimIndent()

        ExportFormat.EXECUTIVE_BRIEF -> """
DTRACK EXECUTIVE BRIEF: $range
========================================
STATUS: GREEN / MOMENTUM CONFIRMED
Day 14 of 90 • Cycle 1 Foundation

Key Highlights:
1. 81.4% execution rate achieved (+4.2% week-over-week).
2. Deep work logged: ${summary.activeHours}h vs target pace.
3. System on track for early sprint completion at Day 86 (+4 days ahead).
4. Friction audit identified 1h 45m post-lunch slip; intervention active.

Recommendation: Maintain 09:00-12:30 deep architecture block lock.
        """.trimIndent()
    }
}
