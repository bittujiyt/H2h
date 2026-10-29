package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val companyName: String,
    val category: String, // Delivery, Retail, Driver, Helper, Cook, Office, Technician, Security
    val locationArea: String,
    val city: String,
    val salary: String,
    val jobType: String, // Full-time, Part-time, Daily Wage, Urgent
    val experienceRequired: String,
    val educationRequired: String,
    val description: String,
    val requirements: String,
    val employerPhone: String,
    val employerWhatsApp: String,
    val employerName: String,
    val postedDate: String,
    val isUrgent: Boolean = false,
    val vacancies: Int = 1,
    val isSaved: Boolean = false,
    val postedByUserId: String? = null, // Mobile number of employer who posted
    val locationPhotoUri: String? = null // URL or URI of workplace/shop photo
)
