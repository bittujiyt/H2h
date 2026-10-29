package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_applications")
data class JobApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jobId: Long,
    val jobTitle: String,
    val companyName: String,
    val applicantPhone: String,
    val applicantName: String,
    val applicantExperience: String,
    val applicantCity: String,
    val appliedAt: Long = System.currentTimeMillis(),
    val status: String = "Applied", // Applied, Under Review, Shortlisted, Hired, Rejected
    val employerNotes: String = ""
)
