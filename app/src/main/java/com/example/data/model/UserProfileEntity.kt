package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val phone: String, // E.g. "9876543210"
    val fullName: String = "Rahul Sharma",
    val email: String = "rahul.jobseeker@example.com",
    val city: String = "Delhi NCR",
    val locality: String = "Laxmi Nagar",
    val preferredCategory: String = "Delivery & Logistics",
    val experienceYears: String = "1-2 Years",
    val education: String = "12th Pass",
    val skills: String = "Bike Driving, Smartphone Navigation, Hindi & English, Cash Handling",
    val bio: String = "Hardworking and punctual delivery executive with 2 years of local route experience. Looking for full-time or urgent delivery/retail jobs in Delhi NCR.",
    val resumeFileName: String? = "Rahul_Sharma_Resume.pdf",
    val resumeUpdatedDate: String? = "28 Sep 2026",
    val isEmployer: Boolean = false,
    val isProfileComplete: Boolean = true
)
