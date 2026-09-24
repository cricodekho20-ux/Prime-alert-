package com.example.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.NewsCategories
import com.example.model.NewsNotification
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.PrimeRed
import com.example.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddEditNewsScreen(
    adminViewModel: AdminViewModel,
    onBackClick: () -> Unit,
    onPublishSuccess: (NewsNotification?) -> Unit,
    modifier: Modifier = Modifier
) {
    val form by adminViewModel.formState.collectAsState()
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val presetImages = listOf(
        Pair("संसद/देश", "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=800&auto=format&fit=crop&q=80"),
        Pair("बिहार हाईवे", "https://images.unsplash.com/photo-1584467735815-f778f274e296?w=800&auto=format&fit=crop&q=80"),
        Pair("क्रिकेट मैच", "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=800&auto=format&fit=crop&q=80"),
        Pair("टेक्नोलॉजी", "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop&q=80"),
        Pair("शेयर बाजार", "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=80"),
        Pair("शिक्षा/छात्र", "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=800&auto=format&fit=crop&q=80"),
        Pair("सिनेमा/कला", "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=800&auto=format&fit=crop&q=80")
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_add_edit_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (form.isEditMode) "खबर संपादित करें (Edit News)" else "नई खबर लिखें (Add News)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("add_news_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Error Message
            if (form.errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = form.errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Headline Field
            OutlinedTextField(
                value = form.headline,
                onValueChange = { adminViewModel.updateForm { f -> f.copy(headline = it, errorMessage = null) } },
                label = { Text("खबर का मुख्य शीर्षक (Headline) *") },
                placeholder = { Text("उदा. भारत ने रचा नया इतिहास...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("news_headline_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimeRed,
                    focusedLabelColor = PrimeRed
                )
            )

            // Short Description
            OutlinedTextField(
                value = form.shortDescription,
                onValueChange = { adminViewModel.updateForm { f -> f.copy(shortDescription = it) } },
                label = { Text("संक्षिप्त विवरण (Short Summary)") },
                placeholder = { Text("1-2 वाक्यों में मुख्य सार...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("news_summary_input"),
                shape = RoundedCornerShape(10.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimeRed,
                    focusedLabelColor = PrimeRed
                )
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = form.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("श्रेणी (Category) *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("news_category_dropdown"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimeRed,
                        focusedLabelColor = PrimeRed
                    )
                )

                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    NewsCategories.getCategoryNames().forEach { catName ->
                        DropdownMenuItem(
                            text = { Text(catName) },
                            onClick = {
                                adminViewModel.updateForm { f -> f.copy(category = catName) }
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Image Preview and Preset Picker
            Column {
                Text(
                    text = "फोटो (Image):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Image Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    AsyncImage(
                        model = form.imageUrl,
                        contentDescription = "Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick photo presets row for fast mobile publishing
                Text(
                    text = "त्वरित फोटो सुझाव (1-Tap Presets):",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetImages.forEach { (label, url) ->
                        val isSelected = form.imageUrl == url
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimeRed else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { adminViewModel.updateForm { f -> f.copy(imageUrl = url) } }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = form.imageUrl,
                    onValueChange = { adminViewModel.updateForm { f -> f.copy(imageUrl = it) } },
                    label = { Text("कस्टम इमेज URL (Image URL)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("news_image_url_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimeRed,
                        focusedLabelColor = PrimeRed
                    )
                )
            }

            // Full Content
            OutlinedTextField(
                value = form.content,
                onValueChange = { adminViewModel.updateForm { f -> f.copy(content = it, errorMessage = null) } },
                label = { Text("पूरी खबर (Full News Content) *") },
                placeholder = { Text("यहाँ विस्तार से खबर का ब्यौरा लिखें...") },
                minLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("news_content_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimeRed,
                    focusedLabelColor = PrimeRed
                )
            )

            // Tags
            OutlinedTextField(
                value = form.tags,
                onValueChange = { adminViewModel.updateForm { f -> f.copy(tags = it) } },
                label = { Text("टैग्स (Tags - अल्पविराम से अलग करें)") },
                placeholder = { Text("उदा. भारत, संसद, विकास, क्रिकेट") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("news_tags_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimeRed,
                    focusedLabelColor = PrimeRed
                )
            )

            // Author
            OutlinedTextField(
                value = form.author,
                onValueChange = { adminViewModel.updateForm { f -> f.copy(author = it) } },
                label = { Text("संवाददाता / लेखक (Author)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimeRed,
                    focusedLabelColor = PrimeRed
                )
            )

            // Options: Breaking News & Send Notification Checkboxes
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Mark as Breaking News
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                adminViewModel.updateForm { f -> f.copy(isBreaking = !f.isBreaking) }
                            }
                    ) {
                        Checkbox(
                            checked = form.isBreaking,
                            onCheckedChange = { isChecked ->
                                adminViewModel.updateForm { f -> f.copy(isBreaking = isChecked) }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = PrimeRed),
                            modifier = Modifier.testTag("news_breaking_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "🔴 ब्रेकिंग न्यूज के रूप में चिह्नित करें (Mark as Breaking News)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "होम स्क्रीन के ऊपरी लाल टिकर में दिखाई देगी",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Send Push Notification
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                adminViewModel.updateForm { f -> f.copy(sendNotification = !f.sendNotification) }
                            }
                    ) {
                        Checkbox(
                            checked = form.sendNotification,
                            onCheckedChange = { isChecked ->
                                adminViewModel.updateForm { f -> f.copy(sendNotification = isChecked) }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = PrimeRed),
                            modifier = Modifier.testTag("news_send_notification_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "📢 पुश नोटिफिकेशन भेजें (Send Push Notification)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "प्रकाशन के तुरंत बाद सभी पाठकों को अलर्ट जाएगा",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Status (Published / Draft)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "स्थिति (Status):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Published", "Draft").forEach { statusOption ->
                                val isSelected = form.status == statusOption
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) PrimeRed else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { adminViewModel.updateForm { f -> f.copy(status = statusOption) } }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (statusOption == "Published") "प्रकाशित (Published)" else "ड्राफ्ट (Draft)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Submit Button: PUBLISH NEWS
            Button(
                onClick = {
                    adminViewModel.publishOrUpdateNews { createdNotif ->
                        onPublishSuccess(createdNotif)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("publish_news_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimeRed)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (form.isEditMode) "खबर अपडेट करें (UPDATE NEWS)" else "खबर प्रकाशित करें (PUBLISH NEWS)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
