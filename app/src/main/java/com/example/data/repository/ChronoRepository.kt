package com.example.data.repository

import com.example.data.local.GoalDao
import com.example.data.local.TaskDao
import com.example.data.model.GoalItem
import com.example.data.model.TaskStatus
import com.example.data.model.TimeSlotTask
import kotlinx.coroutines.flow.Flow

class ChronoRepository(
    private val taskDao: TaskDao,
    private val goalDao: GoalDao
) {
    fun getTasksForDate(dateStr: String): Flow<List<TimeSlotTask>> =
        taskDao.getTasksForDate(dateStr)

    fun getAllTasks(): Flow<List<TimeSlotTask>> =
        taskDao.getAllTasks()

    fun getAllGoals(): Flow<List<GoalItem>> =
        goalDao.getAllGoals()

    suspend fun insertTask(task: TimeSlotTask): Long =
        taskDao.insertTask(task)

    suspend fun updateTask(task: TimeSlotTask) =
        taskDao.updateTask(task)

    suspend fun deleteTask(task: TimeSlotTask) =
        taskDao.deleteTask(task)

    suspend fun insertGoal(goal: GoalItem): Long =
        goalDao.insertGoal(goal)

    suspend fun updateGoal(goal: GoalItem) =
        goalDao.updateGoal(goal)

    suspend fun deleteGoal(goal: GoalItem) =
        goalDao.deleteGoal(goal)

    suspend fun resetAllGoalsProgress() =
        goalDao.resetAllGoalsProgress()

    suspend fun resetGoalsToSprintDefaults() {
        goalDao.clearAllGoals()
        val defaultGoals = listOf(
            GoalItem(
                title = "900h Deep Work Sprint",
                category = "#DeepWork",
                targetValue = 900,
                currentValue = 0,
                unit = "Hours"
            ),
            GoalItem(
                title = "Production Architecture Engine",
                category = "#Code",
                targetValue = 10,
                currentValue = 0,
                unit = "Modules"
            ),
            GoalItem(
                title = "Engineering Dispatch Series",
                category = "#Growth",
                targetValue = 12,
                currentValue = 0,
                unit = "Articles"
            ),
            GoalItem(
                title = "Zone-2 Cardio & Strength",
                category = "#Fitness",
                targetValue = 75,
                currentValue = 0,
                unit = "Workouts"
            ),
            GoalItem(
                title = "Zero Post-Lunch Unplanned Gap",
                category = "#Routine",
                targetValue = 90,
                currentValue = 0,
                unit = "Days Clean"
            )
        )
        goalDao.insertGoals(defaultGoals)
    }

    suspend fun seedSampleDayTemplate(targetDate: String) {
        val sampleTasks = listOf(
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 0,
                startMinute = 0,
                endHour = 6,
                endMinute = 0,
                title = "Night Rest & Sleep Recovery",
                description = "Optimal restorative sleep cycle • Deep & REM tracking active",
                categoryTag = "#Health",
                status = TaskStatus.COMPLETED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 6,
                startMinute = 0,
                endHour = 7,
                endMinute = 0,
                title = "Morning Routine & Sun Exposure",
                description = "Electrolytes + 15m outdoor walk for circadian anchoring",
                categoryTag = "#Routine",
                status = TaskStatus.COMPLETED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 7,
                startMinute = 0,
                endHour = 8,
                endMinute = 30,
                title = "Cardio & Physical Conditioning",
                description = "Zone-2 steady running + kettlebell mobility session",
                categoryTag = "#Fitness",
                status = TaskStatus.COMPLETED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 8,
                startMinute = 30,
                endHour = 9,
                endMinute = 0,
                title = "Nutrition Fuel & Sprint Kickoff",
                description = "Protein intake, review top 3 high-leverage milestones",
                categoryTag = "#LifeOps",
                status = TaskStatus.COMPLETED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 9,
                startMinute = 0,
                endHour = 12,
                endMinute = 0,
                title = "Deep Focus: Core Architecture",
                description = "Deep execution block • Zero notifications & distraction free",
                categoryTag = "#DeepWork",
                secondaryTag = "#SprintPriority",
                status = TaskStatus.COMPLETED,
                outputNote = "Output: 3 Core Modules Implemented"
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 12,
                startMinute = 0,
                endHour = 13,
                endMinute = 0,
                title = "Lunch & Walking Break",
                description = "Nutritious meal + fresh air recovery",
                categoryTag = "#Health",
                status = TaskStatus.COMPLETED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 13,
                startMinute = 0,
                endHour = 15,
                endMinute = 0,
                title = "Engineering Operations & Code Review",
                description = "Review Pull Requests, automated test runner audits",
                categoryTag = "#Code",
                status = TaskStatus.IN_PROGRESS
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 15,
                startMinute = 0,
                endHour = 17,
                endMinute = 0,
                title = "Feature Development & Integration",
                description = "Full stack implementation of high-priority tickets",
                categoryTag = "#DeepWork",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 17,
                startMinute = 0,
                endHour = 18,
                endMinute = 30,
                title = "Sprint Documentation & Writing",
                description = "Document system interfaces & update knowledge base",
                categoryTag = "#Growth",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 18,
                startMinute = 30,
                endHour = 20,
                endMinute = 0,
                title = "Evening Recovery & Social Connection",
                description = "Dinner & disconnected downtime",
                categoryTag = "#Life",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 20,
                startMinute = 0,
                endHour = 22,
                endMinute = 0,
                title = "Side Project Innovation",
                description = "Exploration of novel tools & micro-experiments",
                categoryTag = "#SideProject",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 22,
                startMinute = 0,
                endHour = 23,
                endMinute = 0,
                title = "Daily Retrospective & Screen Shutdown",
                description = "Review execution score, journal wins, plan tomorrow",
                categoryTag = "#Recovery",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 23,
                startMinute = 0,
                endHour = 24,
                endMinute = 0,
                title = "Night Rest Preparation",
                description = "Wind-down environment configured for sleep",
                categoryTag = "#SleepPrep",
                status = TaskStatus.PLANNED
            )
        )
        taskDao.insertTasks(sampleTasks)
    }

    suspend fun seedDefaultDataIfEmpty(defaultDate: String) {
        if (taskDao.getTaskCount() == 0) {
            val initialTasks = listOf(
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 0,
                    startMinute = 0,
                    endHour = 6,
                    endMinute = 0,
                    title = "Night Rest & Deep Sleep Cycle",
                    description = "Oura Score: 89 • Recovery target met (REM + Deep: 3.4h)",
                    categoryTag = "#Health",
                    status = TaskStatus.COMPLETED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 6,
                    startMinute = 0,
                    endHour = 7,
                    endMinute = 0,
                    title = "Morning Hydration & Sunlight Exposure",
                    description = "750ml water with electrolytes + 15m outdoor walk",
                    categoryTag = "#Routine",
                    status = TaskStatus.COMPLETED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 7,
                    startMinute = 0,
                    endHour = 8,
                    endMinute = 30,
                    title = "HIIT Track Run & Kettlebell Complex",
                    description = "5.2 km continuous run + 4 sets clean & presses",
                    categoryTag = "#Fitness",
                    status = TaskStatus.COMPLETED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 8,
                    startMinute = 30,
                    endHour = 9,
                    endMinute = 0,
                    title = "Protein Fuel & Daily Sprint Brief",
                    description = "3 eggs + avocado shake, review top 3 needle-movers",
                    categoryTag = "#LifeOps",
                    status = TaskStatus.COMPLETED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 9,
                    startMinute = 0,
                    endHour = 11,
                    endMinute = 0,
                    title = "Core Engine Architecture & Refactor",
                    description = "Zero-latency state synchronization layer implemented in Rust",
                    categoryTag = "#DeepWork",
                    secondaryTag = "#SaaS",
                    status = TaskStatus.COMPLETED,
                    outputNote = "Output: 124 LOC + 18 Unit Tests"
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 11,
                    startMinute = 0,
                    endHour = 12,
                    endMinute = 0,
                    title = "PR Merges & Database Schema Verification",
                    description = "Approved 4 team PRs; resolved migration contention",
                    categoryTag = "#Code",
                    status = TaskStatus.COMPLETED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 12,
                    startMinute = 0,
                    endHour = 13,
                    endMinute = 0,
                    title = "Nutritional Fasting Break & Walk",
                    description = "Low-carb bowl, 10m sunshine & breathwork",
                    categoryTag = "#Health",
                    status = TaskStatus.COMPLETED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 13,
                    startMinute = 0,
                    endHour = 14,
                    endMinute = 0,
                    title = "Social Media Rabbit Hole & Distraction",
                    description = "Doomscrolling tech Twitter threads • Context switch penalty incurred",
                    categoryTag = "#Unplanned",
                    status = TaskStatus.WASTED,
                    outputNote = "Impact: -12% Daily Focus Ratio",
                    wastedMinutes = 45
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 14,
                    startMinute = 0,
                    endHour = 15,
                    endMinute = 30,
                    title = "Client Strategy Call & Demo Prep",
                    description = "Counterparty conflict; moved to Thursday 10:00 AM slot",
                    categoryTag = "#Revenue",
                    status = TaskStatus.RESCHEDULED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 15,
                    startMinute = 30,
                    endHour = 17,
                    endMinute = 0,
                    title = "System Stress Testing & Bug Bounty",
                    description = "Emulating 50k concurrent websockets on cluster 04. Auditing backpressure throttle queues.",
                    categoryTag = "#Code",
                    secondaryTag = "#SprintPriority",
                    status = TaskStatus.IN_PROGRESS
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 17,
                    startMinute = 0,
                    endHour = 18,
                    endMinute = 30,
                    title = "Content Writing: Sprint Dispatch",
                    description = "Draft sprint engineering dispatch on Substack + X breakdown",
                    categoryTag = "#Growth",
                    status = TaskStatus.PLANNED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 18,
                    startMinute = 30,
                    endHour = 19,
                    endMinute = 30,
                    title = "Evening Zone-2 Walk & Audio Book",
                    description = "High Output Management (Chapters 5-7) • Recovery pace",
                    categoryTag = "#Mindset",
                    status = TaskStatus.PLANNED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 19,
                    startMinute = 30,
                    endHour = 20,
                    endMinute = 30,
                    title = "Dinner & Family Presence",
                    description = "No screens policy • Wholesome dinner",
                    categoryTag = "#Life",
                    status = TaskStatus.PLANNED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 20,
                    startMinute = 30,
                    endHour = 22,
                    endMinute = 0,
                    title = "AI Agent Pipeline Integration",
                    description = "Plug-in automated benchmark tests & prompt evaluation loop",
                    categoryTag = "#SideProject",
                    status = TaskStatus.PLANNED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 22,
                    startMinute = 0,
                    endHour = 23,
                    endMinute = 0,
                    title = "Daily Review, Journaling & Screen Cutoff",
                    description = "Fill daily sprint retrospective score • Red lens glasses on",
                    categoryTag = "#Recovery",
                    status = TaskStatus.PLANNED
                ),
                TimeSlotTask(
                    dateStr = defaultDate,
                    startHour = 23,
                    startMinute = 0,
                    endHour = 24,
                    endMinute = 0,
                    title = "Target 8.0h Sleep Chamber Environment",
                    description = "Thermostat set to 19°C • White noise generator running",
                    categoryTag = "#SleepPrep",
                    status = TaskStatus.PLANNED
                )
            )
            taskDao.insertTasks(initialTasks)
        }

        if (goalDao.getGoalCount() == 0) {
            val initialGoals = listOf(
                GoalItem(
                    title = "900h Deep Work Sprint",
                    category = "#DeepWork",
                    targetValue = 900,
                    currentValue = 0,
                    unit = "Hours"
                ),
                GoalItem(
                    title = "Production Architecture Engine",
                    category = "#Code",
                    targetValue = 10,
                    currentValue = 0,
                    unit = "Modules"
                ),
                GoalItem(
                    title = "Engineering Dispatch Series",
                    category = "#Growth",
                    targetValue = 12,
                    currentValue = 0,
                    unit = "Articles"
                ),
                GoalItem(
                    title = "Zone-2 Cardio & Strength",
                    category = "#Fitness",
                    targetValue = 75,
                    currentValue = 0,
                    unit = "Workouts"
                ),
                GoalItem(
                    title = "Zero Post-Lunch Unplanned Gap",
                    category = "#Routine",
                    targetValue = 90,
                    currentValue = 0,
                    unit = "Days Clean"
                )
            )
            goalDao.insertGoals(initialGoals)
        }
    }
}
