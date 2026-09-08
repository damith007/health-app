package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TaskStatus
import com.example.data.model.TimeSlotTask
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Outline
import com.example.ui.theme.Primary
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TertiaryCyan

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskEditDialog(
    task: TimeSlotTask,
    onDismiss: () -> Unit,
    onSave: (TimeSlotTask, Boolean) -> Unit,
    onDelete: (TimeSlotTask) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var categoryTag by remember { mutableStateOf(task.categoryTag) }
    var selectedStatus by remember { mutableStateOf(task.status) }
    var outputNote by remember { mutableStateOf(task.outputNote) }
    var startHour by remember { mutableIntStateOf(task.startHour) }
    var startMinute by remember { mutableIntStateOf(task.startMinute) }
    var endHour by remember { mutableIntStateOf(task.endHour) }
    var endMinute by remember { mutableIntStateOf(task.endMinute) }
    var wastedMinutes by remember { mutableIntStateOf(task.wastedMinutes) }
    var applyToAll90Days by remember { mutableStateOf(false) }

    val categories = listOf(
        "#DeepWork", "#Code", "#Health", "#Fitness",
        "#Routine", "#LifeOps", "#Growth", "#Mindset",
        "#Revenue", "#SideProject", "#Recovery", "#SleepPrep", "#Unplanned"
    )

    val quick24HourSlots = listOf(
        Pair(0, 6) to "00:00 - 06:00 Rest",
        Pair(6, 7) to "06:00 - 07:00 Wake",
        Pair(7, 9) to "07:00 - 09:00 Fitness",
        Pair(9, 12) to "09:00 - 12:00 DeepWork",
        Pair(12, 13) to "12:00 - 13:00 Lunch",
        Pair(13, 17) to "13:00 - 17:00 Execution",
        Pair(17, 19) to "17:00 - 19:00 Growth",
        Pair(19, 22) to "19:00 - 22:00 Life & Side",
        Pair(22, 24) to "22:00 - 24:00 Review & Sleep"
    )

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
                    Text(
                        text = if (task.id == 0L) "Add Task Slot" else "Edit Task Slot",
                        color = OnSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = OnSurfaceVariant
                        )
                    }
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title", color = OnSurfaceVariant) },
                    placeholder = { Text("e.g. Deep Work: Rust Engine Refactor", color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Outline
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Notes", color = OnSurfaceVariant) },
                    placeholder = { Text("Task objectives, outputs or context", color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Outline
                    ),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                // 24-Hour Quick Presets
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "24-Hour Routine Presets",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quick24HourSlots) { (hours, label) ->
                            val isMatch = startHour == hours.first && endHour == hours.second
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isMatch) Primary.copy(alpha = 0.25f) else SurfaceContainer)
                                    .border(
                                        width = if (isMatch) 1.dp else 0.dp,
                                        color = if (isMatch) Primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        startHour = hours.first
                                        startMinute = 0
                                        endHour = hours.second
                                        endMinute = 0
                                    }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isMatch) Primary else OnSurfaceVariant,
                                    fontSize = 10.sp,
                                    fontWeight = if (isMatch) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Time Interval Pickers (24-Hour Format)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Start Time (24h)",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainer)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("%02d:%02d", startHour, startMinute),
                                color = OnSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceContainerHighest)
                                        .clickable { startHour = (startHour - 1 + 24) % 24 }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("-1h", color = OnSurface, fontSize = 10.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceContainerHighest)
                                        .clickable { startHour = (startHour + 1) % 24 }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("+1h", color = OnSurface, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "End Time (24h)",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainer)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("%02d:%02d", endHour, endMinute),
                                color = OnSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceContainerHighest)
                                        .clickable { endHour = (endHour - 1 + 25) % 25 }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("-1h", color = OnSurface, fontSize = 10.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceContainerHighest)
                                        .clickable { endHour = ((endHour + 1).coerceAtMost(24)) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("+1h", color = OnSurface, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // 90-Day Sprint Routine Sync Toggle
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { applyToAll90Days = !applyToAll90Days },
                    colors = CardDefaults.cardColors(
                        containerColor = if (applyToAll90Days) Primary.copy(alpha = 0.15f) else SurfaceContainer
                    ),
                    border = if (applyToAll90Days) androidx.compose.foundation.BorderStroke(1.dp, Primary) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = null,
                                    tint = if (applyToAll90Days) Primary else OnSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Sync across 90-Day Sprint Routine",
                                    color = if (applyToAll90Days) Primary else OnSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Applies this 24h slot to all 90 days of the sprint cycle",
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = applyToAll90Days,
                            onCheckedChange = { applyToAll90Days = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = OnPrimary,
                                checkedTrackColor = Primary,
                                uncheckedThumbColor = OnSurfaceVariant,
                                uncheckedTrackColor = SurfaceContainerHighest
                            )
                        )
                    }
                }

                // Status Selector (Planned, Completed, Wasted, In Progress, Rescheduled)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Status",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            TaskStatus.PLANNED,
                            TaskStatus.COMPLETED,
                            TaskStatus.WASTED,
                            TaskStatus.IN_PROGRESS,
                            TaskStatus.RESCHEDULED
                        ).forEach { status ->
                            val isSelected = selectedStatus == status
                            val color = when (status) {
                                TaskStatus.COMPLETED -> SecondaryGreen
                                TaskStatus.WASTED -> ErrorRose
                                TaskStatus.IN_PROGRESS -> SecondaryGreen
                                TaskStatus.RESCHEDULED -> TertiaryCyan
                                TaskStatus.PLANNED -> Primary
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) color.copy(alpha = 0.25f)
                                        else SurfaceContainer
                                    )
                                    .clickable { selectedStatus = status }
                                    .padding(vertical = 7.dp, horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = status.displayName,
                                    color = if (isSelected) color else OnSurfaceVariant,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Category Tag Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Category",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { tag ->
                            val isSelected = categoryTag.equals(tag, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(
                                        if (isSelected) Primary.copy(alpha = 0.25f)
                                        else SurfaceContainer
                                    )
                                    .clickable { categoryTag = tag }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = if (isSelected) Primary else OnSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Output Note
                OutlinedTextField(
                    value = outputNote,
                    onValueChange = { outputNote = it },
                    label = { Text("Output Metric / Note", color = OnSurfaceVariant) },
                    placeholder = { Text("e.g. Output: 124 LOC + 18 Unit Tests", color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Outline
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (task.id != 0L) {
                        IconButton(
                            onClick = { onDelete(task) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(ErrorContainer.copy(alpha = 0.3f))
                                .testTag("delete_task_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Task",
                                tint = ErrorRose
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceContainerHighest,
                            contentColor = OnSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(
                                    task.copy(
                                        title = title.trim(),
                                        description = description.trim(),
                                        categoryTag = categoryTag,
                                        status = selectedStatus,
                                        outputNote = outputNote.trim(),
                                        startHour = startHour,
                                        startMinute = startMinute,
                                        endHour = endHour,
                                        endMinute = endMinute,
                                        wastedMinutes = if (selectedStatus == TaskStatus.WASTED) 45 else 0
                                    ),
                                    applyToAll90Days
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("save_task_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = OnPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Task", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
