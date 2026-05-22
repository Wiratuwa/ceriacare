package com.example.klinikanakbarulagi.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.klinikanakbarulagi.model.*
import com.example.klinikanakbarulagi.ui.theme.*
import com.example.klinikanakbarulagi.viewmodel.ClinicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboard(
    viewModel: ClinicViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("queue") }
    val activeDoctor = viewModel.doctors.find { it.id == viewModel.currentDoctorId } ?: viewModel.doctors.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PORTAL DOKTER ANAK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = activeDoctor.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = "Dokter",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandOrange),
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            ) {
                NavigationBarItem(
                    selected = activeTab == "queue",
                    onClick = { activeTab = "queue" },
                    icon = { Icon(Icons.Default.List, contentDescription = "Antrian") },
                    label = { Text("Antrian", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandOrange,
                        selectedTextColor = BrandOrange,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftOrangeBg
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "completed",
                    onClick = { activeTab = "completed" },
                    icon = { Icon(Icons.Default.History, contentDescription = "Selesai") },
                    label = { Text("Selesai", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandOrange,
                        selectedTextColor = BrandOrange,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftOrangeBg
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "profile",
                    onClick = { activeTab = "profile" },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandOrange,
                        selectedTextColor = BrandOrange,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftOrangeBg
                    )
                )
            }
        },
        containerColor = Slate50,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "queue" -> DoctorQueueTab(viewModel, activeDoctor.name)
                "completed" -> DoctorCompletedTab(viewModel, activeDoctor.name)
                "profile" -> DoctorProfileTab(viewModel, activeDoctor)
            }
        }
    }
}

@Composable
fun DoctorQueueTab(viewModel: ClinicViewModel, doctorName: String) {
    // Filter active queues for this doctor
    val activeQueues = viewModel.queue.filter {
        it.doctorName == doctorName && it.status != QueueStatus.Completed
    }

    var examiningItem by remember { mutableStateOf<QueueItem?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Antrian Pasien Hari Ini", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
        }

        if (activeQueues.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada antrian aktif untuk Anda", color = Slate400, fontSize = 12.sp)
                }
            }
        } else {
            items(activeQueues) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Slate100),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        if (item.status == QueueStatus.Examining) SoftOrangeBg else Slate100,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.no,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = if (item.status == QueueStatus.Examining) BrandOrange else Slate800
                                )
                            }
                            Column {
                                Text(
                                    text = item.childName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Slate900
                                )
                                Text(
                                    text = "Jam Booking: ${item.time}",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.status == QueueStatus.Waiting) {
                                Button(
                                    onClick = { viewModel.updateQueueStatus(item.id, QueueStatus.Examining) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Panggil", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { examiningItem = item },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Periksa", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (examiningItem != null) {
        ExaminePatientDialog(
            queueItem = examiningItem!!,
            services = viewModel.services,
            onDismiss = { examiningItem = null },
            onSubmit = { record, service ->
                viewModel.submitMedicalRecord(
                    queueId = record.id,
                    childName = record.childName,
                    parentId = record.parentId,
                    doctorName = record.doctorName,
                    serviceName = record.serviceName,
                    notes = record.notes,
                    height = record.height,
                    weight = record.weight,
                    prescription = record.prescription,
                    servicePrice = service.price
                )
                examiningItem = null
            }
        )
    }
}

@Composable
fun ExaminePatientDialog(
    queueItem: QueueItem,
    services: List<Service>,
    onDismiss: () -> Unit,
    onSubmit: (MedicalRecord, Service) -> Unit
) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var selectedService by remember { mutableStateOf(services.firstOrNull() ?: Service("0", "Umum", 100000.0)) }
    var serviceExpanded by remember { mutableStateOf(false) }

    // Prescription list helper state
    val prescriptionList = remember { mutableStateListOf<PrescriptionItem>() }
    var medicineName by remember { mutableStateOf("") }
    var medicineDosage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Pemeriksaan Medis: ${queueItem.childName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Slate900
                    )
                    Divider(color = Slate100, modifier = Modifier.padding(top = 8.dp))
                }

                // Vital Signs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Tinggi Badan (cm)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                            OutlinedTextField(
                                value = height,
                                onValueChange = { height = it },
                                placeholder = { Text("misal: 110 cm") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Berat Badan (kg)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                placeholder = { Text("misal: 18 kg") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Layanan
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Tindakan / Layanan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                        Box {
                            OutlinedCard(
                                onClick = { serviceExpanded = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedService.name, fontSize = 13.sp, color = Slate800)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            }
                            DropdownMenu(expanded = serviceExpanded, onDismissRequest = { serviceExpanded = false }) {
                                services.forEach { service ->
                                    DropdownMenuItem(
                                        text = { Text("${service.name} (${formatRupiah(service.price)})") },
                                        onClick = {
                                            selectedService = service
                                            serviceExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Medical Diagnosis Notes
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Catatan Pemeriksaan & Diagnosa", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("Tulis instruksi atau catatan resep di sini...") },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Add Prescription Block
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate50, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("RESEP OBAT (OPSIONAL)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandOrange)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = medicineName,
                                onValueChange = { medicineName = it },
                                placeholder = { Text("Nama Obat", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1.2f)
                            )
                            OutlinedTextField(
                                value = medicineDosage,
                                onValueChange = { medicineDosage = it },
                                placeholder = { Text("Dosis", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(0.8f)
                            )
                        }

                        Button(
                            onClick = {
                                if (medicineName.isNotBlank() && medicineDosage.isNotBlank()) {
                                    prescriptionList.add(PrescriptionItem(medicineName, medicineDosage))
                                    medicineName = ""
                                    medicineDosage = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ Tambah Obat", fontSize = 11.sp)
                        }

                        // Added Medicines List
                        if (prescriptionList.isNotEmpty()) {
                            Divider(color = Slate200, modifier = Modifier.padding(vertical = 4.dp))
                            prescriptionList.forEach { med ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(med.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                                        Text(med.dosage, fontSize = 11.sp, color = Slate600)
                                    }
                                    IconButton(onClick = { prescriptionList.remove(med) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandOrange, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Dialog Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                            Text("Batal", color = Slate400)
                        }
                        Button(
                            onClick = {
                                if (notes.isNotBlank()) {
                                    val newRecord = MedicalRecord(
                                        id = queueItem.id, // Pass queue ID to reference which queue item we complete
                                        childName = queueItem.childName,
                                        parentId = queueItem.parentId,
                                        doctorName = queueItem.doctorName,
                                        serviceName = selectedService.name,
                                        date = "", // ViewModel handles Date
                                        notes = notes,
                                        height = height.ifBlank { null },
                                        weight = weight.ifBlank { null },
                                        prescription = prescriptionList.toList()
                                    )
                                    onSubmit(newRecord, selectedService)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                            enabled = notes.isNotBlank(),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Selesai", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorCompletedTab(viewModel: ClinicViewModel, doctorName: String) {
    // Records written by this doctor
    val records = viewModel.medicalRecords.filter { it.doctorName == doctorName }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Pasien Selesai Diperiksa", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
        }

        if (records.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada riwayat rekam medis hari ini", color = Slate400, fontSize = 12.sp)
                }
            }
        } else {
            items(records.reversed()) { record ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Slate100),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = record.childName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Slate900
                            )
                            Text(
                                text = record.date,
                                fontSize = 11.sp,
                                color = Slate400,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Divider(color = Slate100)

                        Text(
                            text = "Layanan: ${record.serviceName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandOrange
                        )

                        if (!record.height.isNullOrEmpty() || !record.weight.isNullOrEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                record.height?.let { Text("TB: $it", fontSize = 11.sp, color = Slate600) }
                                record.weight?.let { Text("BB: $it", fontSize = 11.sp, color = Slate600) }
                            }
                        }

                        Text(
                            text = "Hasil Pemeriksaan:\n${record.notes}",
                            fontSize = 12.sp,
                            color = Slate600
                        )

                        if (record.prescription.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Slate50, RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("Resep:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandOrange)
                                record.prescription.forEach { item ->
                                    Text("• ${item.name} (${item.dosage})", fontSize = 11.sp, color = Slate800)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorProfileTab(viewModel: ClinicViewModel, doctor: Doctor) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .background(SoftOrangeBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Avatar", tint = BrandOrange, modifier = Modifier.size(54.dp))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(doctor.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Slate900)
            Text(doctor.specialty, fontSize = 13.sp, color = BrandOrange, fontWeight = FontWeight.Bold)
            Text("Rumah Sakit CeriaCare Utama", fontSize = 12.sp, color = Slate400)
        }

        Divider(color = Slate100)

        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("LOGOUT DARI PORTAL", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
