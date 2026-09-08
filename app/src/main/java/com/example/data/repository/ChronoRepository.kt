package com.example.data.repository

import com.example.data.local.GoalDao
import com.example.data.local.TaskDao
import com.example.data.model.GoalItem
import com.example.data.model.TaskStatus
import com.example.data.model.TimeSlotTask
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChronoRepository(
    private val taskDao: TaskDao,
    private val goalDao: GoalDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTasksForDate(dateStr: String): Flow<List<TimeSlotTask>> =
        taskDao.getTasksForDate(dateStr)

    suspend fun getTasksForDateOnce(dateStr: String): List<TimeSlotTask> =
        taskDao.getTasksForDateOnce(dateStr)

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
                title = "90-Day Sprint: 720h Deep Focus",
                category = "#DeepWork",
                targetValue = 720,
                currentValue = 0,
                unit = "Hours"
            ),
            GoalItem(
                title = "90-Day Sprint: 10 Architecture Engines",
                category = "#Code",
                targetValue = 10,
                currentValue = 0,
                unit = "Modules"
            ),
            GoalItem(
                title = "90-Day Sprint: 12 Technical Dispatches",
                category = "#Growth",
                targetValue = 12,
                currentValue = 0,
                unit = "Articles"
            ),
            GoalItem(
                title = "90-Day Sprint: 75 Physical Conditioning",
                category = "#Fitness",
                targetValue = 75,
                currentValue = 0,
                unit = "Workouts"
            ),
            GoalItem(
                title = "90-Day Sprint: Zero Routine Leakage",
                category = "#Routine",
                targetValue = 90,
                currentValue = 0,
                unit = "Days Clean"
            )
        )
        goalDao.insertGoals(defaultGoals)
    }

    fun getDailyRoutineTemplate(targetDate: String): List<TimeSlotTask> {
        return listOf(
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 0,
                startMinute = 0,
                endHour = 6,
                endMinute = 0,
                title = "Night Rest & Sleep Recovery",
                description = "Optimal restorative sleep cycle in 24h cadence (00:00 - 06:00)",
                categoryTag = "#Health",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 6,
                startMinute = 0,
                endHour = 7,
                endMinute = 0,
                title = "Morning Routine & Sun Exposure",
                description = "Electrolytes + 15m outdoor walk for circadian anchoring (06:00 - 07:00)",
                categoryTag = "#Routine",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 7,
                startMinute = 0,
                endHour = 8,
                endMinute = 30,
                title = "Cardio & Physical Conditioning",
                description = "Zone-2 steady running + kettlebell mobility session (07:00 - 08:30)",
                categoryTag = "#Fitness",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 8,
                startMinute = 30,
                endHour = 9,
                endMinute = 0,
                title = "Nutrition Fuel & Sprint Kickoff",
                description = "Protein intake, review top 3 high-leverage milestones (08:30 - 09:00)",
                categoryTag = "#LifeOps",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 9,
                startMinute = 0,
                endHour = 12,
                endMinute = 0,
                title = "Deep Focus: Core Architecture",
                description = "Deep execution block • Zero notifications & distraction free (09:00 - 12:00)",
                categoryTag = "#DeepWork",
                secondaryTag = "#SprintPriority",
                status = TaskStatus.PLANNED,
                outputNote = "Target: High-Leverage Architecture Modules"
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 12,
                startMinute = 0,
                endHour = 13,
                endMinute = 0,
                title = "Lunch & Fasting Break",
                description = "Nutritious meal + fresh air recovery (12:00 - 13:00)",
                categoryTag = "#Health",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 13,
                startMinute = 0,
                endHour = 15,
                endMinute = 0,
                title = "Engineering Operations & Code Review",
                description = "Review Pull Requests, automated test runner audits (13:00 - 15:00)",
                categoryTag = "#Code",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 15,
                startMinute = 0,
                endHour = 17,
                endMinute = 0,
                title = "Feature Development & Integration",
                description = "Full stack implementation of high-priority tickets (15:00 - 17:00)",
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
                description = "Document system interfaces & update sprint backlog (17:00 - 18:30)",
                categoryTag = "#Growth",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 18,
                startMinute = 30,
                endHour = 20,
                endMinute = 0,
                title = "Evening Recovery & Nutrition",
                description = "Wholesome dinner & disconnected downtime (18:30 - 20:00)",
                categoryTag = "#LifeOps",
                status = TaskStatus.PLANNED
            ),
            TimeSlotTask(
                dateStr = targetDate,
                startHour = 20,
                startMinute = 0,
                endHour = 22,
                endMinute = 0,
                title = "Side Project Innovation",
                description = "Exploration of novel tools & micro-experiments (20:00 - 22:00)",
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
                description = "Review execution score, journal wins, plan tomorrow (22:00 - 23:00)",
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
                description = "Wind-down environment configured for sleep (23:00 - 24:00)",
                categoryTag = "#SleepPrep",
                status = TaskStatus.PLANNED
            )
        )
    }

    suspend fun seedSampleDayTemplate(targetDate: String) {
        val sampleTasks = getDailyRoutineTemplate(targetDate)
        taskDao.insertTasks(sampleTasks)
    }

    suspend fun syncDailyRoutineToAll90Days(
        sourceDate: String,
        startDateStr: String,
        totalDays: Int = 90
    ) {
        val sourceTasks = taskDao.getTasksForDateOnce(sourceDate)
        val templateToUse = if (sourceTasks.isNotEmpty()) sourceTasks else getDailyRoutineTemplate(sourceDate)

        val cal = Calendar.getInstance()
        val startDate = try {
            dateFormat.parse(startDateStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }

        val allGeneratedTasks = mutableListOf<TimeSlotTask>()

        for (dayIndex in 0 until totalDays) {
            cal.time = startDate
            cal.add(Calendar.DAY_OF_YEAR, dayIndex)
            val dayDateStr = dateFormat.format(cal.time)

            // If it is the source date, keep existing task statuses, otherwise clone as template
            if (dayDateStr == sourceDate && sourceTasks.isNotEmpty()) {
                continue
            }

            // Remove existing for this date to replace with unified 90-day daily routine
            taskDao.deleteTasksForDate(dayDateStr)

            val dayTasks = templateToUse.map { task ->
                TimeSlotTask(
                    id = 0L,
                    dateStr = dayDateStr,
                    startHour = task.startHour,
                    startMinute = task.startMinute,
                    endHour = task.endHour,
                    endMinute = task.endMinute,
                    title = task.title,
                    description = task.description,
                    categoryTag = task.categoryTag,
                    secondaryTag = task.secondaryTag,
                    status = TaskStatus.PLANNED,
                    outputNote = task.outputNote,
                    wastedMinutes = 0
                )
            }
            allGeneratedTasks.addAll(dayTasks)
        }

        if (allGeneratedTasks.isNotEmpty()) {
            taskDao.insertTasks(allGeneratedTasks)
        }
    }

    suspend fun applySlotToAll90Days(
        baseTask: TimeSlotTask,
        startDateStr: String,
        totalDays: Int = 90
    ) {
        val cal = Calendar.getInstance()
        val startDate = try {
            dateFormat.parse(startDateStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }

        val slots = mutableListOf<TimeSlotTask>()
        for (dayIndex in 0 until totalDays) {
            cal.time = startDate
            cal.add(Calendar.DAY_OF_YEAR, dayIndex)
            val dayDateStr = dateFormat.format(cal.time)

            if (dayDateStr == baseTask.dateStr) {
                continue
            }

            slots.add(
                TimeSlotTask(
                    id = 0L,
                    dateStr = dayDateStr,
                    startHour = baseTask.startHour,
                    startMinute = baseTask.startMinute,
                    endHour = baseTask.endHour,
                    endMinute = baseTask.endMinute,
                    title = baseTask.title,
                    description = baseTask.description,
                    categoryTag = baseTask.categoryTag,
                    secondaryTag = baseTask.secondaryTag,
                    status = TaskStatus.PLANNED,
                    outputNote = baseTask.outputNote,
                    wastedMinutes = 0
                )
            )
        }

        if (slots.isNotEmpty()) {
            taskDao.insertTasks(slots)
        }
    }

    suspend fun seedDefaultDataIfEmpty(
        defaultDate: String,
        startDateStr: String = defaultDate,
        totalDays: Int = 90
    ) {
        val count = taskDao.getTaskCount()
        if (count == 0) {
            // Seed the 24-hour daily routine across all 90 days of the sprint
            syncDailyRoutineToAll90Days(defaultDate, startDateStr, totalDays)
        } else {
            // Check if current date has tasks, if not seed it from routine template
            val existingForDate = taskDao.getTasksForDateOnce(defaultDate)
            if (existingForDate.isEmpty()) {
                val routineTasks = getDailyRoutineTemplate(defaultDate)
                taskDao.insertTasks(routineTasks)
            }
        }

        if (goalDao.getGoalCount() == 0) {
            val initialGoals = listOf(
                GoalItem(
                    title = "90-Day Sprint: 720h Deep Focus",
                    category = "#DeepWork",
                    targetValue = 720,
                    currentValue = 0,
                    unit = "Hours"
                ),
                GoalItem(
                    title = "90-Day Sprint: 10 Architecture Engines",
                    category = "#Code",
                    targetValue = 10,
                    currentValue = 0,
                    unit = "Modules"
                ),
                GoalItem(
                    title = "90-Day Sprint: 12 Technical Dispatches",
                    category = "#Growth",
                    targetValue = 12,
                    currentValue = 0,
                    unit = "Articles"
                ),
                GoalItem(
                    title = "90-Day Sprint: 75 Physical Conditioning",
                    category = "#Fitness",
                    targetValue = 75,
                    currentValue = 0,
                    unit = "Workouts"
                ),
                GoalItem(
                    title = "90-Day Sprint: Zero Routine Leakage",
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
