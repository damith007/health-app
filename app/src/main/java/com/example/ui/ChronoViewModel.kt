package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.GoalItem
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.TaskStatus
import com.example.data.model.TimeSlotTask
import com.example.data.model.UserProfile
import com.example.data.repository.ChronoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class ChronoTab(val title: String) {
    TODAY_GRID("Today Grid"),
    GOALS_90("90-Day Goals"),
    ANALYTICS("Analytics")
}

data class SprintConfig(
    val currentDay: Int = 14,
    val totalDays: Int = 90,
    val cycle: Int = 1,
    val status: String = "On Track",
    val targetPercent: Int = 100,
    val phaseTitle: String = "Phase 1: Foundation"
)

data class DailySummary(
    val activeHours: Float = 14.0f,
    val missedHours: Float = 2.0f,
    val wastedHours: Float = 1.0f,
    val plannedHours: Float = 7.0f,
    val dailyScorePercent: Int = 78,
    val sprintDay: Int = 14,
    val totalSprintDays: Int = 90,
    val sprintStatus: String = "On Track",
    val sprintTargetPercent: Int = 100,
    val sprintCycle: Int = 1,
    val sprintPhase: String = "Phase 1: Foundation"
)

class ChronoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ChronoRepository
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayDateFormat = SimpleDateFormat("EEE, MMM d", Locale.US)
    private val prefs = application.getSharedPreferences("chrono_profile_prefs", Context.MODE_PRIVATE)

    private val currentCalendar = Calendar.getInstance()

    private fun loadPersistedSprintConfig(): SprintConfig {
        return SprintConfig(
            currentDay = prefs.getInt("sprint_day", 14),
            totalDays = prefs.getInt("sprint_total_days", 90),
            cycle = prefs.getInt("sprint_cycle", 1),
            status = prefs.getString("sprint_status", "On Track") ?: "On Track",
            targetPercent = prefs.getInt("sprint_target_percent", 100),
            phaseTitle = prefs.getString("sprint_phase", "Phase 1: Foundation") ?: "Phase 1: Foundation"
        )
    }

    private val _sprintConfig = MutableStateFlow(loadPersistedSprintConfig())
    val sprintConfig: StateFlow<SprintConfig> = _sprintConfig.asStateFlow()

    private val _showSprintEditDialog = MutableStateFlow(false)
    val showSprintEditDialog: StateFlow<Boolean> = _showSprintEditDialog.asStateFlow()

    // Default to Wed, Oct 23 2026 or today's date
    private val _selectedDate = MutableStateFlow("2026-10-23")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedDisplayDate = MutableStateFlow("Wed, Oct 23")
    val selectedDisplayDate: StateFlow<String> = _selectedDisplayDate.asStateFlow()

    private val _sprintDayInfo = MutableStateFlow(
        "Sprint Day ${loadPersistedSprintConfig().currentDay} • Cycle ${loadPersistedSprintConfig().cycle}"
    )
    val sprintDayInfo: StateFlow<String> = _sprintDayInfo.asStateFlow()

    private val _currentTab = MutableStateFlow(ChronoTab.TODAY_GRID)
    val currentTab: StateFlow<ChronoTab> = _currentTab.asStateFlow()

    // Dialog state for adding/editing a task
    private val _editingTask = MutableStateFlow<TimeSlotTask?>(null)
    val editingTask: StateFlow<TimeSlotTask?> = _editingTask.asStateFlow()

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    // Dialog state for adding/editing a 90-day goal
    private val _editingGoal = MutableStateFlow<GoalItem?>(null)
    val editingGoal: StateFlow<GoalItem?> = _editingGoal.asStateFlow()

    private val _showGoalEditDialog = MutableStateFlow(false)
    val showGoalEditDialog: StateFlow<Boolean> = _showGoalEditDialog.asStateFlow()

    private val _showResetGoalsDialog = MutableStateFlow(false)
    val showResetGoalsDialog: StateFlow<Boolean> = _showResetGoalsDialog.asStateFlow()

    // Profile Dialog & State
    private val _showProfileDialog = MutableStateFlow(false)
    val showProfileDialog: StateFlow<Boolean> = _showProfileDialog.asStateFlow()

    private val _userProfile = MutableStateFlow(loadPersistedProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Notifications Dialog & State
    private val _showNotificationDialog = MutableStateFlow(false)
    val showNotificationDialog: StateFlow<Boolean> = _showNotificationDialog.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem(
                id = "notif_1",
                title = "15:30 Slot In Progress",
                message = "System Stress Testing & Bug Bounty running on cluster 04.",
                timeAgo = "12m ago",
                type = NotificationType.SLOT_ACTIVE,
                isRead = false
            ),
            NotificationItem(
                id = "notif_2",
                title = "Sprint Milestone Achieved",
                message = "81.4% execution rate reached today (+4.2% vs previous cycle).",
                timeAgo = "1h ago",
                type = NotificationType.SPRINT_MILESTONE,
                isRead = false
            ),
            NotificationItem(
                id = "notif_3",
                title = "Phase 1 Foundation Countdown",
                message = "16 days remaining in Cycle 1 of 90-day sprint.",
                timeAgo = "3h ago",
                type = NotificationType.CIRCADIAN_FLOW,
                isRead = false
            ),
            NotificationItem(
                id = "notif_4",
                title = "Distraction Block Recorded",
                message = "45m unplanned gap logged at 13:00. Focus quota adjusted.",
                timeAgo = "5h ago",
                type = NotificationType.LEAK_WARNING,
                isRead = true
            ),
            NotificationItem(
                id = "notif_5",
                title = "Recovery Suggestion",
                message = "Evening Zone-2 walk scheduled at 18:30. Hydrate well.",
                timeAgo = "6h ago",
                type = NotificationType.RECOVERY_PROMPT,
                isRead = true
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    val unreadNotificationsCount: StateFlow<Int> = _notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 3
    )

    // Analytics range filter
    private val _analyticsRange = MutableStateFlow("7 Days")
    val analyticsRange: StateFlow<String> = _analyticsRange.asStateFlow()

    // Analytics Export & Filter Dialogs
    private val _showExportDialog = MutableStateFlow(false)
    val showExportDialog: StateFlow<Boolean> = _showExportDialog.asStateFlow()

    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog.asStateFlow()

    private val _filterSettings = MutableStateFlow(com.example.ui.components.AnalyticsFilterSettings())
    val filterSettings: StateFlow<com.example.ui.components.AnalyticsFilterSettings> = _filterSettings.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ChronoRepository(db.taskDao(), db.goalDao())

        viewModelScope.launch {
            repository.seedDefaultDataIfEmpty(_selectedDate.value)
        }
    }

    val tasks: StateFlow<List<TimeSlotTask>> = _selectedDate.flatMapLatest { date ->
        repository.getTasksForDate(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val goals: StateFlow<List<GoalItem>> = repository.getAllGoals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dailySummary: StateFlow<DailySummary> = combine(tasks, _selectedDate, _sprintConfig) { taskList, _, sprint ->
        calculateSummary(taskList, sprint)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailySummary()
    )

    private fun loadPersistedProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("user_name", "Sri") ?: "Sri",
            email = prefs.getString("user_email", "srimobile69@gmail.com") ?: "srimobile69@gmail.com",
            tier = prefs.getString("user_tier", "Q4 PRO • Founder Track") ?: "Q4 PRO • Founder Track",
            dailyTargetHours = prefs.getFloat("user_target_hours", 8.0f),
            chronotype = prefs.getString("user_chronotype", "Day Owl (09:00 - 12:30 Peak)") ?: "Day Owl (09:00 - 12:30 Peak)",
            activeStreakDays = prefs.getInt("user_streak", 14),
            totalHoursLogged = prefs.getFloat("user_hours_logged", 128.5f),
            executionRatePct = prefs.getFloat("user_exec_rate", 81.4f),
            hapticFeedback = prefs.getBoolean("user_haptic", true),
            autoRollInProgress = prefs.getBoolean("user_autoroll", true),
            strictMode = prefs.getBoolean("user_strict", true)
        )
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        prefs.edit()
            .putString("user_name", profile.name)
            .putString("user_email", profile.email)
            .putString("user_tier", profile.tier)
            .putFloat("user_target_hours", profile.dailyTargetHours)
            .putString("user_chronotype", profile.chronotype)
            .putBoolean("user_haptic", profile.hapticFeedback)
            .putBoolean("user_autoroll", profile.autoRollInProgress)
            .putBoolean("user_strict", profile.strictMode)
            .apply()
        dismissProfileDialog()
    }

    fun openProfileDialog() {
        _showProfileDialog.value = true
    }

    fun dismissProfileDialog() {
        _showProfileDialog.value = false
    }

    fun openNotificationDialog() {
        _showNotificationDialog.value = true
    }

    fun dismissNotificationDialog() {
        _showNotificationDialog.value = false
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    private fun calculateSummary(
        taskList: List<TimeSlotTask>,
        sprint: SprintConfig = _sprintConfig.value
    ): DailySummary {
        if (taskList.isEmpty()) {
            return DailySummary(
                activeHours = 0f,
                missedHours = 0f,
                wastedHours = 0f,
                plannedHours = 0f,
                dailyScorePercent = 0,
                sprintDay = sprint.currentDay,
                totalSprintDays = sprint.totalDays,
                sprintStatus = sprint.status,
                sprintTargetPercent = sprint.targetPercent,
                sprintCycle = sprint.cycle,
                sprintPhase = sprint.phaseTitle
            )
        }

        var activeMin = 0
        var missedMin = 0
        var wastedMin = 0
        var plannedMin = 0

        for (task in taskList) {
            val duration = ((task.endHour * 60 + task.endMinute) - (task.startHour * 60 + task.startMinute)).coerceAtLeast(30)
            when (task.status) {
                TaskStatus.COMPLETED -> activeMin += duration
                TaskStatus.IN_PROGRESS -> activeMin += duration
                TaskStatus.WASTED -> {
                    wastedMin += if (task.wastedMinutes > 0) task.wastedMinutes else duration
                }
                TaskStatus.RESCHEDULED -> missedMin += duration
                TaskStatus.PLANNED -> plannedMin += duration
            }
        }

        val activeH = activeMin / 60f
        val missedH = missedMin / 60f
        val wastedH = wastedMin / 60f
        val plannedH = plannedMin / 60f

        val recordedTotal = activeH + missedH + wastedH
        val score = if (recordedTotal > 0f) {
            ((activeH / recordedTotal) * 100).toInt().coerceIn(0, 100)
        } else {
            78
        }

        return DailySummary(
            activeHours = if (activeH > 0) (Math.round(activeH * 10) / 10f) else 14.0f,
            missedHours = if (missedH > 0) (Math.round(missedH * 10) / 10f) else 2.0f,
            wastedHours = if (wastedH > 0) (Math.round(wastedH * 10) / 10f) else 1.0f,
            plannedHours = if (plannedH > 0) (Math.round(plannedH * 10) / 10f) else 7.0f,
            dailyScorePercent = if (recordedTotal > 0) score else 78,
            sprintDay = sprint.currentDay,
            totalSprintDays = sprint.totalDays,
            sprintStatus = sprint.status,
            sprintTargetPercent = sprint.targetPercent,
            sprintCycle = sprint.cycle,
            sprintPhase = sprint.phaseTitle
        )
    }

    fun selectTab(tab: ChronoTab) {
        _currentTab.value = tab
    }

    fun setAnalyticsRange(range: String) {
        _analyticsRange.value = range
    }

    fun openExportDialog() {
        _showExportDialog.value = true
    }

    fun dismissExportDialog() {
        _showExportDialog.value = false
    }

    fun openFilterDialog() {
        _showFilterDialog.value = true
    }

    fun dismissFilterDialog() {
        _showFilterDialog.value = false
    }

    fun applyFilterSettings(settings: com.example.ui.components.AnalyticsFilterSettings) {
        _filterSettings.value = settings
        dismissFilterDialog()
    }

    fun toggleTaskStatus(task: TimeSlotTask) {
        viewModelScope.launch {
            val nextStatus = task.status.nextToggle()
            repository.updateTask(task.copy(status = nextStatus))
        }
    }

    fun setTaskStatus(task: TimeSlotTask, newStatus: TaskStatus) {
        viewModelScope.launch {
            repository.updateTask(task.copy(status = newStatus))
        }
    }

    fun openEditTask(task: TimeSlotTask) {
        _editingTask.value = task
        _showEditDialog.value = true
    }

    fun openAddNewTask() {
        val nextStartHour = tasks.value.maxOfOrNull { it.endHour }?.coerceAtMost(23) ?: 12
        val newTask = TimeSlotTask(
            dateStr = _selectedDate.value,
            startHour = nextStartHour,
            startMinute = 0,
            endHour = (nextStartHour + 1).coerceAtMost(24),
            endMinute = 0,
            title = "",
            description = "",
            categoryTag = "#DeepWork",
            status = TaskStatus.PLANNED
        )
        _editingTask.value = newTask
        _showEditDialog.value = true
    }

    fun dismissEditDialog() {
        _showEditDialog.value = false
        _editingTask.value = null
    }

    fun saveTask(task: TimeSlotTask) {
        viewModelScope.launch {
            if (task.id == 0L) {
                repository.insertTask(task.copy(dateStr = _selectedDate.value))
            } else {
                repository.updateTask(task)
            }
            dismissEditDialog()
        }
    }

    fun deleteTask(task: TimeSlotTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
            dismissEditDialog()
        }
    }

    fun updateGoal(goal: GoalItem) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun openSetNewGoal() {
        val newGoal = GoalItem(
            id = 0L,
            title = "",
            category = "#DeepWork",
            targetValue = 100,
            currentValue = 0,
            unit = "Hours",
            isCompleted = false
        )
        _editingGoal.value = newGoal
        _showGoalEditDialog.value = true
    }

    fun openEditGoal(goal: GoalItem) {
        _editingGoal.value = goal
        _showGoalEditDialog.value = true
    }

    fun dismissGoalEditDialog() {
        _showGoalEditDialog.value = false
        _editingGoal.value = null
    }

    fun saveGoal(goal: GoalItem) {
        viewModelScope.launch {
            if (goal.id == 0L) {
                repository.insertGoal(goal)
            } else {
                repository.updateGoal(goal)
            }
            dismissGoalEditDialog()
        }
    }

    fun deleteGoal(goal: GoalItem) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            dismissGoalEditDialog()
        }
    }

    fun openResetGoalsDialog() {
        _showResetGoalsDialog.value = true
    }

    fun dismissResetGoalsDialog() {
        _showResetGoalsDialog.value = false
    }

    fun resetGoalsProgressOnly() {
        viewModelScope.launch {
            repository.resetAllGoalsProgress()
            dismissResetGoalsDialog()
        }
    }

    fun resetGoalsToSprintDefaults() {
        viewModelScope.launch {
            repository.resetGoalsToSprintDefaults()
            dismissResetGoalsDialog()
        }
    }

    fun openSprintEditDialog() {
        _showSprintEditDialog.value = true
    }

    fun dismissSprintEditDialog() {
        _showSprintEditDialog.value = false
    }

    fun updateSprintConfig(config: SprintConfig) {
        _sprintConfig.value = config
        prefs.edit()
            .putInt("sprint_day", config.currentDay)
            .putInt("sprint_total_days", config.totalDays)
            .putInt("sprint_cycle", config.cycle)
            .putString("sprint_status", config.status)
            .putInt("sprint_target_percent", config.targetPercent)
            .putString("sprint_phase", config.phaseTitle)
            .apply()
        _sprintDayInfo.value = "Sprint Day ${config.currentDay} • Cycle ${config.cycle}"
        _showSprintEditDialog.value = false
    }

    fun previousDay() {
        adjustDay(-1)
    }

    fun nextDay() {
        adjustDay(1)
    }

    fun jumpToToday() {
        _selectedDate.value = "2026-10-23"
        _selectedDisplayDate.value = "Wed, Oct 23"
        _sprintDayInfo.value = "Sprint Day ${_sprintConfig.value.currentDay} • Cycle ${_sprintConfig.value.cycle}"
        viewModelScope.launch {
            repository.seedDefaultDataIfEmpty(_selectedDate.value)
        }
    }

    private fun adjustDay(delta: Int) {
        try {
            currentCalendar.time = dateFormat.parse(_selectedDate.value) ?: Calendar.getInstance().time
            currentCalendar.add(Calendar.DAY_OF_YEAR, delta)
            val newDateStr = dateFormat.format(currentCalendar.time)
            _selectedDate.value = newDateStr
            _selectedDisplayDate.value = displayDateFormat.format(currentCalendar.time)

            viewModelScope.launch {
                repository.seedDefaultDataIfEmpty(newDateStr)
            }
        } catch (e: Exception) {
            // fallback
        }
    }
}
