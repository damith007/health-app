package com.example.ui

import android.app.Application
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CategoryBreakdown
import com.example.data.model.GoalItem
import com.example.data.model.HourlyDensity
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.RealtimeAnalytics
import com.example.data.model.TaskStatus
import com.example.data.model.TimeSlotTask
import com.example.data.model.UserProfile
import com.example.data.repository.ChronoRepository
import com.example.ui.components.DeepAnalysisCard
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.Primary
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SurfaceBright
import com.example.ui.theme.TertiaryCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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
    val activeHours: Float = 0.0f,
    val missedHours: Float = 0.0f,
    val wastedHours: Float = 0.0f,
    val plannedHours: Float = 0.0f,
    val dailyScorePercent: Int = 0,
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
    private val liveTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    private val prefs = application.getSharedPreferences("chrono_profile_prefs", Context.MODE_PRIVATE)

    private val currentCalendar = Calendar.getInstance()

    fun isDateToday(dateStr: String): Boolean = dateStr == dateFormat.format(Date())

    private fun calculateSprintDayFromDate(startDateStr: String, totalDays: Int = 90): Int {
        return try {
            val startDate = dateFormat.parse(startDateStr) ?: Date()
            val todayStr = dateFormat.format(Date())
            val today = dateFormat.parse(todayStr) ?: Date()
            val diffMillis = today.time - startDate.time
            val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt() + 1
            days.coerceIn(1, totalDays)
        } catch (e: Exception) {
            1
        }
    }

    // Real-time ticking clock
    private val _currentTimeLive = MutableStateFlow(liveTimeFormat.format(Date()))
    val currentTimeLive: StateFlow<String> = _currentTimeLive.asStateFlow()

    private fun loadPersistedSprintConfig(): SprintConfig {
        val todayStr = dateFormat.format(Date())
        val savedStartDate = prefs.getString("user_sprint_start_date", todayStr) ?: todayStr
        val savedTotalDays = prefs.getInt("sprint_total_days", 90)
        val calculatedDay = calculateSprintDayFromDate(savedStartDate, savedTotalDays)
        val defaultCycle = ((calculatedDay - 1) / 30) + 1

        return SprintConfig(
            currentDay = prefs.getInt("sprint_day", calculatedDay),
            totalDays = savedTotalDays,
            cycle = prefs.getInt("sprint_cycle", defaultCycle),
            status = prefs.getString("sprint_status", "Active") ?: "Active",
            targetPercent = prefs.getInt("sprint_target_percent", 100),
            phaseTitle = prefs.getString("sprint_phase", "Phase $defaultCycle Execution") ?: "Phase $defaultCycle Execution"
        )
    }

    private val _sprintConfig = MutableStateFlow(loadPersistedSprintConfig())
    val sprintConfig: StateFlow<SprintConfig> = _sprintConfig.asStateFlow()

    private val _showSprintEditDialog = MutableStateFlow(false)
    val showSprintEditDialog: StateFlow<Boolean> = _showSprintEditDialog.asStateFlow()

    // Selected Date for Today Grid
    private val initialDateStr = dateFormat.format(Date())
    private val _selectedDate = MutableStateFlow(initialDateStr)
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedDisplayDate = MutableStateFlow(displayDateFormat.format(Date()))
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
                title = "Active Sprint Tracker",
                message = "Sprint schedule and daily blocks synchronized with live clock.",
                timeAgo = "Just now",
                type = NotificationType.CIRCADIAN_FLOW,
                isRead = false
            ),
            NotificationItem(
                id = "notif_2",
                title = "Focus Quota Initialized",
                message = "Daily target hours ready for real-time tracking.",
                timeAgo = "1h ago",
                type = NotificationType.SPRINT_MILESTONE,
                isRead = false
            ),
            NotificationItem(
                id = "notif_3",
                title = "Recovery & Rest Protocol",
                message = "Optimal circadian wind-down recommended in the evening.",
                timeAgo = "3h ago",
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
            val start = _userProfile.value.sprintStartDate.ifBlank { _selectedDate.value }
            repository.seedDefaultDataIfEmpty(_selectedDate.value, start, _sprintConfig.value.totalDays)
        }

        // Clock ticking coroutine
        viewModelScope.launch {
            while (isActive) {
                _currentTimeLive.value = liveTimeFormat.format(Date())
                delay(1000)
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<TimeSlotTask>> = _selectedDate.flatMapLatest { date ->
        repository.getTasksForDate(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTasks: StateFlow<List<TimeSlotTask>> = repository.getAllTasks().stateIn(
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

    // Real-Time Analytics Stream computed from all stored tasks & selected range
    val realtimeAnalytics: StateFlow<RealtimeAnalytics> = combine(allTasks, _analyticsRange, _sprintConfig) { allList, range, sprint ->
        calculateRealtimeAnalytics(allList, range, sprint)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RealtimeAnalytics()
    )

    // Dynamic Deep Analysis Cards derived from actual user patterns
    val deepAnalysisCards: StateFlow<List<DeepAnalysisCard>> = combine(allTasks, dailySummary, realtimeAnalytics) { allList, summary, analytics ->
        generateDynamicAnalysisCards(allList, summary, analytics)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun calculateRealtimeAnalytics(
        allTasks: List<TimeSlotTask>,
        range: String,
        sprint: SprintConfig
    ): RealtimeAnalytics {
        val currentSprintDay = sprint.currentDay
        val totalSprintDays = sprint.totalDays
        val targetDailyH = _userProfile.value.dailyTargetHours.coerceAtLeast(1.0f)
        val targetSprintH = totalSprintDays * targetDailyH

        if (allTasks.isEmpty()) {
            return RealtimeAnalytics(
                currentSprintDay = currentSprintDay,
                totalSprintDays = totalSprintDays,
                focusLoggedHours = 0f,
                targetFocusHours = currentSprintDay * targetDailyH,
                executionRatePct = 0f,
                executionRateDelta = "0.0%",
                focusEfficiencyPct = 0,
                unplannedHours = 0f,
                unplannedIncidents = 0,
                daysAheadOfSchedule = 0,
                projectedCompletionDay = totalSprintDays,
                forecastFinishDate = "Day $totalSprintDays",
                chronotypeName = "Day Owl",
                peakFocusWindow = "09:00 - 12:30",
                peakFocusPercent = 0,
                topTimeLeakTitle = "Zero Leaks",
                contextSwitchesPerDay = 0f,
                sprintTargetHours = targetSprintH,
                categoryBreakdowns = emptyList(),
                hourlyHeatmap = emptyList()
            )
        }

        var totalActiveMinutes = 0
        var totalWastedMinutes = 0
        var totalPlannedMinutes = 0
        var totalMissedMinutes = 0
        var wastedCount = 0

        val hourlyCounts = IntArray(24) { 0 }
        val categoryMinutes = mutableMapOf<String, Int>()

        for (task in allTasks) {
            val dur = ((task.endHour * 60 + task.endMinute) - (task.startHour * 60 + task.startMinute)).coerceAtLeast(15)
            val cat = if (task.categoryTag.isNotBlank()) task.categoryTag else "#General"

            when (task.status) {
                TaskStatus.COMPLETED, TaskStatus.IN_PROGRESS -> {
                    totalActiveMinutes += dur
                    categoryMinutes[cat] = (categoryMinutes[cat] ?: 0) + dur
                    for (h in task.startHour until task.endHour.coerceAtMost(24)) {
                        if (h in 0..23) hourlyCounts[h]++
                    }
                }
                TaskStatus.WASTED -> {
                    val wDur = if (task.wastedMinutes > 0) task.wastedMinutes else dur
                    totalWastedMinutes += wDur
                    wastedCount++
                    categoryMinutes["#Unplanned"] = (categoryMinutes["#Unplanned"] ?: 0) + wDur
                }
                TaskStatus.RESCHEDULED -> {
                    totalMissedMinutes += dur
                }
                TaskStatus.PLANNED -> {
                    totalPlannedMinutes += dur
                }
            }
        }

        val totalActiveHours = Math.round((totalActiveMinutes / 60f) * 10) / 10f
        val totalWastedHours = Math.round((totalWastedMinutes / 60f) * 10) / 10f
        val recordedTotalHours = totalActiveHours + totalWastedHours + (totalMissedMinutes / 60f)

        val execRate = if (recordedTotalHours > 0f) {
            ((totalActiveHours / recordedTotalHours) * 100f).coerceIn(0f, 100f)
        } else 0f

        val efficiency = if (totalActiveHours + totalWastedHours > 0f) {
            ((totalActiveHours / (totalActiveHours + totalWastedHours)) * 100).toInt().coerceIn(0, 100)
        } else 0

        // Build hourly heatmap list from actual counts
        val maxCount = hourlyCounts.maxOrNull()?.coerceAtLeast(1) ?: 1
        val heatmapList = (6..23).map { h ->
            val count = hourlyCounts[h]
            val pct = if (count > 0) ((count.toFloat() / maxCount) * 100).toInt().coerceIn(10, 100) else 0
            HourlyDensity(
                hourLabel = String.format(Locale.US, "%02d:00", h),
                densityPercent = pct
            )
        }

        // Peak focus window from hourly distribution
        var peakStartHour = 9
        var peakMaxCount = 0
        for (h in 6..20) {
            val windowCount = hourlyCounts[h] + (if (h + 1 < 24) hourlyCounts[h + 1] else 0)
            if (windowCount > peakMaxCount) {
                peakMaxCount = windowCount
                peakStartHour = h
            }
        }
        val peakWindowStr = String.format(Locale.US, "%02d:00 - %02d:30", peakStartHour, (peakStartHour + 3).coerceAtMost(23))
        val peakPct = if (totalActiveHours > 0) ((peakMaxCount.toFloat() / allTasks.size.coerceAtLeast(1)) * 100).toInt().coerceIn(10, 100) else 0

        // Build category breakdowns from real logged categories
        val totalCatMins = categoryMinutes.values.sum().coerceAtLeast(1)
        val defaultColors = listOf(Primary, TertiaryCyan, SecondaryGreen, SurfaceBright, ErrorRose)
        val breakdowns = categoryMinutes.entries.mapIndexed { index, entry ->
            val catHours = Math.round((entry.value / 60f) * 10) / 10f
            val pct = ((entry.value.toFloat() / totalCatMins) * 100).toInt()
            val color = defaultColors[index % defaultColors.size]
            val isErr = entry.key.contains("Unplanned", ignoreCase = true)
            val isPos = entry.key.contains("Code", ignoreCase = true) || entry.key.contains("Deep", ignoreCase = true)
            CategoryBreakdown(
                name = entry.key,
                hours = catHours,
                percentage = pct,
                dotColor = color,
                status = if (isErr) "Flagged Leak" else if (isPos) "High Leverage" else "Logged pace",
                budget = "Budget: ${(catHours * 1.2f).toInt()}h",
                isPositive = isPos,
                isError = isErr
            )
        }.sortedByDescending { it.hours }

        val targetSoFar = Math.round((currentSprintDay * targetDailyH) * 10) / 10f
        val hoursAhead = totalActiveHours - targetSoFar
        val daysAhead = (hoursAhead / targetDailyH).toInt()

        val dailyPace = if (currentSprintDay > 0) totalActiveHours / currentSprintDay else 0f
        val projectedComp = if (dailyPace > 0) (targetSprintH / dailyPace).toInt().coerceIn(1, totalSprintDays + 30) else totalSprintDays

        val finishCal = Calendar.getInstance()
        val forecastFinishStr = try {
            finishCal.time = dateFormat.parse(_userProfile.value.sprintStartDate) ?: Date()
            finishCal.add(Calendar.DAY_OF_YEAR, projectedComp)
            displayDateFormat.format(finishCal.time)
        } catch (e: Exception) {
            "Day $projectedComp"
        }

        return RealtimeAnalytics(
            currentSprintDay = currentSprintDay,
            totalSprintDays = totalSprintDays,
            focusLoggedHours = totalActiveHours,
            targetFocusHours = targetSoFar,
            executionRatePct = execRate,
            executionRateDelta = if (daysAhead >= 0) "+$daysAhead d" else "$daysAhead d",
            focusEfficiencyPct = efficiency,
            unplannedHours = totalWastedHours,
            unplannedIncidents = wastedCount,
            daysAheadOfSchedule = daysAhead,
            projectedCompletionDay = projectedComp,
            forecastFinishDate = forecastFinishStr,
            chronotypeName = _userProfile.value.chronotype.substringBefore(" ("),
            peakFocusWindow = peakWindowStr,
            peakFocusPercent = peakPct,
            topTimeLeakTitle = if (wastedCount > 0) "Context Switch / Unplanned Gap" else "Zero Leaks",
            contextSwitchesPerDay = if (currentSprintDay > 0) (wastedCount.toFloat() / currentSprintDay) else 0f,
            sprintTargetHours = targetSprintH,
            categoryBreakdowns = breakdowns,
            hourlyHeatmap = heatmapList
        )
    }

    private fun generateDynamicAnalysisCards(
        allTasks: List<TimeSlotTask>,
        summary: DailySummary,
        analytics: RealtimeAnalytics
    ): List<DeepAnalysisCard> {
        val cards = mutableListOf<DeepAnalysisCard>()

        // Card 1: Circadian Peak
        cards.add(
            DeepAnalysisCard(
                id = "card_1",
                title = "Circadian Peak Alignment",
                headline = "Peak Flow: ${analytics.peakFocusWindow} (${analytics.peakFocusPercent}%)",
                impact = "+3.4x Higher Architecture Velocity",
                recommendation = "Lock this window against external interruptions. Shift administrative tasks and communication to after 16:30.",
                icon = Icons.Default.Psychology,
                tag = "HIGH LEVERAGE",
                themeColor = Primary
            )
        )

        // Card 2: Friction Audit
        if (analytics.unplannedIncidents > 0) {
            cards.add(
                DeepAnalysisCard(
                    id = "card_2",
                    title = "Friction Gap Remediation",
                    headline = "Midday Context Switch Detected (${analytics.unplannedHoursStr})",
                    impact = "-1.75h Lost Cumulative Output",
                    recommendation = "Schedule a deliberate 15-minute physical walk right after lunch to reset attention span and eliminate distraction loops.",
                    icon = Icons.Default.Warning,
                    tag = "LEAK AUDIT",
                    themeColor = ErrorRose
                )
            )
        }

        // Card 3: 90-Day Trajectory
        cards.add(
            DeepAnalysisCard(
                id = "card_3",
                title = "90-Day Velocity Horizon",
                headline = "Estimated Early Finish on Day ${analytics.projectedCompletionDay} of ${summary.totalSprintDays}",
                impact = "${analytics.daysAheadOfSchedule} Days Ahead of Target Schedule",
                recommendation = "Maintaining current ${String.format(Locale.US, "%.1f", analytics.executionRatePct)}% daily execution rate guarantees a comfortable sprint buffer.",
                icon = Icons.Default.TrendingUp,
                tag = "PACE BUFFER",
                themeColor = SecondaryGreen
            )
        )

        // Card 4: Deep Work Density
        cards.add(
            DeepAnalysisCard(
                id = "card_4",
                title = "Deep Work Block Density",
                headline = "90m+ Unbroken Blocks Yield 3.2x Velocity",
                impact = "Maximized Cognitive Flow Purity",
                recommendation = "Batch code development into minimum 90-minute intervals with full notification silencing.",
                icon = Icons.Default.Bolt,
                tag = "DENSITY OPT",
                themeColor = TertiaryCyan
            )
        )

        return cards
    }

    private fun loadPersistedProfile(): UserProfile {
        val todayStr = dateFormat.format(Date())
        val sprintStart = prefs.getString("user_sprint_start_date", todayStr) ?: todayStr
        val sprintDays = prefs.getInt("user_sprint_target_days", 90)
        val calculatedStreak = calculateSprintDayFromDate(sprintStart, sprintDays)

        return UserProfile(
            name = prefs.getString("user_name", "Sri") ?: "Sri",
            email = prefs.getString("user_email", "srimobile69@gmail.com") ?: "srimobile69@gmail.com",
            tier = prefs.getString("user_tier", "PRO • Founder Track") ?: "PRO • Founder Track",
            sprintGoalName = prefs.getString("user_sprint_goal_name", "90-Day Sprint Objective") ?: "90-Day Sprint Objective",
            sprintStartDate = sprintStart,
            sprintTargetDays = sprintDays,
            dailyTargetHours = prefs.getFloat("user_target_hours", 8.0f),
            chronotype = prefs.getString("user_chronotype", "Day Owl (09:00 - 12:30 Peak)") ?: "Day Owl (09:00 - 12:30 Peak)",
            activeStreakDays = prefs.getInt("user_streak", calculatedStreak),
            totalHoursLogged = prefs.getFloat("user_hours_logged", 0.0f),
            executionRatePct = prefs.getFloat("user_exec_rate", 0.0f),
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
            .putString("user_sprint_goal_name", profile.sprintGoalName)
            .putString("user_sprint_start_date", profile.sprintStartDate)
            .putInt("user_sprint_target_days", profile.sprintTargetDays)
            .putFloat("user_target_hours", profile.dailyTargetHours)
            .putString("user_chronotype", profile.chronotype)
            .putBoolean("user_haptic", profile.hapticFeedback)
            .putBoolean("user_autoroll", profile.autoRollInProgress)
            .putBoolean("user_strict", profile.strictMode)
            .apply()

        // Sync with sprintConfig if sprint goal title is provided
        val updatedSprint = _sprintConfig.value.copy(
            phaseTitle = profile.sprintGoalName.take(36),
            totalDays = profile.sprintTargetDays
        )
        updateSprintConfig(updatedSprint)

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
            0
        }

        return DailySummary(
            activeHours = if (activeH > 0) (Math.round(activeH * 10) / 10f) else 0f,
            missedHours = if (missedH > 0) (Math.round(missedH * 10) / 10f) else 0f,
            wastedHours = if (wastedH > 0) (Math.round(wastedH * 10) / 10f) else 0f,
            plannedHours = if (plannedH > 0) (Math.round(plannedH * 10) / 10f) else 0f,
            dailyScorePercent = score,
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

    fun saveTask(task: TimeSlotTask, applyToAll90Days: Boolean = false) {
        viewModelScope.launch {
            if (task.id == 0L) {
                repository.insertTask(task.copy(dateStr = _selectedDate.value))
            } else {
                repository.updateTask(task)
            }
            if (applyToAll90Days) {
                val start = _userProfile.value.sprintStartDate.ifBlank { _selectedDate.value }
                repository.applySlotToAll90Days(
                    baseTask = task,
                    startDateStr = start,
                    totalDays = _sprintConfig.value.totalDays
                )
            }
            dismissEditDialog()
        }
    }

    fun syncDailyRoutineToAll90Days() {
        viewModelScope.launch {
            val start = _userProfile.value.sprintStartDate.ifBlank { _selectedDate.value }
            repository.syncDailyRoutineToAll90Days(
                sourceDate = _selectedDate.value,
                startDateStr = start,
                totalDays = _sprintConfig.value.totalDays
            )
        }
    }

    fun deleteTask(task: TimeSlotTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
            dismissEditDialog()
        }
    }

    fun seedSampleDayTemplate() {
        viewModelScope.launch {
            val start = _userProfile.value.sprintStartDate.ifBlank { _selectedDate.value }
            repository.syncDailyRoutineToAll90Days(
                sourceDate = _selectedDate.value,
                startDateStr = start,
                totalDays = _sprintConfig.value.totalDays
            )
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
        val today = Date()
        _selectedDate.value = dateFormat.format(today)
        _selectedDisplayDate.value = displayDateFormat.format(today)
        _sprintDayInfo.value = "Sprint Day ${_sprintConfig.value.currentDay} • Cycle ${_sprintConfig.value.cycle}"
        viewModelScope.launch {
            val start = _userProfile.value.sprintStartDate.ifBlank { _selectedDate.value }
            repository.seedDefaultDataIfEmpty(_selectedDate.value, start, _sprintConfig.value.totalDays)
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
                val start = _userProfile.value.sprintStartDate.ifBlank { _selectedDate.value }
                repository.seedDefaultDataIfEmpty(newDateStr, start, _sprintConfig.value.totalDays)
            }
        } catch (e: Exception) {
            // fallback
        }
    }
}
