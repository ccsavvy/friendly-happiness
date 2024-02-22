package com.example.taskmanager.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.util.Calendar

// TODO: add calendar and see
@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val name: String,
    val desc: String,
    val checked: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    val userID: String,
    val custCalendar: Long,
    @PrimaryKey (autoGenerate = true) val id: Int = 0
): Parcelable{
    val createDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)

}