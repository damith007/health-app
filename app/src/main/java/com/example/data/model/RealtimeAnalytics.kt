package com.example.data.model

import androidx.compose.ui.graphics.Color
import java.util.Locale

data class HourlyDensity(
    val hourLabel: String,
    val densityPercent: Int
)

data class CategoryBreakdown(
    val name: String,
    val hours: Float,
    val percentage: Int,
    val dotColor: Color,
    val status: String,
    val budget: String,
    val isPositive: Boolean = false,
    val isError: Boolean = false
) {
    val hoursFormatted: String
        get() = String.format(Locale.US, "%.1fh", hours)
}

data class CategoryAllocationItem(
    val tag: String,
    val hours: Float,
    val percentage: Int,
    val budgetHours: Float,
    val status: String,
    val dotColor: Color,
    val isPositive: Boolean = false,
    val isError: Boolean = false
)

data class FrictionLeakItem(
    val title: String,
    val hours: Float,
    val incidents: Int,
    val trigger: String
)

data class RealtimeAnalytics(
    val focusLoggedHours: Float = 64.5f,
    val targetFocusHours: Float = 72.0f,
    val executionRatePct: Float = 81.4f,
    val executionRateDelta: String = "+4.2%",
    val focusEfficiencyPct: Int = 86,
    val unplannedHours: Float = 1.75f,
    val unplannedIncidents: Int = 3,
    val daysAheadOfSchedule: Int = 4,
    val projectedCompletionDay: Int = 86,
    val forecastFinishDate: String = "Nov 28",
    val chronotypeName: String = "Day Owl",
    val peakFocusWindow: String = "09:00 - 12:30",
    val peakFocusPercent: Int = 94,
    val topTimeLeakTitle: String = "Social Rabbit Hole",
    val contextSwitchesPerDay: Float = 4.2f,
    val sprintTargetHours: Float = 810.0f,
    val categoryBreakdowns: List<CategoryBreakdown> = emptyList(),
    val hourlyHeatmap: List<HourlyDensity> = emptyList()
) {
    val unplannedHoursStr: String
        get() {
            val totalMinutes = (unplannedHours * 60).toInt()
            val h = totalMinutes / 60
            val m = totalMinutes % 60
            return if (h > 0) "${h}h ${m}m" else "${m}m"
        }
}

data class ActiveTaskProgress(
    val task: TimeSlotTask,
    val remainingMinutes: Int,
    val elapsedMinutes: Int,
    val totalMinutes: Int
) {
    val progressFraction: Float
        get() = if (totalMinutes > 0) (elapsedMinutes.toFloat() / totalMinutes.toFloat()).coerceIn(0f, 1f) else 0f
}
