package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.GoalItem
import com.example.data.model.TaskStatus
import com.example.data.model.TimeSlotTask
import com.example.data.repository.ChronoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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

data class DailySummary(
    val activeHours: Float = 14.0f,
    val missedHours: Float = 2.0f,
    val wastedHours: Float = 1.0f,
    val plannedHours: Float = 7.0f,
    val dailyScorePercent: Int = 78,
    val sprintDay: Int = 14,
    val totalSprintDays: Int = 90
)

class ChronoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ChronoRepository
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayDateFormat = SimpleDateFormat("EEE, MMM d", Locale.US)

    private val currentCalendar = Calendar.getInstance()

    // Default to Wed, Oct 23 2026 or today's date
    private val _selectedDate = MutableStateFlow("2026-10-23")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedDisplayDate = MutableStateFlow("Wed, Oct 23")
    val selectedDisplayDate: StateFlow<String> = _selectedDisplayDate.asStateFlow()

    private val _sprintDayInfo = MutableStateFlow("Sprint Day 14 • Cycle 1")
    val sprintDayInfo: StateFlow<String> = _sprintDayInfo.asStateFlow()

    private val _currentTab = MutableStateFlow(ChronoTab.TODAY_GRID)
    val currentTab: StateFlow<ChronoTab> = _currentTab.asStateFlow()

    // Dialog state for adding/editing a task
    private val _editingTask = MutableStateFlow<TimeSlotTask?>(null)
    val editingTask: StateFlow<TimeSlotTask?> = _editingTask.asStateFlow()

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    // Analytics range filter
    private val _analyticsRange = MutableStateFlow("7 Days")
    val analyticsRange: StateFlow<String> = _analyticsRange.asStateFlow()

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

    val dailySummary: StateFlow<DailySummary> = tasks.combine(_selectedDate) { taskList, _ ->
        calculateSummary(taskList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailySummary()
    )

    private fun calculateSummary(taskList: List<TimeSlotTask>): DailySummary {
        if (taskList.isEmpty()) {
            return DailySummary(0f, 0f, 0f, 0f, 0, 14, 90)
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
            sprintDay = 14,
            totalSprintDays = 90
        )
    }

    fun selectTab(tab: ChronoTab) {
        _currentTab.value = tab
    }

    fun setAnalyticsRange(range: String) {
        _analyticsRange.value = range
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

    fun previousDay() {
        adjustDay(-1)
    }

    fun nextDay() {
        adjustDay(1)
    }

    fun jumpToToday() {
        _selectedDate.value = "2026-10-23"
        _selectedDisplayDate.value = "Wed, Oct 23"
        _sprintDayInfo.value = "Sprint Day 14 • Cycle 1"
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
            val dayOfYear = currentCalendar.get(Calendar.DAY_OF_YEAR) % 90 + 1
            _sprintDayInfo.value = "Sprint Day $dayOfYear • Cycle 1"

            viewModelScope.launch {
                repository.seedDefaultDataIfEmpty(newDateStr)
            }
        } catch (e: Exception) {
            // fallback
        }
    }
}
