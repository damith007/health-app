package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GoalItem
import com.example.data.model.TimeSlotTask
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM time_slot_tasks WHERE dateStr = :date ORDER BY startHour ASC, startMinute ASC")
    fun getTasksForDate(date: String): Flow<List<TimeSlotTask>>

    @Query("SELECT * FROM time_slot_tasks WHERE dateStr = :date ORDER BY startHour ASC, startMinute ASC")
    suspend fun getTasksForDateOnce(date: String): List<TimeSlotTask>

    @Query("SELECT * FROM time_slot_tasks ORDER BY startHour ASC, startMinute ASC")
    fun getAllTasks(): Flow<List<TimeSlotTask>>

    @Query("SELECT * FROM time_slot_tasks ORDER BY startHour ASC, startMinute ASC")
    suspend fun getAllTasksOnce(): List<TimeSlotTask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TimeSlotTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TimeSlotTask>)

    @Update
    suspend fun updateTask(task: TimeSlotTask)

    @Delete
    suspend fun deleteTask(task: TimeSlotTask)

    @Query("DELETE FROM time_slot_tasks WHERE dateStr = :date")
    suspend fun deleteTasksForDate(date: String)

    @Query("DELETE FROM time_slot_tasks WHERE title = :title AND startHour = :startHour AND startMinute = :startMinute")
    suspend fun deleteMatchingSlots(title: String, startHour: Int, startMinute: Int)

    @Query("DELETE FROM time_slot_tasks")
    suspend fun clearAllTasks()

    @Query("SELECT COUNT(*) FROM time_slot_tasks")
    suspend fun getTaskCount(): Int
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals_90 ORDER BY id ASC")
    fun getAllGoals(): Flow<List<GoalItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalItem>)

    @Update
    suspend fun updateGoal(goal: GoalItem)

    @Delete
    suspend fun deleteGoal(goal: GoalItem)

    @Query("DELETE FROM goals_90")
    suspend fun clearAllGoals()

    @Query("UPDATE goals_90 SET currentValue = 0, isCompleted = 0")
    suspend fun resetAllGoalsProgress()

    @Query("SELECT COUNT(*) FROM goals_90")
    suspend fun getGoalCount(): Int
}
