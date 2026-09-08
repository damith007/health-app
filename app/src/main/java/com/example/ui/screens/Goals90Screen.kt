package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalItem
import com.example.ui.SprintConfig
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSecondary
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

@Composable
fun Goals90Screen(
    goals: List<GoalItem>,
    sprintConfig: SprintConfig? = null,
    onSprintClick: () -> Unit = {},
    onUpdateGoal: (GoalItem) -> Unit,
    onSetNewGoal: () -> Unit,
    onResetGoals: () -> Unit = {},
    onEditGoal: (GoalItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGoalCategory by remember { mutableStateOf<String?>(null) }

    val filteredGoals = remember(goals, selectedGoalCategory) {
        if (selectedGoalCategory == null) {
            goals
        } else {
            goals.filter { it.category.equals(selectedGoalCategory, ignoreCase = true) }
        }
    }

    val goalCategories = remember(goals) {
        listOf("#DeepWork", "#Code", "#Fitness", "#Routine", "#Growth")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("goals_90_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GoalsHeaderSection(
                sprintConfig = sprintConfig,
                onSprintClick = onSprintClick,
                onSetNewGoal = onSetNewGoal,
                onResetGoals = onResetGoals
            )
        }

        // Category Filter Chips
        item {
            com.example.ui.components.TaskCategoryFilterBar(
                selectedCategory = selectedGoalCategory,
                onSelectCategory = { selectedGoalCategory = it },
                categories = goalCategories,
                modifier = Modifier.padding(horizontal = 0.dp)
            )
        }

        if (filteredGoals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedGoalCategory != null)
                            "No goals found for $selectedGoalCategory.\nTap 'All Slots' to reset filter."
                        else
                            "No goals configured yet.\nTap 'Set Goal' to define a 90-day target.",
                        color = OnSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(
                items = filteredGoals,
                key = { it.id }
            ) { goal ->
                GoalCardItem(
                    goal = goal,
                    onClick = { onEditGoal(goal) },
                    onIncrement = {
                        onUpdateGoal(goal.copy(currentValue = (goal.currentValue + 1).coerceAtMost(goal.targetValue)))
                    },
                    onDecrement = {
                        onUpdateGoal(goal.copy(currentValue = (goal.currentValue - 1).coerceAtLeast(0)))
                    },
                    onToggleComplete = {
                        onUpdateGoal(goal.copy(isCompleted = !goal.isCompleted))
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
fun GoalsHeaderSection(
    sprintConfig: SprintConfig? = null,
    onSprintClick: () -> Unit = {},
    onSetNewGoal: () -> Unit,
    onResetGoals: () -> Unit = {}
) {
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSprintClick() }
                        .padding(vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryContainer.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "90-Day Sprint Objectives",
                                color = OnSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Sprint",
                                tint = Primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        val subtitle = if (sprintConfig != null) {
                            "${sprintConfig.phaseTitle} • Day ${sprintConfig.currentDay} of ${sprintConfig.totalDays}"
                        } else {
                            "Q4 Phase 1: Foundation • Day 14 of 90"
                        }
                        Text(
                            text = subtitle,
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Reset Goals button
                    OutlinedButton(
                        onClick = onResetGoals,
                        modifier = Modifier.testTag("reset_goals_button"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Goals",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "Reset",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Set Goal button
                    Button(
                        onClick = onSetNewGoal,
                        modifier = Modifier.testTag("set_new_goal_button"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = OnPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(2.dp))
                        Text(
                            text = "Set Goal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Overall sprint progress bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sprint Horizon", color = OnSurfaceVariant, fontSize = 10.sp)
                    Text("14 / 90 Days (16d remaining in Phase 1)", color = Primary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SurfaceContainerHighest)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(14f / 90f)
                            .fillMaxHeight()
                            .background(Primary)
                    )
                }
            }
        }
    }
}

@Composable
fun GoalCardItem(
    goal: GoalItem,
    onClick: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onToggleComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("goal_card_${goal.id}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                            .clip(RoundedCornerShape(9999.dp))
                            .background(
                                when (goal.category) {
                                    "#DeepWork", "#Code" -> Primary.copy(alpha = 0.2f)
                                    "#Fitness", "#Routine" -> SecondaryGreen.copy(alpha = 0.2f)
                                    else -> TertiaryCyan.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = goal.category,
                            color = when (goal.category) {
                                "#DeepWork", "#Code" -> Primary
                                "#Fitness", "#Routine" -> SecondaryGreen
                                else -> TertiaryCyan
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = goal.title,
                        color = OnSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Goal",
                        tint = OnSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(if (goal.isCompleted) SecondaryGreen else SurfaceContainerHighest)
                            .clickable { onToggleComplete() }
                            .testTag("goal_complete_toggle_${goal.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (goal.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = OnSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Target progress numbers and quick +/- adjusters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${goal.currentValue}",
                        color = OnSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "/ ${goal.targetValue} ${goal.unit}",
                        color = OnSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Quick Increment / Decrement
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerHigh)
                            .clickable { onDecrement() }
                            .testTag("goal_decrement_${goal.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Primary.copy(alpha = 0.2f))
                            .clickable { onIncrement() }
                            .testTag("goal_increment_${goal.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(SurfaceContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(goal.progressFraction)
                        .fillMaxHeight()
                        .background(if (goal.isCompleted) SecondaryGreen else Primary)
                )
            }
        }
    }
}
