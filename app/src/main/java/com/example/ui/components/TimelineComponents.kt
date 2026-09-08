package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskStatus
import com.example.data.model.TimeSlotTask
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.OnErrorContainer
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSecondary
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
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TertiaryContainer
import com.example.ui.theme.TertiaryCyan

@Composable
fun TimelineNowMarker(
    timeStr: String = "16:15",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time pill in left column
        Box(
            modifier = Modifier
                .width(48.dp)
                .padding(end = 6.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Primary)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = timeStr,
                    color = OnPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Needle pinhead & line
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Primary)
                    .border(2.dp, Primary.copy(alpha = 0.35f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Primary, SecondaryGreen, TertiaryCyan)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(Primary.copy(alpha = 0.25f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "NOW ACTIVE",
                    color = Primary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
            }
        }
    }
}

@Composable
fun TimelineSlotItem(
    task: TimeSlotTask,
    onStatusToggle: (TimeSlotTask) -> Unit,
    onStatusSelect: (TimeSlotTask, TaskStatus) -> Unit,
    onTaskClick: (TimeSlotTask) -> Unit,
    modifier: Modifier = Modifier
) {
    val isWasted = task.status == TaskStatus.WASTED
    val isInProgress = task.status == TaskStatus.IN_PROGRESS
    val isRescheduled = task.status == TaskStatus.RESCHEDULED
    val isCompleted = task.status == TaskStatus.COMPLETED

    val accentColor = when {
        isWasted -> ErrorRose
        isInProgress -> SecondaryGreen
        isCompleted -> when (task.categoryTag) {
            "#Health", "#Routine" -> TertiaryCyan
            "#DeepWork", "#Code", "#SideProject" -> Primary
            else -> SecondaryGreen
        }
        isRescheduled -> TertiaryCyan
        else -> SurfaceBright
    }

    val cardBackground = when {
        isWasted -> ErrorContainer.copy(alpha = 0.30f)
        isInProgress -> SurfaceContainerHigh
        else -> SurfaceContainer
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Left Column: Time markers
        Column(
            modifier = Modifier
                .width(48.dp)
                .padding(top = 2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = task.startTimeFormatted,
                color = if (isWasted) ErrorRose else if (isInProgress) SecondaryGreen else OnSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = task.endTimeFormatted,
                color = if (isWasted) ErrorRose.copy(alpha = 0.7f) else OnSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }

        // Right Column: Card
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(cardBackground)
                .border(
                    width = if (isInProgress) 1.dp else 0.dp,
                    color = if (isInProgress) SecondaryGreen.copy(alpha = 0.4f) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onTaskClick(task) }
        ) {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                // Vertical accent stripe
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(accentColor)
                )

                // Card Inner Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Top row: Categories & Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isWasted) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(ErrorRose.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = ErrorRose,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = "UNPLANNED GAP",
                                            color = ErrorRose,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            } else {
                                // Category Tag
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(
                                            when (task.categoryTag) {
                                                "#Health", "#Routine" -> TertiaryCyan.copy(alpha = 0.15f)
                                                "#Fitness" -> SecondaryGreen.copy(alpha = 0.15f)
                                                "#DeepWork", "#Code" -> Primary.copy(alpha = 0.20f)
                                                else -> Primary.copy(alpha = 0.15f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = task.categoryTag,
                                        color = when (task.categoryTag) {
                                            "#Health", "#Routine" -> TertiaryCyan
                                            "#Fitness" -> SecondaryGreen
                                            "#DeepWork", "#Code" -> Primary
                                            else -> Primary
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                if (task.secondaryTag.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(9999.dp))
                                            .background(SurfaceContainerHighest)
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = task.secondaryTag,
                                            color = OnSurfaceVariant,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Text(
                                    text = task.durationFormatted,
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Status Badge (Tappable to toggle!)
                        StatusBadgePill(
                            task = task,
                            onStatusToggle = { onStatusToggle(task) }
                        )
                    }

                    // Task Title & Edit Affordance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.title.ifEmpty { "Untitled Task" },
                            color = if (isWasted) OnErrorContainer else OnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (isRescheduled) TextDecoration.LineThrough else null,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = OnSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onTaskClick(task) }
                        )
                    }

                    // Task Description
                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            color = if (isWasted) OnErrorContainer.copy(alpha = 0.8f) else OnSurfaceVariant,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    // Extra section based on Status:
                    when {
                        isInProgress -> {
                            // 4-Action segmented controls
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerLowest)
                                    .padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SecondaryGreen)
                                        .clickable { onStatusSelect(task, TaskStatus.COMPLETED) }
                                        .padding(vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = OnSecondary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "Complete",
                                            color = OnSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceContainer)
                                        .clickable { onStatusSelect(task, TaskStatus.PLANNED) }
                                        .padding(vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = OnSurfaceVariant,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "Hold",
                                            color = OnSurfaceVariant,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceContainer)
                                        .clickable { onTaskClick(task) }
                                        .padding(vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.CallSplit,
                                            contentDescription = null,
                                            tint = OnSurfaceVariant,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "Split 30m",
                                            color = OnSurfaceVariant,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceContainer)
                                        .clickable { onTaskClick(task) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Options",
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        task.status == TaskStatus.PLANNED -> {
                            // Micro state chips picker for Planned tasks
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(SurfaceContainerHighest)
                                        .clickable { onStatusSelect(task, TaskStatus.COMPLETED) }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Mark Done",
                                        color = SecondaryGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(SurfaceContainerHighest)
                                        .clickable { onStatusSelect(task, TaskStatus.RESCHEDULED) }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Postpone",
                                        color = TertiaryCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(SurfaceContainerHighest)
                                        .clickable { onStatusSelect(task, TaskStatus.WASTED) }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Cancel",
                                        color = ErrorRose,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        isWasted -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (task.outputNote.isNotEmpty()) task.outputNote else "Impact: -12% Daily Focus Ratio",
                                    color = ErrorRose,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ErrorRose.copy(alpha = 0.15f))
                                        .clickable { onTaskClick(task) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Log Post-Mortem",
                                        color = ErrorRose,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        task.outputNote.isNotEmpty() -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = task.outputNote,
                                    color = SecondaryGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
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

@Composable
fun StatusBadgePill(
    task: TimeSlotTask,
    onStatusToggle: () -> Unit
) {
    val (bgColor, textColor, text, icon) = when (task.status) {
        TaskStatus.COMPLETED -> Quadruple(
            SecondaryGreen.copy(alpha = 0.15f),
            SecondaryGreen,
            "Completed",
            Icons.Default.Check
        )
        TaskStatus.IN_PROGRESS -> Quadruple(
            SecondaryGreen.copy(alpha = 0.20f),
            SecondaryGreen,
            "IN PROGRESS",
            null
        )
        TaskStatus.WASTED -> Quadruple(
            ErrorRose.copy(alpha = 0.20f),
            ErrorRose,
            if (task.wastedMinutes > 0) "Wasted (${task.wastedMinutes}m)" else "Wasted",
            Icons.Default.Block
        )
        TaskStatus.RESCHEDULED -> Quadruple(
            TertiaryCyan.copy(alpha = 0.20f),
            TertiaryCyan,
            "Rescheduled",
            Icons.Default.Update
        )
        TaskStatus.PLANNED -> Quadruple(
            SurfaceContainerHighest,
            OnSurfaceVariant,
            "Planned",
            Icons.Default.Schedule
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(bgColor)
            .clickable { onStatusToggle() }
            .padding(horizontal = 7.dp, vertical = 2.5.dp)
            .testTag("status_badge_${task.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            if (task.status == TaskStatus.IN_PROGRESS) {
                PulsingDot(color = SecondaryGreen, sizeDp = 6)
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
