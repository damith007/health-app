package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskStatus
import com.example.data.model.TimeSlotTask
import com.example.ui.DailySummary
import com.example.ui.components.TimelineNowMarker
import com.example.ui.components.TimelineSlotItem
import com.example.ui.components.TodayOverviewMatrix
import com.example.ui.theme.OnSurfaceVariant
import kotlinx.coroutines.launch

@Composable
fun TodayGridScreen(
    tasks: List<TimeSlotTask>,
    dailySummary: DailySummary,
    displayDate: String,
    sprintDayInfo: String,
    currentTimeLive: String = "16:15",
    isToday: Boolean = true,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onJumpToNow: () -> Unit,
    onSprintClick: () -> Unit = {},
    onSeedTemplate: () -> Unit = {},
    onSyncRoutineTo90Days: () -> Unit = {},
    onStatusToggle: (TimeSlotTask) -> Unit,
    onStatusSelect: (TimeSlotTask, TaskStatus) -> Unit,
    onTaskClick: (TimeSlotTask) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val filteredTasks = remember(tasks, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) {
            tasks
        } else {
            tasks.filter { it.categoryTag.equals(selectedCategoryFilter, ignoreCase = true) }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .testTag("today_grid_timeline"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Overview Matrix & Sprint Summary
        item(key = "overview_matrix") {
            TodayOverviewMatrix(
                summary = dailySummary,
                displayDate = displayDate,
                sprintDayInfo = sprintDayInfo,
                onPreviousDay = onPreviousDay,
                onNextDay = onNextDay,
                onJumpToNow = {
                    onJumpToNow()
                    // Scroll to current active block
                    val activeIndex = tasks.indexOfFirst { it.status == TaskStatus.IN_PROGRESS }
                    if (activeIndex != -1) {
                        coroutineScope.launch {
                            listState.animateScrollToItem((activeIndex + 1).coerceAtLeast(0))
                        }
                    }
                },
                onSprintClick = onSprintClick,
                onSyncRoutineTo90Days = onSyncRoutineTo90Days
            )
        }

        // Quick Category Filter Bar
        item(key = "category_filter_bar") {
            com.example.ui.components.TaskCategoryFilterBar(
                selectedCategory = selectedCategoryFilter,
                onSelectCategory = { selectedCategoryFilter = it }
            )
        }

        // Timeline Items
        if (filteredTasks.isEmpty()) {
            item(key = "empty_state") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (selectedCategoryFilter != null)
                            "No tasks found for $selectedCategoryFilter.\nTap 'All Slots' to reset filter."
                        else
                            "No schedule blocks logged for $displayDate.",
                        color = OnSurfaceVariant,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    if (selectedCategoryFilter == null) {
                        androidx.compose.material3.Button(
                            onClick = onSeedTemplate,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = com.example.ui.theme.Primary,
                                contentColor = com.example.ui.theme.OnPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Load Daily Architecture Schedule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            itemsIndexed(
                items = filteredTasks,
                key = { _, item -> item.id }
            ) { index, task ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Show NOW ACTIVE marker right before IN_PROGRESS task or current active hour
                    val isFirstInProgress = task.status == TaskStatus.IN_PROGRESS
                    val isCurrentSlot = isToday && (isFirstInProgress || (tasks.none { it.status == TaskStatus.IN_PROGRESS } && index == 0))
                    if (isCurrentSlot) {
                        TimelineNowMarker(
                            timeStr = currentTimeLive,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    TimelineSlotItem(
                        task = task,
                        onStatusToggle = onStatusToggle,
                        onStatusSelect = onStatusSelect,
                        onTaskClick = onTaskClick
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
