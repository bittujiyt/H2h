package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.JobApplicationEntity
import com.example.data.model.JobEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.JobRepository
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocalizedStrings
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class Screen {
    data object Home : Screen()
    data class JobDetail(val jobId: Long) : Screen()
    data object PostJob : Screen()
    data object Applications : Screen()
    data object Messages : Screen()
    data class ChatConversation(
        val jobId: Long,
        val jobTitle: String,
        val companyName: String,
        val recipientPhone: String,
        val recipientName: String
    ) : Screen()
    data object Profile : Screen()
    data object EditProfile : Screen()
    data object ResumePreview : Screen()
    data object Notifications : Screen()
    data object Analytics : Screen()
}

enum class AppRole {
    JOB_SEEKER,
    EMPLOYER
}

data class UiState(
    // Auth
    val isLoggedIn: Boolean = true, // Logged in with default user, can logout to test OTP screen
    val authPhone: String = "9876543210",
    val enteredOtp: String = "",
    val isOtpSent: Boolean = false,
    val otpCountdown: Int = 30,

    // Navigation & View
    val currentScreen: Screen = Screen.Home,
    val screenStack: List<Screen> = listOf(Screen.Home),
    val appRole: AppRole = AppRole.JOB_SEEKER,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val isDarkMode: Boolean = false,

    // Filters & Search
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedCity: String = "All",
    val selectedJobType: String = "All",
    val urgentOnly: Boolean = false,

    // Toast / Feedback
    val toastMessage: String? = null
)

class HappyToHelpViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JobRepository
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = JobRepository(database.appDao())
        NotificationHelper.createNotificationChannel(application)

        // Ensure default data is present if fresh
        viewModelScope.launch {
            AppDatabase.populateInitialData(database.appDao())
        }
    }

    val currentStrings: StateFlow<com.example.ui.i18n.AppStrings> =
        combine(_uiState) { state ->
            LocalizedStrings.get(state.first().language)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LocalizedStrings.english
        )

    val allJobs: StateFlow<List<JobEntity>> = repository.allJobs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedJobs: StateFlow<List<JobEntity>> = repository.savedJobs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredJobs: StateFlow<List<JobEntity>> = combine(
        allJobs,
        _uiState
    ) { jobs, state ->
        jobs.filter { job ->
            val matchesQuery = state.searchQuery.isBlank() ||
                job.title.contains(state.searchQuery, ignoreCase = true) ||
                job.companyName.contains(state.searchQuery, ignoreCase = true) ||
                job.locationArea.contains(state.searchQuery, ignoreCase = true) ||
                job.category.contains(state.searchQuery, ignoreCase = true)

            val matchesCategory = state.selectedCategory == "All" ||
                job.category.equals(state.selectedCategory, ignoreCase = true)

            val matchesCity = state.selectedCity == "All" ||
                job.city.equals(state.selectedCity, ignoreCase = true)

            val matchesJobType = state.selectedJobType == "All" ||
                job.jobType.equals(state.selectedJobType, ignoreCase = true)

            val matchesUrgent = !state.urgentOnly || job.isUrgent

            matchesQuery && matchesCategory && matchesCity && matchesJobType && matchesUrgent
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userProfile: StateFlow<UserProfileEntity?> = repository.getUserProfile("9876543210")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val applications: StateFlow<List<JobApplicationEntity>> = repository.allApplications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val unreadNotifCount: StateFlow<Int> = repository.unreadCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val userMessages: StateFlow<List<ChatMessageEntity>> = repository.getAllUserMessages("9876543210")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getConversationMessages(conversationId: String) =
        repository.getMessages(conversationId)

    // === AUTHENTICATION ===
    fun updateAuthPhone(phone: String) {
        _uiState.update { it.copy(authPhone = phone) }
    }

    fun updateEnteredOtp(otp: String) {
        _uiState.update { it.copy(enteredOtp = otp) }
    }

    fun sendOtp() {
        val phone = _uiState.value.authPhone.trim()
        if (phone.length < 10) {
            showToast("Please enter a valid 10-digit mobile number")
            return
        }
        _uiState.update { it.copy(isOtpSent = true, enteredOtp = "") }
        showToast("OTP sent to +91 $phone! Demo OTP is 1234")
    }

    fun quickFillDemoOtp() {
        _uiState.update { it.copy(enteredOtp = "1234") }
    }

    fun verifyOtp() {
        val otp = _uiState.value.enteredOtp.trim()
        if (otp == "1234" || otp.length == 4) {
            _uiState.update {
                it.copy(
                    isLoggedIn = true,
                    isOtpSent = false,
                    enteredOtp = "",
                    currentScreen = Screen.Home,
                    screenStack = listOf(Screen.Home)
                )
            }
            showToast("Logged in successfully! Welcome to Happy to Help.")
        } else {
            showToast("Invalid OTP. Try 1234")
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                isLoggedIn = false,
                isOtpSent = false,
                enteredOtp = "",
                currentScreen = Screen.Home,
                screenStack = listOf(Screen.Home)
            )
        }
        showToast("Signed out. You can sign in anytime with mobile OTP.")
    }

    // === NAVIGATION ===
    fun navigateTo(screen: Screen) {
        _uiState.update { state ->
            val newStack = state.screenStack + screen
            state.copy(currentScreen = screen, screenStack = newStack)
        }
    }

    fun navigateBack(): Boolean {
        val stack = _uiState.value.screenStack
        if (stack.size > 1) {
            val newStack = stack.dropLast(1)
            val previousScreen = newStack.last()
            _uiState.update {
                it.copy(currentScreen = previousScreen, screenStack = newStack)
            }
            return true
        }
        return false
    }

    // === APP CONFIG ===
    fun setLanguage(lang: AppLanguage) {
        _uiState.update { it.copy(language = lang) }
        showToast("Language changed to ${lang.displayName}")
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun switchRole(role: AppRole) {
        _uiState.update { it.copy(appRole = role) }
    }

    // === SEARCH & FILTER ===
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedCategory(cat: String) {
        _uiState.update { it.copy(selectedCategory = cat) }
    }

    fun setSelectedCity(city: String) {
        _uiState.update { it.copy(selectedCity = city) }
    }

    fun setSelectedJobType(jobType: String) {
        _uiState.update { it.copy(selectedJobType = jobType) }
    }

    fun toggleUrgentOnly() {
        _uiState.update { it.copy(urgentOnly = !it.urgentOnly) }
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = "All",
                selectedCity = "All",
                selectedJobType = "All",
                urgentOnly = false
            )
        }
    }

    // === ACTIONS: JOB INTERACTIONS ===
    fun toggleSaveJob(jobId: Long, currentlySaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSaveJob(jobId, !currentlySaved)
            showToast(if (!currentlySaved) "Job saved to bookmarks" else "Job removed from bookmarks")
        }
    }

    fun applyForJob(job: JobEntity) {
        viewModelScope.launch {
            val user = userProfile.value
            val application = JobApplicationEntity(
                jobId = job.id,
                jobTitle = job.title,
                companyName = job.companyName,
                applicantPhone = user?.phone ?: "9876543210",
                applicantName = user?.fullName ?: "Rahul Sharma",
                applicantExperience = user?.experienceYears ?: "1-2 Years",
                applicantCity = user?.city ?: "Delhi NCR",
                status = "Applied",
                employerNotes = "Application submitted via Happy to Help."
            )
            repository.applyForJob(application)

            // Trigger notification
            val notif = NotificationEntity(
                title = "Application Sent to ${job.companyName} 🎉",
                message = "Your digital profile and resume were shared with ${job.employerName}. You can also call or message them directly.",
                type = "APPLICATION_UPDATE",
                relatedJobId = job.id
            )
            repository.addNotification(notif)

            // Real Android push notification
            NotificationHelper.showSystemNotification(
                getApplication(),
                (System.currentTimeMillis() % 10000).toInt(),
                "Application Sent: ${job.title}",
                "${job.companyName} has received your profile. Tap to view status."
            )

            showToast("Application submitted successfully! Employer notified.")
        }
    }

    fun postNewJob(
        title: String,
        company: String,
        category: String,
        location: String,
        city: String,
        salary: String,
        jobType: String,
        vacancies: Int,
        requirements: String,
        employerName: String,
        phone: String,
        isUrgent: Boolean,
        locationPhotoUri: String? = null
    ) {
        viewModelScope.launch {
            val newJob = JobEntity(
                title = title.trim(),
                companyName = company.trim(),
                category = category.trim(),
                locationArea = location.trim(),
                city = if (city.isBlank()) "Delhi NCR" else city.trim(),
                salary = salary.trim(),
                jobType = jobType,
                experienceRequired = "0-2 Years",
                educationRequired = "Open for all",
                description = "$title needed at $company. Local candidates preferred with immediate joining.",
                requirements = requirements.trim(),
                employerPhone = phone.trim(),
                employerWhatsApp = phone.trim(),
                employerName = employerName.trim(),
                postedDate = "Just now",
                isUrgent = isUrgent,
                vacancies = vacancies,
                isSaved = false,
                postedByUserId = phone.trim(),
                locationPhotoUri = locationPhotoUri
            )
            val newId = repository.insertJob(newJob)

            val notif = NotificationEntity(
                title = "New Job Published! 🚀",
                message = "Your job post '$title' is now live for nearby candidates in $location.",
                type = "HIRING",
                relatedJobId = newId
            )
            repository.addNotification(notif)

            showToast("Job posted successfully! Candidates can now apply and call you.")
            navigateBack()
        }
    }

    fun updateApplicationStatus(applicationId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(applicationId, newStatus)
            showToast("Candidate status updated to $newStatus")
        }
    }

    // === MESSAGES ===
    fun sendChatMessage(
        jobId: Long,
        jobTitle: String,
        companyName: String,
        recipientPhone: String,
        text: String
    ) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val sender = userProfile.value?.phone ?: "9876543210"
            val conversationId = "${jobId}_$sender"
            val message = ChatMessageEntity(
                conversationId = conversationId,
                jobId = jobId,
                jobTitle = jobTitle,
                companyName = companyName,
                senderPhone = sender,
                recipientPhone = recipientPhone,
                messageText = text.trim(),
                isFromJobSeeker = _uiState.value.appRole == AppRole.JOB_SEEKER,
                isRead = true
            )
            repository.sendMessage(message)
        }
    }

    // === PROFILE & RESUME ===
    fun updateProfile(
        name: String,
        email: String,
        city: String,
        locality: String,
        role: String,
        experience: String,
        education: String,
        skills: String,
        bio: String
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity(phone = "9876543210")
            val updated = current.copy(
                fullName = name,
                email = email,
                city = city,
                locality = locality,
                preferredCategory = role,
                experienceYears = experience,
                education = education,
                skills = skills,
                bio = bio,
                isProfileComplete = true
            )
            repository.saveUserProfile(updated)
            showToast("Profile details updated successfully!")
            navigateBack()
        }
    }

    fun uploadSimulatedResume(fileName: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity(phone = "9876543210")
            val updated = current.copy(
                resumeFileName = fileName,
                resumeUpdatedDate = "Updated today"
            )
            repository.saveUserProfile(updated)

            val notif = NotificationEntity(
                title = "Resume Uploaded 📄",
                message = "Your document '$fileName' is now attached to your applications.",
                type = "APPLICATION_UPDATE"
            )
            repository.addNotification(notif)

            showToast("Resume '$fileName' attached successfully!")
        }
    }

    // === NOTIFICATIONS ===
    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showToast("All notifications marked as read")
        }
    }

    fun triggerTestNotification() {
        viewModelScope.launch {
            val notif = NotificationEntity(
                title = "🔔 New Local Job in Laxmi Nagar",
                message = "Bikaner Sweets posted an opening for Kitchen Assistant (₹20,000/mo). Tap to view and apply.",
                type = "JOB_ALERT",
                relatedJobId = 5
            )
            repository.addNotification(notif)

            NotificationHelper.showSystemNotification(
                getApplication(),
                (System.currentTimeMillis() % 10000).toInt(),
                "New Job Alert: Kitchen Assistant",
                "₹20,000/mo in Laxmi Nagar. Immediate joining."
            )
            showToast("Live notification sent!")
        }
    }

    // === FEEDBACK / TOAST ===
    private fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
