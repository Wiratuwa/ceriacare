package com.example.klinikanakbarulagi.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.klinikanakbarulagi.model.*
import com.example.klinikanakbarulagi.ui.components.*
import com.example.klinikanakbarulagi.ui.theme.*
import com.example.klinikanakbarulagi.viewmodel.ClinicViewModel

@Composable
fun DoctorDashboard(
    viewModel: ClinicViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("queue") }
    val activeDoctor = viewModel.doctors.find { it.id == viewModel.currentDoctorId } ?: viewModel.doctors.first()

    Scaffold(
        topBar = {
            CeriaTopBar(
                title = "PORTAL DOKTER,",
                subtitle = activeDoctor.name,
                containerColor = BrandOrange,
                icon = Icons.Default.MedicalServices
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth().height(100.dp)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxSize().padding(bottom = 8.dp)
                ) {
                    val tabs = listOf(
                        Triple("queue", Icons.Default.FormatListNumbered, "Antrian"),
                        Triple("completed", Icons.AutoMirrored.Filled.FactCheck, "Selesai"),
                        Triple("profile", Icons.Default.AccountCircle, "Profil")
                    )
                    
                    tabs.forEach { (tag, icon, label) ->
                        NavigationBarItem(
                            selected = activeTab == tag,
                            onClick = { activeTab = tag },
                            icon = { 
                                Icon(
                                    imageVector = icon, 
                                    contentDescription = label,
                                    modifier = Modifier.size(24.dp)
                                ) 
                            },
                            label = { 
                                Text(
                                    label, 
                                    style = MaterialTheme.typography.labelLarge, 
                                    fontSize = 11.sp,
                                    fontWeight = if (activeTab == tag) FontWeight.Bold else FontWeight.Medium
                                ) 
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandOrange,
                                selectedTextColor = BrandOrange,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400,
                                indicatorColor = SoftOrangeBg
                            )
                        )
                    }
                }
            }
        },
        containerColor = Slate50,
        modifier = modifier.fillMaxSize().background(Slate50)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedTabContent(targetState = activeTab) { state ->
                when (state) {
                    "queue" -> DoctorQueueTab(viewModel, activeDoctor.name)
                    "completed" -> DoctorCompletedTab(viewModel, activeDoctor.name)
                    "profile" -> DoctorProfileTab(viewModel, activeDoctor)
                }
            }
        }
    }
}

@Composable
fun DoctorQueueTab(viewModel: ClinicViewModel, doctorName: String) {
    val activeQueues = viewModel.queue.filter { it.doctorName == doctorName && it.status != QueueStatus.Completed }
    var examiningItem by remember { mutableStateOf<QueueItem?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 140.dp, start = 24.dp, end = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { Text("Antrian Pasien Hari Ini", style = MaterialTheme.typography.titleMedium, color = Slate900) }

        if (activeQueues.isEmpty()) {
            item {
                CeriaCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Tidak ada antrian aktif untuk Anda hari ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate400,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        } else {
            items(activeQueues) { item ->
                CeriaCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (item.status == QueueStatus.Examining) BrandOrange else Slate100
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (item.status == QueueStatus.Examining) BrandOrange else SoftOrangeBg,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = item.no,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontSize = 24.sp,
                                    color = if (item.status == QueueStatus.Examining) Color.White else BrandOrange
                                )
                            }
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.childName, style = MaterialTheme.typography.titleMedium, color = Slate900)
                            Text("Jam Booking: ${item.time}", style = MaterialTheme.typography.bodySmall, color = Slate400)
                        }

                        if (item.status == QueueStatus.Waiting) {
                            CeriaButton(
                                text = "PANGGIL",
                                onClick = { viewModel.updateQueueStatus(item.id, QueueStatus.Examining) },
                                modifier = Modifier.width(100.dp),
                                containerColor = BrandOrange
                            )
                        } else {
                            CeriaButton(
                                text = "PERIKSA",
                                onClick = { examiningItem = item },
                                modifier = Modifier.width(100.dp),
                                containerColor = BrandGreen
                            )
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
                viewModel.submitMedicalRecord(record.id, record.childName, record.parentId, record.doctorName, record.serviceName, record.notes, record.height, record.weight, record.prescription, service.price)
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

    val prescriptionList = remember { mutableStateListOf<PrescriptionItem>() }
    var medicineName by remember { mutableStateOf("") }
    var medicineDosage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        CeriaCard(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(8.dp)) {
            LazyColumn(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(28.dp)) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Rekam Medis Pasien", style = MaterialTheme.typography.labelLarge, color = BrandOrange)
                        Text(queueItem.childName, style = MaterialTheme.typography.displayMedium, color = Slate900)
                    }
                    HorizontalDivider(color = Slate100, modifier = Modifier.padding(top = 16.dp))
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        CeriaTextField(label = "Tinggi (cm)", value = height, onValueChange = { height = it }, placeholder = "110", modifier = Modifier.weight(1f))
                        CeriaTextField(label = "Berat (kg)", value = weight, onValueChange = { weight = it }, placeholder = "18", modifier = Modifier.weight(1f))
                    }
                }

                item {
                    BookingSelector(label = "Tindakan Medis", selected = selectedService.name, onClick = { serviceExpanded = true })
                    DropdownMenu(expanded = serviceExpanded, onDismissRequest = { serviceExpanded = false }) {
                        services.forEach { s -> DropdownMenuItem(text = { Text("${s.name} (${formatRupiah(s.price)})") }, onClick = { selectedService = s; serviceExpanded = false }) }
                    }
                }

                item {
                    CeriaTextField(
                        label = "Diagnosa & Instruksi",
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = "Tulis diagnosa atau instruksi perawatan di sini...",
                        singleLine = false,
                        modifier = Modifier.height(140.dp)
                    )
                }

                item {
                    Surface(shape = RoundedCornerShape(32.dp), color = SoftOrangeBg) {
                        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("RESEP OBAT", style = MaterialTheme.typography.labelLarge, color = BrandOrange)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                CeriaTextField(label = "Obat", value = medicineName, onValueChange = { medicineName = it }, modifier = Modifier.weight(1.2f))
                                CeriaTextField(label = "Dosis", value = medicineDosage, onValueChange = { medicineDosage = it }, modifier = Modifier.weight(0.8f))
                            }
                            CeriaButton(
                                text = "+ TAMBAH RESEP",
                                onClick = { 
                                    if (medicineName.isNotBlank() && medicineDosage.isNotBlank()) {
                                        prescriptionList.add(PrescriptionItem(medicineName, medicineDosage))
                                        medicineName = ""
                                        medicineDosage = ""
                                    } 
                                },
                                containerColor = BrandOrange
                            )

                            prescriptionList.forEach { med ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .padding(start = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${med.name} - ${med.dosage}", style = MaterialTheme.typography.bodyMedium, color = Slate900)
                                    IconButton(onClick = { prescriptionList.remove(med) }) {
                                        Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = BrandOrange)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                            Text("BATAL", style = MaterialTheme.typography.labelLarge, color = Slate400)
                        }
                        CeriaButton(
                            text = "SIMPAN PEMERIKSAAN",
                            onClick = { 
                                if (notes.isNotBlank()) {
                                    onSubmit(MedicalRecord(queueItem.id, queueItem.childName, queueItem.parentId, queueItem.doctorName, selectedService.name, "", notes, height, weight, prescriptionList.toList()), selectedService)
                                }
                            },
                            containerColor = BrandGreen,
                            modifier = Modifier.weight(2f),
                            enabled = notes.isNotBlank()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorCompletedTab(viewModel: ClinicViewModel, doctorName: String) {
    val records = viewModel.medicalRecords.filter { it.doctorName == doctorName }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 140.dp, start = 24.dp, end = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { Text("Pasien Selesai Hari Ini", style = MaterialTheme.typography.titleMedium, color = Slate900) }

        if (records.isEmpty()) {
            item {
                Text("Belum ada pasien yang diperiksa hari ini.", style = MaterialTheme.typography.bodyMedium, color = Slate400, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp))
            }
        } else {
            items(records.reversed()) { record ->
                CeriaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column {
                                Text(record.childName, style = MaterialTheme.typography.titleLarge, color = Slate900)
                                Text(record.date, style = MaterialTheme.typography.bodySmall, color = Slate400)
                            }
                            CeriaBadge(text = "SELESAI", containerColor = SoftGreenBg, contentColor = BrandGreen)
                        }
                        Text(record.serviceName, style = MaterialTheme.typography.labelLarge, color = BrandOrange)
                        HorizontalDivider(color = Slate50)
                        Text(record.notes, style = MaterialTheme.typography.bodyMedium, color = Slate600)

                        if (record.prescription.isNotEmpty()) {
                            var isExpanded by remember { mutableStateOf(false) }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    onClick = { isExpanded = !isExpanded },
                                    shape = RoundedCornerShape(24.dp),
                                    color = SoftOrangeBg,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Icon(Icons.Default.Medication, contentDescription = null, tint = BrandOrange)
                                            Text("DETAIL PENGGUNAAN OBAT", style = MaterialTheme.typography.labelLarge, color = BrandOrange)
                                        }
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = BrandOrange
                                        )
                                    }
                                }

                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    CeriaCard(containerColor = Color.White, borderColor = SoftOrangeBg) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            record.prescription.forEach { item ->
                                                var showDosage by remember { mutableStateOf(false) }

                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .clickable { showDosage = !showDosage }
                                                        .background(if (showDosage) Slate50 else Color.Transparent)
                                                        .padding(12.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(item.name, style = MaterialTheme.typography.bodyLarge, color = Slate900, fontWeight = FontWeight.Bold)
                                                        Icon(
                                                            imageVector = if (showDosage) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                            contentDescription = null,
                                                            tint = Slate400,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                    AnimatedVisibility(visible = showDosage) {
                                                        Text(
                                                            text = item.dosage,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = BrandOrange,
                                                            modifier = Modifier.padding(top = 8.dp)
                                                        )
                                                    }
                                                }
                                                if (item != record.prescription.last()) HorizontalDivider(color = Slate50, modifier = Modifier.padding(horizontal = 12.dp))
                                            }
                                        }
                                    }
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
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Surface(
            shape = CircleShape,
            color = SoftOrangeBg,
            modifier = Modifier
                .size(160.dp)
                .shadow(24.dp, CircleShape, spotColor = BrandOrange)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.MedicalInformation, 
                    contentDescription = null, 
                    tint = BrandOrange, 
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, 
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = doctor.name, 
                style = MaterialTheme.typography.displayMedium,
                fontSize = 28.sp, // Adjusted to prevent awkward wrapping
                color = Slate900,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            
            CeriaBadge(
                text = doctor.specialty, 
                containerColor = SoftOrangeBg,
                contentColor = BrandOrange
            )
            
            Text(
                text = "CeriaCare Pediatric Hospital", 
                style = MaterialTheme.typography.bodyMedium, 
                color = Slate400,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        CeriaButton(
            text = "KELUAR DARI PORTAL", 
            onClick = { viewModel.logout() }, 
            containerColor = BrandOrange,
            icon = Icons.AutoMirrored.Filled.Logout
        )
        
        Spacer(modifier = Modifier.height(112.dp)) // Clearance for bottom nav
    }
}
