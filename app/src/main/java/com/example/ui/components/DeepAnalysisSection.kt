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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.TertiaryContainer
import com.example.ui.theme.TertiaryCyan

data class DeepAnalysisCard(
    val id: String,
    val title: String,
    val headline: String,
    val impact: String,
    val recommendation: String,
    val icon: ImageVector,
    val tag: String,
    val themeColor: Color
)

@Composable
fun DeepAnalysisSection(
    analysisCards: List<DeepAnalysisCard> = emptyList(),
    modifier: Modifier = Modifier
) {
    val defaultCards = remember {
        listOf(
            DeepAnalysisCard(
                id = "card_1",
                title = "Circadian Peak Alignment",
                headline = "09:00 - 12:30 Focus Purity at 94%",
                impact = "+3.4x Higher Architecture Velocity",
                recommendation = "Lock this 3.5-hour morning window against external meetings. Shift all asynchronous code reviews and administrative triage to post-16:30.",
                icon = Icons.Default.Speed,
                tag = "HIGH LEVERAGE",
                themeColor = Primary
            ),
            DeepAnalysisCard(
                id = "card_2",
                title = "Friction Gap Remediation",
                headline = "Post-Lunch Context Switch Detected (13:00 - 13:45)",
                impact = "-1.75h Lost Cumulative Daily Output",
                recommendation = "Schedule a deliberate 20-minute physical recovery walk or light cardio right at 13:15 to suppress dopamine seeking and social rabbit holes.",
                icon = Icons.Default.TipsAndUpdates,
                tag = "LEAK MITIGATION",
                themeColor = TertiaryCyan
            ),
            DeepAnalysisCard(
                id = "card_3",
                title = "90-Day Horizon Pacing",
                headline = "Day Velocity exceeds target pace by 4.2%",
                impact = "Estimated Sprint Completion: Ahead of Schedule",
                recommendation = "You have accumulated a solid buffer. Maintain current weekly volume without burnout escalation to guarantee a Q4 early finish.",
                icon = Icons.Default.AutoAwesome,
                tag = "PACE BUFFER",
                themeColor = SecondaryGreen
            )
        )
    }

    val cards = if (analysisCards.isNotEmpty()) analysisCards else defaultCards
    var expandedCardId by remember(cards) { mutableStateOf<String?>(cards.firstOrNull()?.id) }
    var actionAppliedMap by remember { mutableStateOf(mapOf<String, Boolean>()) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .padding(14.dp)
            .testTag("deep_analysis_section")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Deep Analysis & Insights",
                            color = OnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AI Chrono Engine Pattern Synthesis",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SecondaryContainer.copy(alpha = 0.2f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "3 Insights",
                        color = SecondaryGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Insights list
            cards.forEach { card ->
                val isExpanded = expandedCardId == card.id
                val isApplied = actionAppliedMap[card.id] == true

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .clickable {
                            expandedCardId = if (isExpanded) null else card.id
                        }
                        .padding(12.dp)
                        .testTag("insight_card_${card.id}")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(card.themeColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = card.icon,
                                        contentDescription = null,
                                        tint = card.themeColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = card.title,
                                        color = OnSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = card.headline,
                                        color = OnSurfaceVariant,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(card.themeColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = card.tag,
                                    color = card.themeColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(2.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerHighest)
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("IMPACT:", color = card.themeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text(card.impact, color = OnSurface, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(
                                        text = card.recommendation,
                                        color = OnSurfaceVariant,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isApplied) SecondaryContainer else card.themeColor)
                                        .clickable {
                                            actionAppliedMap = actionAppliedMap.toMutableMap().apply {
                                                put(card.id, !(get(card.id) ?: false))
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("apply_recommendation_${card.id}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (isApplied) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = SecondaryGreen,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "Protocol Applied",
                                                color = SecondaryGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Text(
                                                text = "Apply Protocol",
                                                color = OnPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = null,
                                                tint = OnPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
