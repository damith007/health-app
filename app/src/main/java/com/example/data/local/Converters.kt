package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String = status.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = try {
        TaskStatus.valueOf(value)
    } catch (e: Exception) {
        TaskStatus.PLANNED
    }
}
