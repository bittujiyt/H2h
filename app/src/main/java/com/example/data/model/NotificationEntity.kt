package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "JOB_ALERT", // JOB_ALERT, APPLICATION_UPDATE, MESSAGE, HIRING
    val isRead: Boolean = false,
    val relatedJobId: Long? = null
)
