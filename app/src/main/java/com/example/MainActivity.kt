package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ChronoTab
import com.example.ui.ChronoViewModel
import com.example.ui.components.ChronoBottomNav
import com.example.ui.components.ChronoTopHeader
import com.example.ui.components.ExportReportDialog
import com.example.ui.components.GoalEditDialog
import com.example.ui.components.MetricFilterDialog
import com.example.ui.components.NotificationDialog
import com.example.ui.components.ProfileDialog
import com.example.ui.components.ResetGoalsDialog
import com.example.ui.components.SprintEditDialog
import com.example.ui.components.TaskEditDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.Goals90Screen
import com.example.ui.screens.TodayGridScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.Primary
import com.example.ui.theme.SurfaceDark

class MainActivity : ComponentActivity() {
    private val viewModel: ChronoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                ChronoApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ChronoApp(viewModel: ChronoViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val dailySummary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val selectedDisplayDate by viewModel.selectedDisplayDate.collectAsStateWithLifecycle()
    val sprintDayInfo by viewModel.sprintDayInfo.collectAsStateWithLifecycle()
    val analyticsRange by viewModel.analyticsRange.collectAsStateWithLifecycle()

    val showEditDialog by viewModel.showEditDialog.collectAsStateWithLifecycle()
    val editingTask by viewModel.editingTask.collectAsStateWithLifecycle()

    val showGoalEditDialog by viewModel.showGoalEditDialog.collectAsStateWithLifecycle()
    val editingGoal by viewModel.editingGoal.collectAsStateWithLifecycle()
    val showResetGoalsDialog by viewModel.showResetGoalsDialog.collectAsStateWithLifecycle()

    val showProfileDialog by viewModel.showProfileDialog.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val showNotificationDialog by viewModel.showNotificationDialog.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()

    val showExportDialog by viewModel.showExportDialog.collectAsStateWithLifecycle()
    val showFilterDialog by viewModel.showFilterDialog.collectAsStateWithLifecycle()
    val filterSettings by viewModel.filterSettings.collectAsStateWithLifecycle()

    val showSprintEditDialog by viewModel.showSprintEditDialog.collectAsStateWithLifecycle()
    val sprintConfig by viewModel.sprintConfig.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark),
        topBar = {
            ChronoTopHeader(
                currentTab = currentTab,
                unreadNotificationsCount = unreadNotificationsCount,
                onNotificationsClick = { viewModel.openNotificationDialog() },
                onProfileClick = { viewModel.openProfileDialog() }
            )
        },
        bottomBar = {
            ChronoBottomNav(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        floatingActionButton = {
            when (currentTab) {
                ChronoTab.TODAY_GRID -> {
                    FloatingActionButton(
                        onClick = { viewModel.openAddNewTask() },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Primary,
                        contentColor = OnPrimary,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .testTag("add_task_fab")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task Block",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                ChronoTab.GOALS_90 -> {
                    FloatingActionButton(
                        onClick = { viewModel.openSetNewGoal() },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Primary,
                        contentColor = OnPrimary,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .testTag("set_goal_fab")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Set Sprint Goal",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                else -> {}
            }
        },
        containerColor = SurfaceDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceDark)
        ) {
            when (currentTab) {
                ChronoTab.TODAY_GRID -> {
                    TodayGridScreen(
                        tasks = tasks,
                        dailySummary = dailySummary,
                        displayDate = selectedDisplayDate,
                        sprintDayInfo = sprintDayInfo,
                        onPreviousDay = { viewModel.previousDay() },
                        onNextDay = { viewModel.nextDay() },
                        onJumpToNow = { viewModel.jumpToToday() },
                        onSprintClick = { viewModel.openSprintEditDialog() },
                        onStatusToggle = { viewModel.toggleTaskStatus(it) },
                        onStatusSelect = { task, status -> viewModel.setTaskStatus(task, status) },
                        onTaskClick = { viewModel.openEditTask(it) }
                    )
                }
                ChronoTab.GOALS_90 -> {
                    Goals90Screen(
                        goals = goals,
                        sprintConfig = sprintConfig,
                        onSprintClick = { viewModel.openSprintEditDialog() },
                        onUpdateGoal = { viewModel.updateGoal(it) },
                        onSetNewGoal = { viewModel.openSetNewGoal() },
                        onResetGoals = { viewModel.openResetGoalsDialog() },
                        onEditGoal = { viewModel.openEditGoal(it) }
                    )
                }
                ChronoTab.ANALYTICS -> {
                    AnalyticsScreen(
                        selectedRange = analyticsRange,
                        onRangeChange = { viewModel.setAnalyticsRange(it) },
                        onFilterClick = { viewModel.openFilterDialog() },
                        onExportClick = { viewModel.openExportDialog() }
                    )
                }
            }

            // Edit / Add Task Dialog
            if (showEditDialog && editingTask != null) {
                TaskEditDialog(
                    task = editingTask!!,
                    onDismiss = { viewModel.dismissEditDialog() },
                    onSave = { viewModel.saveTask(it) },
                    onDelete = { viewModel.deleteTask(it) }
                )
            }

            // Set / Edit Goal Dialog
            if (showGoalEditDialog && editingGoal != null) {
                GoalEditDialog(
                    goal = editingGoal!!,
                    onDismiss = { viewModel.dismissGoalEditDialog() },
                    onSave = { viewModel.saveGoal(it) },
                    onDelete = { viewModel.deleteGoal(it) }
                )
            }

            // Profile & Settings Dialog
            if (showProfileDialog) {
                ProfileDialog(
                    userProfile = userProfile,
                    onDismiss = { viewModel.dismissProfileDialog() },
                    onSaveProfile = { viewModel.updateUserProfile(it) }
                )
            }

            // Notifications Dialog
            if (showNotificationDialog) {
                NotificationDialog(
                    notifications = notifications,
                    onDismiss = { viewModel.dismissNotificationDialog() },
                    onMarkAllAsRead = { viewModel.markAllNotificationsRead() },
                    onNotificationClick = { viewModel.markNotificationRead(it.id) },
                    onClearAll = { viewModel.clearAllNotifications() }
                )
            }

            // Export Sprint Report Dialog
            if (showExportDialog) {
                ExportReportDialog(
                    selectedRange = analyticsRange,
                    dailySummary = dailySummary,
                    onDismiss = { viewModel.dismissExportDialog() }
                )
            }

            // Metric Filter & Lenses Dialog
            if (showFilterDialog) {
                MetricFilterDialog(
                    currentSettings = filterSettings,
                    onDismiss = { viewModel.dismissFilterDialog() },
                    onApply = { viewModel.applyFilterSettings(it) }
                )
            }

            // Reset 90-Day Goals Dialog
            if (showResetGoalsDialog) {
                ResetGoalsDialog(
                    onDismiss = { viewModel.dismissResetGoalsDialog() },
                    onResetProgressOnly = { viewModel.resetGoalsProgressOnly() },
                    onResetToSprintDefaults = { viewModel.resetGoalsToSprintDefaults() }
                )
            }

            // Sprint Tracker Calibration Dialog
            if (showSprintEditDialog) {
                SprintEditDialog(
                    currentConfig = sprintConfig,
                    onDismiss = { viewModel.dismissSprintEditDialog() },
                    onSave = { updatedConfig -> viewModel.updateSprintConfig(updatedConfig) },
                    onNavigateToGoals = {
                        viewModel.dismissSprintEditDialog()
                        viewModel.selectTab(ChronoTab.GOALS_90)
                    }
                )
            }
        }
    }
}
