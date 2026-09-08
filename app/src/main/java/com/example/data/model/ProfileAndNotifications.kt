package com.example.data.model

data class UserProfile(
    val name: String = "Sri",
    val email: String = "srimobile69@gmail.com",
    val tier: String = "PRO • Founder Track",
    val sprintGoalName: String = "90-Day Sprint Objective",
    val sprintStartDate: String = "2026-09-08",
    val sprintTargetDays: Int = 90,
    val dailyTargetHours: Float = 8.0f,
    val chronotype: String = "Day Owl (09:00 - 12:30 Peak)",
    val activeStreakDays: Int = 1,
    val totalHoursLogged: Float = 0.0f,
    val executionRatePct: Float = 0.0f,
    val hapticFeedback: Boolean = true,
    val autoRollInProgress: Boolean = true,
    val strictMode: Boolean = true
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    SLOT_ACTIVE,
    SPRINT_MILESTONE,
    CIRCADIAN_FLOW,
    LEAK_WARNING,
    RECOVERY_PROMPT
}
