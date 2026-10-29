package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AppDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.JobApplicationEntity
import com.example.data.model.JobEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        JobEntity::class,
        UserProfileEntity::class,
        JobApplicationEntity::class,
        ChatMessageEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "happy_to_help_database"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.appDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: AppDao) {
            val sampleJobs = listOf(
                JobEntity(
                    id = 1,
                    title = "Urgent Delivery Executive",
                    companyName = "SpeedFast Hyperlocal Delivery",
                    category = "Delivery",
                    locationArea = "Laxmi Nagar & Preet Vihar",
                    city = "Delhi NCR",
                    salary = "₹18,000 - ₹26,000 / Month + Fuel Allowance",
                    jobType = "Urgent",
                    experienceRequired = "Fresher / 0-1 yr",
                    educationRequired = "10th Pass",
                    description = "Deliver groceries, packages, and food in the local area. Bike and valid driving license required. Immediate joining with joining bonus of ₹2,000.",
                    requirements = "Own 2-wheeler, Smartphone, Aadhaar card, Pan card, DL",
                    employerPhone = "9811223344",
                    employerWhatsApp = "9811223344",
                    employerName = "Rajesh Gupta (Area Hub Manager)",
                    postedDate = "Today",
                    isUrgent = true,
                    vacancies = 8,
                    isSaved = false,
                    locationPhotoUri = "https://images.unsplash.com/photo-1616401784845-180882ba9ba8?auto=format&fit=crop&w=800&q=80"
                ),
                JobEntity(
                    id = 2,
                    title = "Retail Counter Sales Assistant",
                    companyName = "Sharma Cloth & Garment Store",
                    category = "Retail",
                    locationArea = "Chandni Chowk, Main Bazaar",
                    city = "Delhi NCR",
                    salary = "₹16,000 - ₹19,000 / Month",
                    jobType = "Full Time",
                    experienceRequired = "Fresher to 2 yrs",
                    educationRequired = "10th or 12th Pass",
                    description = "Assist walk-in customers, showcase clothing stock, manage billing counters, and organize shelf displays. Good polite communication in Hindi required.",
                    requirements = "Polite speaking, Basic Hindi & English reading, punctual",
                    employerPhone = "9871100223",
                    employerWhatsApp = "9871100223",
                    employerName = "Sunil Sharma (Shop Owner)",
                    postedDate = "Yesterday",
                    isUrgent = false,
                    vacancies = 3,
                    isSaved = true,
                    locationPhotoUri = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=800&q=80"
                ),
                JobEntity(
                    id = 3,
                    title = "Personal & Family Car Driver",
                    companyName = "Goel Family Residence",
                    category = "Driver",
                    locationArea = "South Extension Part 2",
                    city = "Delhi NCR",
                    salary = "₹22,000 - ₹25,000 / Month",
                    jobType = "Full Time",
                    experienceRequired = "2+ Years driving experience",
                    educationRequired = "Basic Schooling",
                    description = "Family car driver needed for Honda City & Creta (automatic & manual). Route knowledge of Delhi, Noida, and Airport essential. Clean record.",
                    requirements = "Valid LMV license, Non-smoker, police verification ready",
                    employerPhone = "9899334455",
                    employerWhatsApp = "9899334455",
                    employerName = "Anurag Goel (Employer)",
                    postedDate = "2 days ago",
                    isUrgent = true,
                    vacancies = 1,
                    isSaved = false,
                    locationPhotoUri = "https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&w=800&q=80"
                ),
                JobEntity(
                    id = 4,
                    title = "Warehouse Packing & Loading Helper",
                    companyName = "Metro Logistics Hub",
                    category = "Helper",
                    locationArea = "Okhla Industrial Area Phase 3",
                    city = "Delhi NCR",
                    salary = "₹650 / Day (₹17,000 - ₹19,500/mo)",
                    jobType = "Daily Wage",
                    experienceRequired = "No experience needed",
                    educationRequired = "Any",
                    description = "Packing boxes, pasting barcodes, scanning cartons, and loading delivery vans. Daily and weekly payment payout options available. Tea & snacks provided.",
                    requirements = "Physically fit, hardworking, punctual",
                    employerPhone = "9822334455",
                    employerWhatsApp = "9822334455",
                    employerName = "Karamjeet Singh (Supervisor)",
                    postedDate = "Today",
                    isUrgent = true,
                    vacancies = 15,
                    isSaved = false,
                    locationPhotoUri = "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=800&q=80"
                ),
                JobEntity(
                    id = 5,
                    title = "Cook / Kitchen Assistant",
                    companyName = "Bikaner Sweets & Restaurant",
                    category = "Cook",
                    locationArea = "Karol Bagh Market",
                    city = "Delhi NCR",
                    salary = "₹20,000 - ₹24,000 / Month + Food & Stay",
                    jobType = "Full Time",
                    experienceRequired = "1+ year kitchen experience",
                    educationRequired = "Any",
                    description = "North Indian cuisine, curry preparation, tandoor, and assisting head chef in kitchen hygiene. Free accommodation and meals included.",
                    requirements = "Hygiene focused, kitchen experience, team player",
                    employerPhone = "9818800998",
                    employerWhatsApp = "9818800998",
                    employerName = "Manoj Aggarwal (Manager)",
                    postedDate = "3 days ago",
                    isUrgent = false,
                    vacancies = 2,
                    isSaved = false,
                    locationPhotoUri = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80"
                ),
                JobEntity(
                    id = 6,
                    title = "Computer Operator & Billing Clerk",
                    companyName = "Jain Hardware & Electricals",
                    category = "Office",
                    locationArea = "Connaught Place, Barakhamba Road",
                    city = "Delhi NCR",
                    salary = "₹18,000 - ₹21,000 / Month",
                    jobType = "Full Time",
                    experienceRequired = "0-2 Years",
                    educationRequired = "12th Pass or Graduate",
                    description = "Create GST invoices in Tally / Excel, manage customer calls, enter stock items, and assist accountant. Training provided on billing software.",
                    requirements = "Basic MS Excel, computer typing 25+ WPM, neat handwriting",
                    employerPhone = "9810112233",
                    employerWhatsApp = "9810112233",
                    employerName = "Vikas Jain (Proprietor)",
                    postedDate = "1 day ago",
                    isUrgent = false,
                    vacancies = 2,
                    isSaved = false,
                    locationPhotoUri = "https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=800&q=80"
                ),
                JobEntity(
                    id = 7,
                    title = "AC & Home Appliance Technician",
                    companyName = "CoolTech Home Services",
                    category = "Technician",
                    locationArea = "Noida Sector 18 & Indirapuram",
                    city = "Delhi NCR",
                    salary = "₹25,000 - ₹35,000 / Month (Commission + Fixed)",
                    jobType = "Full Time",
                    experienceRequired = "1-3 Years",
                    educationRequired = "ITI or Practical training",
                    description = "Visit residential customers for Split AC servicing, gas charging, washing machine, and refrigerator repair. Tools and uniform provided.",
                    requirements = "ITI / Diploma or hands-on experience, bike, smartphone",
                    employerPhone = "9870022334",
                    employerWhatsApp = "9870022334",
                    employerName = "Deepak Verma (Technical Head)",
                    postedDate = "Today",
                    isUrgent = true,
                    vacancies = 4,
                    isSaved = false,
                    locationPhotoUri = "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?auto=format&fit=crop&w=800&q=80"
                ),
                JobEntity(
                    id = 8,
                    title = "Security Guard (Day/Night Shift)",
                    companyName = "Apex Facility & Guarding Solutions",
                    category = "Security",
                    locationArea = "Cyber City & Golf Course Road",
                    city = "Delhi NCR",
                    salary = "₹17,500 - ₹19,000 / Month",
                    jobType = "Full Time",
                    experienceRequired = "Fresher to 1 yr",
                    educationRequired = "8th or 10th Pass",
                    description = "Gate entry register maintenance, visitor tracking, patrolling residential society and office towers. Uniform and shoes provided free.",
                    requirements = "Height 5'6\"+, alert mind, Aadhaar card, clean background",
                    employerPhone = "9811445566",
                    employerWhatsApp = "9811445566",
                    employerName = "Col. R.S. Rathore (HR Admin)",
                    postedDate = "2 days ago",
                    isUrgent = false,
                    vacancies = 10,
                    isSaved = false,
                    locationPhotoUri = "https://images.unsplash.com/photo-1577495508048-b635879837f1?auto=format&fit=crop&w=800&q=80"
                )
            )
            dao.insertJobs(sampleJobs)

            // Seed user profile
            val defaultProfile = UserProfileEntity(
                phone = "9876543210",
                fullName = "Rahul Sharma",
                email = "rahul.sharma@example.com",
                city = "Delhi NCR",
                locality = "Laxmi Nagar",
                preferredCategory = "Delivery",
                experienceYears = "2 Years",
                education = "12th Pass",
                skills = "Bike Driving, GPS Maps Navigation, Customer Service, Cash Handling",
                bio = "Energetic and reliable local delivery partner with 2 years of delivery experience in Delhi NCR. Always punctual and courteous.",
                resumeFileName = "Rahul_Sharma_Resume.pdf",
                resumeUpdatedDate = "Today",
                isEmployer = false,
                isProfileComplete = true
            )
            dao.insertOrUpdateProfile(defaultProfile)

            // Seed initial notifications
            val sampleNotifications = listOf(
                NotificationEntity(
                    id = 1,
                    title = "Welcome to Happy to Help! 🤝",
                    message = "Discover verified local jobs near you. Direct contact with employers, no middlemen or agent fees.",
                    timestamp = System.currentTimeMillis() - 3600000 * 5,
                    type = "SYSTEM",
                    isRead = false
                ),
                NotificationEntity(
                    id = 2,
                    title = "🔥 8 Urgent Vacancies in Laxmi Nagar",
                    message = "SpeedFast Hyperlocal Delivery is hiring 8 Delivery Executives with immediate joining & bonus.",
                    timestamp = System.currentTimeMillis() - 3600000 * 2,
                    type = "JOB_ALERT",
                    isRead = false,
                    relatedJobId = 1
                ),
                NotificationEntity(
                    id = 3,
                    title = "Resume Ready to Share 📄",
                    message = "Your digital resume profile is 100% complete and ready to send directly to employers.",
                    timestamp = System.currentTimeMillis() - 3600000 * 24,
                    type = "APPLICATION_UPDATE",
                    isRead = true
                )
            )
            for (notif in sampleNotifications) {
                dao.insertNotification(notif)
            }

            // Seed initial chat
            dao.insertMessage(
                ChatMessageEntity(
                    id = 1,
                    conversationId = "1_9876543210",
                    jobId = 1,
                    jobTitle = "Urgent Delivery Executive",
                    companyName = "SpeedFast Hyperlocal Delivery",
                    senderPhone = "9811223344",
                    recipientPhone = "9876543210",
                    messageText = "Namaste Rahul! We saw your profile. Are you available to join from tomorrow at our Laxmi Nagar hub?",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isFromJobSeeker = false,
                    isRead = false
                )
            )

            // Seed initial application
            dao.insertApplication(
                JobApplicationEntity(
                    id = 1,
                    jobId = 1,
                    jobTitle = "Urgent Delivery Executive",
                    companyName = "SpeedFast Hyperlocal Delivery",
                    applicantPhone = "9876543210",
                    applicantName = "Rahul Sharma",
                    applicantExperience = "2 Years",
                    applicantCity = "Delhi NCR",
                    appliedAt = System.currentTimeMillis() - 3600000 * 4,
                    status = "Shortlisted",
                    employerNotes = "Selected for tomorrow's briefing."
                )
            )
        }
    }
}
