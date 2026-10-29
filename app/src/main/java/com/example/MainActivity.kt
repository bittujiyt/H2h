package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.JobEntity
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AppHeader
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.ApplicationsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatConversationScreen
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JobDetailScreen
import com.example.ui.screens.MessagesInboxScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PostJobScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ResumePreviewScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HappyToHelpViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: HappyToHelpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val strings by viewModel.currentStrings.collectAsStateWithLifecycle()
            val allJobs by viewModel.allJobs.collectAsStateWithLifecycle()
            val filteredJobs by viewModel.filteredJobs.collectAsStateWithLifecycle()
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val applications by viewModel.applications.collectAsStateWithLifecycle()
            val notifications by viewModel.notifications.collectAsStateWithLifecycle()
            val unreadNotifCount by viewModel.unreadNotifCount.collectAsStateWithLifecycle()
            val userMessages by viewModel.userMessages.collectAsStateWithLifecycle()

            val snackbarHostState = remember { SnackbarHostState() }

            // Handle Toast / Feedback snackbar
            LaunchedEffect(uiState.toastMessage) {
                uiState.toastMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.dismissToast()
                }
            }

            // Handle System Back Button
            BackHandler(enabled = uiState.screenStack.size > 1) {
                viewModel.navigateBack()
            }

            MyApplicationTheme(darkTheme = uiState.isDarkMode) {
                if (!uiState.isLoggedIn) {
                    AuthScreen(
                        state = uiState,
                        strings = strings,
                        onPhoneChange = { viewModel.updateAuthPhone(it) },
                        onOtpChange = { viewModel.updateEnteredOtp(it) },
                        onSendOtp = { viewModel.sendOtp() },
                        onQuickFillOtp = { viewModel.quickFillDemoOtp() },
                        onVerifyOtp = { viewModel.verifyOtp() }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            if (uiState.currentScreen is Screen.Home ||
                                uiState.currentScreen is Screen.Applications ||
                                uiState.currentScreen is Screen.Messages ||
                                uiState.currentScreen is Screen.Analytics ||
                                uiState.currentScreen is Screen.Profile
                            ) {
                                AppHeader(
                                    strings = strings,
                                    currentRole = uiState.appRole,
                                    onRoleToggle = { viewModel.switchRole(it) },
                                    currentLang = uiState.language,
                                    onLangSelect = { viewModel.setLanguage(it) },
                                    unreadNotifCount = unreadNotifCount,
                                    onNotifClick = { viewModel.navigateTo(Screen.Notifications) },
                                    isDarkMode = uiState.isDarkMode,
                                    onToggleDark = { viewModel.toggleDarkMode() }
                                )
                            }
                        },
                        bottomBar = {
                            if (uiState.currentScreen is Screen.Home ||
                                uiState.currentScreen is Screen.Applications ||
                                uiState.currentScreen is Screen.Messages ||
                                uiState.currentScreen is Screen.Analytics ||
                                uiState.currentScreen is Screen.Profile
                            ) {
                                AppBottomNavigation(
                                    currentScreen = uiState.currentScreen,
                                    onNavigate = { viewModel.navigateTo(it) },
                                    strings = strings,
                                    appRole = uiState.appRole
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (val screen = uiState.currentScreen) {
                                is Screen.Home -> {
                                    HomeScreen(
                                        state = uiState,
                                        strings = strings,
                                        jobs = filteredJobs,
                                        onSearchChange = { viewModel.setSearchQuery(it) },
                                        onCategorySelect = { viewModel.setSelectedCategory(it) },
                                        onCitySelect = { viewModel.setSelectedCity(it) },
                                        onJobTypeSelect = { viewModel.setSelectedJobType(it) },
                                        onToggleUrgent = { viewModel.toggleUrgentOnly() },
                                        onClearFilters = { viewModel.clearFilters() },
                                        onJobClick = { jobId ->
                                            viewModel.navigateTo(Screen.JobDetail(jobId))
                                        },
                                        onApplyJob = { job ->
                                            viewModel.applyForJob(job)
                                        },
                                        onToggleSave = { job ->
                                            viewModel.toggleSaveJob(job.id, job.isSaved)
                                        },
                                        onChatEmployer = { job ->
                                            viewModel.navigateTo(
                                                Screen.ChatConversation(
                                                    jobId = job.id,
                                                    jobTitle = job.title,
                                                    companyName = job.companyName,
                                                    recipientPhone = job.employerPhone,
                                                    recipientName = job.employerName
                                                )
                                            )
                                        },
                                        onNavigatePostJob = {
                                            viewModel.navigateTo(Screen.PostJob)
                                        }
                                    )
                                }

                                is Screen.JobDetail -> {
                                    val selectedJob = allJobs.find { it.id == screen.jobId }
                                    JobDetailScreen(
                                        job = selectedJob,
                                        strings = strings,
                                        onBack = { viewModel.navigateBack() },
                                        onApply = { job -> viewModel.applyForJob(job) },
                                        onToggleSave = { job -> viewModel.toggleSaveJob(job.id, job.isSaved) },
                                        onChat = { job ->
                                            viewModel.navigateTo(
                                                Screen.ChatConversation(
                                                    jobId = job.id,
                                                    jobTitle = job.title,
                                                    companyName = job.companyName,
                                                    recipientPhone = job.employerPhone,
                                                    recipientName = job.employerName
                                                )
                                            )
                                        }
                                    )
                                }

                                is Screen.PostJob -> {
                                    PostJobScreen(
                                        strings = strings,
                                        onBack = { viewModel.navigateBack() },
                                        onSubmitJob = { title, comp, cat, loc, city, sal, type, vac, req, empName, phone, urgent, photoUri ->
                                            viewModel.postNewJob(
                                                title, comp, cat, loc, city, sal, type, vac, req, empName, phone, urgent, photoUri
                                            )
                                        }
                                    )
                                }

                                is Screen.Applications -> {
                                    ApplicationsScreen(
                                        applications = applications,
                                        appRole = uiState.appRole,
                                        strings = strings,
                                        onStatusChange = { appId, newStatus ->
                                            viewModel.updateApplicationStatus(appId, newStatus)
                                        },
                                        onOpenChat = { jId, jTitle, cName, rPhone, rName ->
                                            viewModel.navigateTo(
                                                Screen.ChatConversation(
                                                    jobId = jId,
                                                    jobTitle = jTitle,
                                                    companyName = cName,
                                                    recipientPhone = rPhone,
                                                    recipientName = rName
                                                )
                                            )
                                        }
                                    )
                                }

                                is Screen.Messages -> {
                                    MessagesInboxScreen(
                                        messages = userMessages,
                                        strings = strings,
                                        onOpenConversation = { jId, jTitle, cName, rPhone, rName ->
                                            viewModel.navigateTo(
                                                Screen.ChatConversation(
                                                    jobId = jId,
                                                    jobTitle = jTitle,
                                                    companyName = cName,
                                                    recipientPhone = rPhone,
                                                    recipientName = rName
                                                )
                                            )
                                        }
                                    )
                                }

                                is Screen.ChatConversation -> {
                                    val conversationId = "${screen.jobId}_9876543210"
                                    val convMessages by viewModel.getConversationMessages(conversationId)
                                        .collectAsState(initial = emptyList())

                                    ChatConversationScreen(
                                        jobId = screen.jobId,
                                        jobTitle = screen.jobTitle,
                                        companyName = screen.companyName,
                                        recipientPhone = screen.recipientPhone,
                                        recipientName = screen.recipientName,
                                        messages = convMessages,
                                        strings = strings,
                                        onBack = { viewModel.navigateBack() },
                                        onSendMessage = { text ->
                                            viewModel.sendChatMessage(
                                                screen.jobId,
                                                screen.jobTitle,
                                                screen.companyName,
                                                screen.recipientPhone,
                                                text
                                            )
                                        }
                                    )
                                }

                                is Screen.Profile -> {
                                    ProfileScreen(
                                        profile = userProfile,
                                        strings = strings,
                                        currentLang = uiState.language,
                                        onLangSelect = { viewModel.setLanguage(it) },
                                        isDarkMode = uiState.isDarkMode,
                                        onToggleDark = { viewModel.toggleDarkMode() },
                                        onEditProfile = { viewModel.navigateTo(Screen.EditProfile) },
                                        onViewResume = { viewModel.navigateTo(Screen.ResumePreview) },
                                        onUploadResume = { docName -> viewModel.uploadSimulatedResume(docName) },
                                        onLogout = { viewModel.logout() }
                                    )
                                }

                                is Screen.EditProfile -> {
                                    EditProfileScreen(
                                        profile = userProfile,
                                        strings = strings,
                                        onBack = { viewModel.navigateBack() },
                                        onSave = { name, email, city, loc, role, exp, edu, skills, bio ->
                                            viewModel.updateProfile(name, email, city, loc, role, exp, edu, skills, bio)
                                        }
                                    )
                                }

                                is Screen.ResumePreview -> {
                                    ResumePreviewScreen(
                                        profile = userProfile,
                                        strings = strings,
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }

                                is Screen.Notifications -> {
                                    NotificationsScreen(
                                        notifications = notifications,
                                        strings = strings,
                                        onBack = { viewModel.navigateBack() },
                                        onMarkAllRead = { viewModel.markAllNotificationsRead() },
                                        onTriggerTestAlert = { viewModel.triggerTestNotification() },
                                        onJobClick = { jId ->
                                            viewModel.navigateTo(Screen.JobDetail(jId))
                                        }
                                    )
                                }

                                is Screen.Analytics -> {
                                    AnalyticsScreen(
                                        appRole = uiState.appRole,
                                        strings = strings,
                                        applicationsCount = applications.size,
                                        jobsCount = allJobs.size
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
