package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: String, // E.g., "${jobId}_${userPhone}"
    val jobId: Long,
    val jobTitle: String,
    val companyName: String,
    val senderPhone: String,
    val recipientPhone: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromJobSeeker: Boolean = true,
    val isRead: Boolean = true
)
