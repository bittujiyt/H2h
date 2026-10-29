package com.example.data.repository

import com.example.data.dao.AppDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.JobApplicationEntity
import com.example.data.model.JobEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class JobRepository(private val appDao: AppDao) {

    // === JOBS ===
    val allJobs: Flow<List<JobEntity>> = appDao.getAllJobs()
    val savedJobs: Flow<List<JobEntity>> = appDao.getSavedJobs()

    fun getJobById(id: Long): Flow<JobEntity?> = appDao.getJobById(id)

    fun getJobsByEmployer(employerPhone: String): Flow<List<JobEntity>> =
        appDao.getJobsByEmployer(employerPhone)

    suspend fun insertJob(job: JobEntity): Long = appDao.insertJob(job)

    suspend fun updateJob(job: JobEntity) = appDao.updateJob(job)

    suspend fun deleteJob(job: JobEntity) = appDao.deleteJob(job)

    suspend fun toggleSaveJob(id: Long, isSaved: Boolean) =
        appDao.updateSavedStatus(id, isSaved)

    // === PROFILE ===
    fun getUserProfile(phone: String): Flow<UserProfileEntity?> =
        appDao.getUserProfile(phone)

    suspend fun saveUserProfile(profile: UserProfileEntity) =
        appDao.insertOrUpdateProfile(profile)

    // === APPLICATIONS ===
    fun getApplicationsForSeeker(phone: String): Flow<List<JobApplicationEntity>> =
        appDao.getApplicationsForSeeker(phone)

    fun getApplicationsForJob(jobId: Long): Flow<List<JobApplicationEntity>> =
        appDao.getApplicationsForJob(jobId)

    val allApplications: Flow<List<JobApplicationEntity>> = appDao.getAllApplications()

    suspend fun applyForJob(application: JobApplicationEntity): Long =
        appDao.insertApplication(application)

    suspend fun updateApplicationStatus(id: Long, status: String) =
        appDao.updateApplicationStatus(id, status)

    // === CHAT ===
    fun getMessages(conversationId: String): Flow<List<ChatMessageEntity>> =
        appDao.getMessagesForConversation(conversationId)

    fun getAllUserMessages(phone: String): Flow<List<ChatMessageEntity>> =
        appDao.getAllMessagesForUser(phone)

    suspend fun sendMessage(message: ChatMessageEntity): Long =
        appDao.insertMessage(message)

    suspend fun markMessagesAsRead(conversationId: String) =
        appDao.markMessagesAsRead(conversationId)

    // === NOTIFICATIONS ===
    val allNotifications: Flow<List<NotificationEntity>> = appDao.getAllNotifications()
    val unreadCount: Flow<Int> = appDao.getUnreadNotificationCount()

    suspend fun addNotification(notification: NotificationEntity): Long =
        appDao.insertNotification(notification)

    suspend fun markNotificationAsRead(id: Long) =
        appDao.markNotificationAsRead(id)

    suspend fun markAllNotificationsAsRead() =
        appDao.markAllNotificationsAsRead()

    suspend fun clearNotifications() =
        appDao.clearAllNotifications()
}
