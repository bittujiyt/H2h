package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatMessageEntity
import com.example.data.model.JobApplicationEntity
import com.example.data.model.JobEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // === JOBS ===
    @Query("SELECT * FROM jobs ORDER BY isUrgent DESC, id DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id")
    fun getJobById(id: Long): Flow<JobEntity?>

    @Query("SELECT * FROM jobs WHERE postedByUserId = :employerPhone ORDER BY id DESC")
    fun getJobsByEmployer(employerPhone: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE isSaved = 1 ORDER BY id DESC")
    fun getSavedJobs(): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)

    @Update
    suspend fun updateJob(job: JobEntity)

    @Delete
    suspend fun deleteJob(job: JobEntity)

    @Query("UPDATE jobs SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: Long, isSaved: Boolean)

    // === PROFILES ===
    @Query("SELECT * FROM user_profiles WHERE phone = :phone LIMIT 1")
    fun getUserProfile(phone: String): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    // === APPLICATIONS ===
    @Query("SELECT * FROM job_applications WHERE applicantPhone = :phone ORDER BY appliedAt DESC")
    fun getApplicationsForSeeker(phone: String): Flow<List<JobApplicationEntity>>

    @Query("SELECT * FROM job_applications WHERE jobId = :jobId ORDER BY appliedAt DESC")
    fun getApplicationsForJob(jobId: Long): Flow<List<JobApplicationEntity>>

    @Query("SELECT * FROM job_applications ORDER BY appliedAt DESC")
    fun getAllApplications(): Flow<List<JobApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: JobApplicationEntity): Long

    @Query("UPDATE job_applications SET status = :status WHERE id = :id")
    suspend fun updateApplicationStatus(id: Long, status: String)

    // === CHAT MESSAGES ===
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE senderPhone = :userPhone OR recipientPhone = :userPhone ORDER BY timestamp DESC")
    fun getAllMessagesForUser(userPhone: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("UPDATE chat_messages SET isRead = 1 WHERE conversationId = :conversationId")
    suspend fun markMessagesAsRead(conversationId: String)

    // === NOTIFICATIONS ===
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()
}
