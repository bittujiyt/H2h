package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.AmberUrgent
import com.example.ui.theme.EmeraldGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    strings: AppStrings,
    onBack: () -> Unit,
    onSubmitJob: (
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
        locationPhotoUri: String?
    ) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Delivery") }
    var location by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Delhi NCR") }
    var salary by remember { mutableStateOf("₹18,000 - ₹22,000 / month") }
    var jobType by remember { mutableStateOf("Full Time") }
    var vacanciesText by remember { mutableStateOf("2") }
    var requirements by remember { mutableStateOf("Valid Aadhaar card, punctual, polite behavior") }
    var employerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isUrgent by remember { mutableStateOf(false) }
    var locationPhotoUri by remember { mutableStateOf<String?>("https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=800&q=80") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            locationPhotoUri = uri.toString()
        }
    }

    val photoPresets = listOf(
        "🏪 Retail Shop" to "https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=800&q=80",
        "📦 Delivery Hub" to "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=800&q=80",
        "🍳 Kitchen" to "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80",
        "💻 Office" to "https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=800&q=80",
        "🛠️ Workshop" to "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?auto=format&fit=crop&w=800&q=80",
        "🚗 Driver / Car" to "https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&w=800&q=80"
    )

    val categories = listOf("Delivery", "Retail", "Driver", "Helper", "Cook", "Office", "Technician", "Security")
    val jobTypes = listOf("Full Time", "Part Time", "Daily Wage", "Urgent")

    var categoryExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.postNewJob, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("post_job_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Post your vacancy directly to local workers. Candidates in your area will receive instant notifications and can see your workplace photo, call or message you.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(14.dp)
                )
            }

            // === WORKPLACE & LOCATION PHOTO UPLOAD SECTION ===
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("workplace_photo_section"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Workplace / Shop Photo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (locationPhotoUri != null) {
                            Surface(
                                color = EmeraldGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Photo Added", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Text(
                        text = "Apni dukaan, office ya workplace ki photo upload karein taaki workers use dekhkar asani se pehchan sakein.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Photo Preview or Upload Target Box
                    if (!locationPhotoUri.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(locationPhotoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Workplace photo preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Overlay Button to remove/change
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clickable { locationPhotoUri = null }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remove", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Upload Action Button (Zero-permission Android Photo Picker)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pick_photo_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gallery / Camera se Photo Chunein", fontSize = 12.sp)
                        }
                    }

                    // Workplace Photo Presets
                    Text(
                        text = "Or choose sample workplace photo:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        photoPresets.forEach { (name, url) ->
                            AssistChip(
                                onClick = { locationPhotoUri = url },
                                label = { Text(name, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // Job Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(strings.jobTitleLabel) },
                placeholder = { Text("e.g. Delivery Partner, Retail Shop Helper") },
                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("input_job_title"),
                shape = RoundedCornerShape(10.dp)
            )

            // Business Name
            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text(strings.companyNameLabel) },
                placeholder = { Text("e.g. Sharma Kirana Store, Metro Logistics") },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("input_company"),
                shape = RoundedCornerShape(10.dp)
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(strings.categoryLabel) },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("input_category"),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                category = item
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Location Area & Landmark
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(strings.locationLabel) },
                placeholder = { Text("e.g. Near Metro Gate 2, Laxmi Nagar") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("input_location"),
                shape = RoundedCornerShape(10.dp)
            )

            // City
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City / Region") },
                placeholder = { Text("e.g. Delhi NCR") },
                modifier = Modifier.fillMaxWidth().testTag("input_city"),
                shape = RoundedCornerShape(10.dp)
            )

            // Salary
            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = { Text(strings.salaryLabel) },
                placeholder = { Text("e.g. ₹18,000 / month or ₹650 / day") },
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("input_salary"),
                shape = RoundedCornerShape(10.dp)
            )

            // Job Type Dropdown & Openings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = jobType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        jobTypes.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    jobType = item
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = vacanciesText,
                    onValueChange = { vacanciesText = it.filter { c -> c.isDigit() } },
                    label = { Text(strings.vacanciesLabel) },
                    placeholder = { Text("Openings") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.9f).testTag("input_vacancies"),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            // Requirements
            OutlinedTextField(
                value = requirements,
                onValueChange = { requirements = it },
                label = { Text(strings.requirementsLabel) },
                placeholder = { Text("e.g. 10th pass, timing 9 AM to 7 PM, bike required") },
                modifier = Modifier.fillMaxWidth().testTag("input_requirements"),
                minLines = 2,
                shape = RoundedCornerShape(10.dp)
            )

            // Employer Contact
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = employerName,
                    onValueChange = { employerName = it },
                    label = { Text(strings.contactPersonLabel) },
                    placeholder = { Text("e.g. Rajesh Kumar") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.weight(1f).testTag("input_employer_name"),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it },
                    label = { Text(strings.phoneLabel) },
                    placeholder = { Text("10-digit Mobile") },
                    leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("input_phone"),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            // Urgent Hiring Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isUrgent,
                    onCheckedChange = { isUrgent = it },
                    modifier = Modifier.testTag("urgent_checkbox")
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = AmberUrgent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = strings.urgentHiring,
                            fontWeight = FontWeight.Bold,
                            color = AmberUrgent
                        )
                    }
                    Text(
                        text = "Highlight with urgent badge to get workers within 24 hours",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Submit Button
            Button(
                onClick = {
                    val finalVacancies = vacanciesText.toIntOrNull() ?: 1
                    onSubmitJob(
                        title.ifBlank { "Delivery Partner" },
                        company.ifBlank { "Local Business Hub" },
                        category,
                        location.ifBlank { "Laxmi Nagar, Delhi NCR" },
                        city.ifBlank { "Delhi NCR" },
                        salary.ifBlank { "₹18,000 / month" },
                        jobType,
                        finalVacancies,
                        requirements.ifBlank { "Hardworking & punctual" },
                        employerName.ifBlank { "Manager" },
                        phone.ifBlank { "9811223344" },
                        isUrgent,
                        locationPhotoUri
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_job_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = strings.submitJobPost,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
