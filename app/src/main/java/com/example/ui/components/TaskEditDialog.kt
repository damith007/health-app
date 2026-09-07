package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
    onSave: (TimeSlotTask) -> Unit,
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

    val categories = listOf(
        "#DeepWork", "#Code", "#Health", "#Fitness",
        "#Routine", "#LifeOps", "#Growth", "#Mindset",
        "#Revenue", "#SideProject", "#Recovery", "#SleepPrep", "#Unplanned"
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

                // Time Interval Pickers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Start Time",
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
                            text = "End Time",
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
                                    )
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
