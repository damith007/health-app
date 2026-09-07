package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskStatus(val displayName: String) {
    COMPLETED("Completed"),
    IN_PROGRESS("In Progress"),
    PLANNED("Planned"),
    WASTED("Wasted"),
    RESCHEDULED("Rescheduled");

    fun nextToggle(): TaskStatus = when (this) {
        PLANNED -> COMPLETED
        COMPLETED -> WASTED
        WASTED -> IN_PROGRESS
        IN_PROGRESS -> RESCHEDULED
        RESCHEDULED -> PLANNED
    }
}

@Entity(tableName = "time_slot_tasks")
data class TimeSlotTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateStr: String, // e.g. "2026-10-23" or formatted date
    val startHour: Int,
    val startMinute: Int = 0,
    val endHour: Int,
    val endMinute: Int = 0,
    val title: String,
    val description: String = "",
    val categoryTag: String = "#DeepWork",
    val secondaryTag: String = "",
    val status: TaskStatus = TaskStatus.PLANNED,
    val outputNote: String = "",
    val wastedMinutes: Int = 0
) {
    val durationFormatted: String
        get() {
            val totalMinutes = (endHour * 60 + endMinute) - (startHour * 60 + startMinute)
            val positiveMin = if (totalMinutes <= 0) 60 else totalMinutes
            val hours = positiveMin / 60
            val minutes = positiveMin % 60
            return when {
                hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
                hours > 0 -> "${hours}h 00m"
                else -> "${minutes}m"
            }
        }

    val startTimeFormatted: String
        get() = String.format("%02d:%02d", startHour, startMinute)

    val endTimeFormatted: String
        get() = String.format("%02d:%02d", endHour, endMinute)
}
