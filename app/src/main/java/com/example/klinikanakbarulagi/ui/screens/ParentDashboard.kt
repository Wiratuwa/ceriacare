package com.example.klinikanakbarulagi.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ParentDashboard(
    viewModel: ClinicViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("home") }
    val activeParent = viewModel.parents.find { it.id == viewModel.currentParentId } ?: viewModel.parents.first()

    Scaffold(
        topBar = {
            CeriaTopBar(
                title = "Halo Bunda,",
                subtitle = activeParent.name,
                containerColor = BrandBlue,
                icon = Icons.Default.ChildCare,
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color.White)
                    }
                }
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
                        Triple("home", Icons.Default.GridView, "Beranda"),
                        Triple("booking", Icons.Default.CalendarToday, "Jadwal"),
                        Triple("history", Icons.Default.Assignment, "Catatan"),
                        Triple("payment", Icons.Default.Wallet, "Bayar"),
                        Triple("profile", Icons.Default.Face, "Profil")
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
                                selectedIconColor = BrandBlue,
                                selectedTextColor = BrandBlue,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400,
                                indicatorColor = SoftBlueBg
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
                    "home" -> ParentHomeTab(viewModel)
                    "booking" -> ParentBookingTab(viewModel)
                    "history" -> ParentHistoryTab(viewModel)
                    "payment" -> ParentPaymentTab(viewModel)
                    "profile" -> ParentProfileTab(viewModel)
                }
            }
        }
    }
}

fun formatRupiah(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return format.format(value).replace("Rp", "Rp ").replace(",00", "")
}

@Composable
fun ParentHomeTab(viewModel: ClinicViewModel) {
    val parentId = viewModel.currentParentId
    val myChildren = viewModel.childrenDb[parentId] ?: emptyList()
    val nearestQueue = viewModel.queue.find { it.parentId == parentId && it.status != QueueStatus.Completed }

    var showAddChildDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 140.dp, start = 24.dp, end = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Hero / Nearest Queue Section
        item {
            val gradient = Brush.verticalGradient(listOf(BrandBlue, BrandBlueDark))
            Surface(
                shape = RoundedCornerShape(40.dp),
                shadowElevation = 20.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(gradient)
                        .padding(28.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                                }
                            }
                            Column {
                                Text("ANTRIAN TERDEKAT", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.7f))
                                Text(
                                    text = nearestQueue?.let { "${it.childName}" } ?: "Siap untuk diperiksa?",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White
                                )
                            }
                        }
                        
                        if (nearestQueue != null) {
                            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Nomor Antrian", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.7f))
                                    Text(nearestQueue.no, style = MaterialTheme.typography.displayMedium, color = Color.White)
                                }
                                CeriaBadge(
                                    text = if (nearestQueue.status == QueueStatus.Examining) "Sedang Diperiksa" else "Menunggu Giliran",
                                    containerColor = if (nearestQueue.status == QueueStatus.Examining) BrandOrange else BrandGreen
                                )
                            }
                        } else {
                            Text(
                                "Klik menu Jadwal untuk melakukan pendaftaran konsultasi hari ini.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Child Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("Buah Hati Bunda", style = MaterialTheme.typography.titleMedium, color = Slate900)
                    Text(
                        text = "+ DAFTAR BARU",
                        style = MaterialTheme.typography.labelLarge,
                        color = BrandBlue,
                        modifier = Modifier.clickable { showAddChildDialog = true }
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(myChildren) { child ->
                        CeriaCard(
                            modifier = Modifier.width(140.dp),
                            elevation = 8
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (child.gender == "Laki-laki") SoftBlueBg else SoftOrangeBg,
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (child.gender == "Laki-laki") Icons.Default.Male else Icons.Default.Female,
                                            contentDescription = null,
                                            tint = if (child.gender == "Laki-laki") BrandBlue else BrandOrange,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        child.name, 
                                        style = MaterialTheme.typography.titleMedium, 
                                        fontSize = 14.sp,
                                        color = Slate900, 
                                        textAlign = TextAlign.Center, 
                                        maxLines = 1
                                    )
                                    Text(
                                        child.age, 
                                        style = MaterialTheme.typography.bodySmall, 
                                        color = Slate400,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Queue Monitor (Redesigned with Objects)
        item {
            Text("Pantau Antrian Live", style = MaterialTheme.typography.titleMedium, color = Slate900)
        }

        val activeQueues = viewModel.queue.filter { it.status != QueueStatus.Completed }
        if (activeQueues.isEmpty()) {
            item {
                CeriaCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Sedang tidak ada antrian aktif di klinik saat ini.",
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
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (item.status == QueueStatus.Examining) SoftOrangeBg else SoftBlueBg,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = item.no,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (item.status == QueueStatus.Examining) BrandOrange else BrandBlue
                                )
                            }
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.childName, style = MaterialTheme.typography.titleMedium, color = Slate900)
                            Text("dr. ${item.doctorName.replace("dr. ", "")}", style = MaterialTheme.typography.bodySmall, color = Slate400)
                        }

                        CeriaBadge(
                            text = if (item.status == QueueStatus.Examining) "PERIKSA" else "ANTRI",
                            containerColor = if (item.status == QueueStatus.Examining) BrandOrange else BrandBlue
                        )
                    }
                }
            }
        }
    }

    if (showAddChildDialog) {
        AddChildDialog(
            onDismiss = { showAddChildDialog = false },
            onAdd = { name, age, gender ->
                viewModel.addChildToParent(parentId, name, age, gender)
                showAddChildDialog = false
            }
        )
    }
}

@Composable
fun AddChildDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Laki-laki") }

    Dialog(onDismissRequest = onDismiss) {
        CeriaCard(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Text("Daftar Data Anak", style = MaterialTheme.typography.titleLarge, color = Slate900)

                CeriaTextField(label = "Nama Lengkap Anak", value = name, onValueChange = { name = it }, placeholder = "Contoh: Arka Pratama")
                CeriaTextField(label = "Umur Anak", value = age, onValueChange = { age = it }, placeholder = "Contoh: 3 tahun")

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("JENIS KELAMIN", style = MaterialTheme.typography.labelLarge, color = Slate400, modifier = Modifier.padding(start = 16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CeriaButton(
                            text = "Laki-laki",
                            onClick = { gender = "Laki-laki" },
                            containerColor = if (gender == "Laki-laki") BrandBlue else Slate50,
                            contentColor = if (gender == "Laki-laki") Color.White else Slate600,
                            modifier = Modifier.weight(1f)
                        )
                        CeriaButton(
                            text = "Perempuan",
                            onClick = { gender = "Perempuan" },
                            containerColor = if (gender == "Perempuan") BrandBlue else Slate50,
                            contentColor = if (gender == "Perempuan") Color.White else Slate600,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("BATAL", style = MaterialTheme.typography.labelLarge, color = Slate400)
                    }
                    CeriaButton(
                        text = "SIMPAN",
                        onClick = { if (name.isNotBlank()) onAdd(name, age, gender) },
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun ParentBookingTab(viewModel: ClinicViewModel) {
    val parentId = viewModel.currentParentId
    val parentName = viewModel.parents.find { it.id == parentId }?.name ?: "Bunda"
    val children = viewModel.childrenDb[parentId] ?: emptyList()

    var selectedChildName by remember { mutableStateOf("") }
    var selectedServiceName by remember { mutableStateOf("") }
    var selectedDoctorName by remember { mutableStateOf("") }
    var bookingDate by remember { mutableStateOf("2026-05-23") }
    var bookingTime by remember { mutableStateOf("09:00") }

    var childExpanded by remember { mutableStateOf(false) }
    var serviceExpanded by remember { mutableStateOf(false) }
    var doctorExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(children, viewModel.services, viewModel.doctors) {
        if (selectedChildName.isEmpty() && children.isNotEmpty()) selectedChildName = children.first().name
        if (selectedServiceName.isEmpty() && viewModel.services.isNotEmpty()) selectedServiceName = viewModel.services.first().name
        if (selectedDoctorName.isEmpty() && viewModel.doctors.isNotEmpty()) selectedDoctorName = viewModel.doctors.first().name
    }

    val myBookings = viewModel.bookings.filter { it.parentId == parentId }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 140.dp, start = 24.dp, end = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            CeriaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Text("Jadwalkan Kunjungan", style = MaterialTheme.typography.titleLarge, color = Slate900)

                    BookingSelector(label = "Pilih Anak", selected = selectedChildName, onClick = { childExpanded = true })
                    BookingSelector(label = "Layanan Klinik", selected = selectedServiceName, onClick = { serviceExpanded = true })
                    BookingSelector(label = "Pilih Dokter", selected = selectedDoctorName, onClick = { doctorExpanded = true })

                    DropdownMenu(expanded = childExpanded, onDismissRequest = { childExpanded = false }) {
                        children.forEach { child -> DropdownMenuItem(text = { Text(child.name) }, onClick = { selectedChildName = child.name; childExpanded = false }) }
                    }
                    DropdownMenu(expanded = serviceExpanded, onDismissRequest = { serviceExpanded = false }) {
                        viewModel.services.forEach { service -> DropdownMenuItem(text = { Text("${service.name} (${formatRupiah(service.price)})") }, onClick = { selectedServiceName = service.name; serviceExpanded = false }) }
                    }
                    DropdownMenu(expanded = doctorExpanded, onDismissRequest = { doctorExpanded = false }) {
                        viewModel.doctors.forEach { doc -> DropdownMenuItem(text = { Text(doc.name) }, onClick = { selectedDoctorName = doc.name; doctorExpanded = false }) }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CeriaTextField(label = "Tanggal", value = bookingDate, onValueChange = { bookingDate = it }, modifier = Modifier.weight(1.2f))
                        CeriaTextField(label = "Jam", value = bookingTime, onValueChange = { bookingTime = it }, modifier = Modifier.weight(0.8f))
                    }

                    CeriaButton(
                        text = "BUAT JADWAL SEKARANG",
                        onClick = {
                            if (selectedChildName.isNotEmpty() && selectedServiceName.isNotEmpty() && selectedDoctorName.isNotEmpty()) {
                                viewModel.createBooking(parentId, parentName, selectedChildName, selectedServiceName, selectedDoctorName, bookingDate, bookingTime)
                            }
                        },
                        enabled = children.isNotEmpty()
                    )
                }
            }
        }

        item {
            Text("Riwayat Booking", style = MaterialTheme.typography.titleMedium, color = Slate900)
        }

        if (myBookings.isEmpty()) {
            item {
                Text("Belum ada riwayat booking.", style = MaterialTheme.typography.bodyMedium, color = Slate400, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        } else {
            items(myBookings.reversed()) { book ->
                CeriaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(book.childName, style = MaterialTheme.typography.titleMedium, color = Slate900)
                            CeriaBadge(
                                text = book.status.name,
                                containerColor = when (book.status) { BookingStatus.PENDING -> Slate100; BookingStatus.APPROVED -> SoftGreenBg; BookingStatus.REJECTED -> SoftOrangeBg },
                                contentColor = when (book.status) { BookingStatus.PENDING -> Slate600; BookingStatus.APPROVED -> BrandGreen; BookingStatus.REJECTED -> BrandOrange }
                            )
                        }
                        HorizontalDivider(color = Slate50)
                        Text("${book.serviceName} • dr. ${book.doctorName.replace("dr. ", "")}", style = MaterialTheme.typography.bodyLarge, color = Slate800)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            LabelledIcon(Icons.Default.Event, book.date)
                            LabelledIcon(Icons.Default.Schedule, book.time)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingSelector(label: String, selected: String, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelLarge, color = Slate400, modifier = Modifier.padding(start = 16.dp))
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Slate50,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(selected.ifEmpty { label }, style = MaterialTheme.typography.bodyLarge, color = if (selected.isEmpty()) Slate400 else Slate800)
                Icon(Icons.Default.UnfoldMore, contentDescription = null, tint = Slate400)
            }
        }
    }
}

@Composable
fun LabelledIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Slate600)
    }
}

@Composable
fun ParentHistoryTab(viewModel: ClinicViewModel) {
    val parentId = viewModel.currentParentId
    val records = viewModel.medicalRecords.filter { it.parentId == parentId }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 140.dp, start = 24.dp, end = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { Text("Riwayat Rekam Medis", style = MaterialTheme.typography.titleMedium, color = Slate900) }

        if (records.isEmpty()) {
            item { Text("Belum ada rekam medis.", style = MaterialTheme.typography.bodyMedium, color = Slate400, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp)) }
        } else {
            items(records.reversed()) { record ->
                CeriaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(record.childName, style = MaterialTheme.typography.titleLarge, color = Slate900)
                            Text(record.date, style = MaterialTheme.typography.bodySmall, color = Slate400)
                        }
                        Text("${record.serviceName} • dr. ${record.doctorName.replace("dr. ", "")}", style = MaterialTheme.typography.labelLarge, color = BrandBlue)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                            record.height?.let { Text("TB: $it", style = MaterialTheme.typography.titleMedium, color = Slate800) }
                            record.weight?.let { Text("BB: $it", style = MaterialTheme.typography.titleMedium, color = Slate800) }
                        }

                        Surface(shape = RoundedCornerShape(24.dp), color = Slate50) {
                            Text(record.notes, style = MaterialTheme.typography.bodyMedium, color = Slate600, modifier = Modifier.padding(20.dp))
                        }

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
                                            Text("RESEP OBAT", style = MaterialTheme.typography.labelLarge, color = BrandOrange)
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
fun ParentPaymentTab(viewModel: ClinicViewModel) {
    val parentId = viewModel.currentParentId
    val myInvoices = viewModel.invoices.filter { it.parentId == parentId }
    var selectedInvoiceToPay by remember { mutableStateOf<Invoice?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 140.dp, start = 24.dp, end = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { Text("Administrasi & Bayar", style = MaterialTheme.typography.titleMedium, color = Slate900) }

        val unpaid = myInvoices.filter { it.status != InvoiceStatus.PAID }
        val paid = myInvoices.filter { it.status == InvoiceStatus.PAID }

        item { Text("BELUM TERBAYAR", style = MaterialTheme.typography.labelLarge, color = Slate400) }

        if (unpaid.isEmpty()) {
            item { CeriaCard(modifier = Modifier.fillMaxWidth()) { Text("Semua tagihan lunas!", style = MaterialTheme.typography.bodyMedium, color = Slate400, modifier = Modifier.padding(32.dp), textAlign = TextAlign.Center) } }
        } else {
            items(unpaid) { inv ->
                CeriaCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(inv.item, style = MaterialTheme.typography.titleMedium, color = Slate900)
                            Text(formatRupiah(inv.price), style = MaterialTheme.typography.titleLarge, color = BrandOrange)
                        }
                        if (inv.status == InvoiceStatus.PENDING) {
                            CeriaButton(text = "BAYAR", onClick = { selectedInvoiceToPay = inv }, modifier = Modifier.width(100.dp))
                        } else {
                            CeriaBadge(text = "PROSES", containerColor = Slate100, contentColor = Slate600)
                        }
                    }
                }
            }
        }

        item { Text("RIWAYAT TRANSAKSI", style = MaterialTheme.typography.labelLarge, color = Slate400) }
        items(paid.reversed()) { inv ->
            CeriaCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(inv.item, style = MaterialTheme.typography.titleMedium, color = Slate900)
                        Text(formatRupiah(inv.price), style = MaterialTheme.typography.bodyMedium, color = Slate600)
                        CeriaBadge(
                            text = "LUNAS", 
                            containerColor = SoftGreenBg, 
                            contentColor = BrandGreen,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        tint = BrandGreen, 
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }

    if (selectedInvoiceToPay != null) {
        val inv = selectedInvoiceToPay!!
        Dialog(onDismissRequest = { selectedInvoiceToPay = null }) {
            CeriaCard(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Text("Konfirmasi Bayar", style = MaterialTheme.typography.titleLarge, color = Slate900)
                    Text("Total Tagihan: ${formatRupiah(inv.price)}", style = MaterialTheme.typography.displayMedium, color = BrandOrange)
                    
                    Surface(shape = RoundedCornerShape(24.dp), color = SoftBlueBg) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("BANK MANDIRI", style = MaterialTheme.typography.labelLarge, color = BrandBlue)
                            Text("123-000-456-7890", style = MaterialTheme.typography.titleLarge, color = BrandBlueDark)
                            Text("a/n CeriaCare Indonesia", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        TextButton(onClick = { selectedInvoiceToPay = null }, modifier = Modifier.weight(1f)) { Text("BATAL", color = Slate400) }
                        CeriaButton(text = "KONFIRMASI", onClick = { viewModel.payInvoice(inv.id); selectedInvoiceToPay = null }, modifier = Modifier.weight(1.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun ParentProfileTab(viewModel: ClinicViewModel) {
    val parentId = viewModel.currentParentId
    val parent = viewModel.parents.find { it.id == parentId } ?: viewModel.parents.first()
    var showAddChildDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = SoftBlueBg,
            modifier = Modifier.size(140.dp).shadow(20.dp, CircleShape)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(100.dp))
            }
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(parent.name, style = MaterialTheme.typography.displayMedium, color = Slate900)
            Text(parent.email, style = MaterialTheme.typography.bodyLarge, color = Slate400)
            CeriaBadge(text = parent.memberType, containerColor = SoftBlueBg, contentColor = BrandBlue)
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            ProfileActionButton(icon = Icons.Default.AddBox, label = "Daftar Data Anak", onClick = { showAddChildDialog = true })
            CeriaButton(text = "KELUAR AKUN", onClick = { viewModel.logout() }, containerColor = BrandOrange)
        }
    }

    if (showAddChildDialog) {
        AddChildDialog(onDismiss = { showAddChildDialog = false }, onAdd = { name, age, gender -> viewModel.addChildToParent(parentId, name, age, gender); showAddChildDialog = false })
    }
}

@Composable
fun ProfileActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    CeriaCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Surface(shape = CircleShape, color = SoftBlueBg, modifier = Modifier.size(44.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(24.dp))
                    }
                }
                Text(label, style = MaterialTheme.typography.titleMedium, color = Slate800)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
        }
    }
}
