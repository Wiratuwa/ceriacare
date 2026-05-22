package com.example.klinikanakbarulagi.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.klinikanakbarulagi.model.*
import com.example.klinikanakbarulagi.ui.theme.*
import com.example.klinikanakbarulagi.viewmodel.ClinicViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboard(
    viewModel: ClinicViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("home") }

    val activeParent = viewModel.parents.find { it.id == viewModel.currentParentId } ?: viewModel.parents.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CERIACARE APP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = activeParent.name,
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
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifikasi",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBlue),
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
                    selected = activeTab == "home",
                    onClick = { activeTab = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandBlue,
                        selectedTextColor = BrandBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftBlueBg
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "booking",
                    onClick = { activeTab = "booking" },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Booking") },
                    label = { Text("Booking", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandBlue,
                        selectedTextColor = BrandBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftBlueBg
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "history",
                    onClick = { activeTab = "history" },
                    icon = { Icon(Icons.Default.History, contentDescription = "Riwayat") },
                    label = { Text("Riwayat", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandBlue,
                        selectedTextColor = BrandBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftBlueBg
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "payment",
                    onClick = { activeTab = "payment" },
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = "Bayar") },
                    label = { Text("Bayar", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandBlue,
                        selectedTextColor = BrandBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftBlueBg
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "profile",
                    onClick = { activeTab = "profile" },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandBlue,
                        selectedTextColor = BrandBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftBlueBg
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
                "home" -> ParentHomeTab(viewModel)
                "booking" -> ParentBookingTab(viewModel)
                "history" -> ParentHistoryTab(viewModel)
                "payment" -> ParentPaymentTab(viewModel)
                "profile" -> ParentProfileTab(viewModel)
            }
        }
    }
}

// Format currency
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Nearest Schedule / Queue Widget
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Jadwal",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "JADWAL TERDEKAT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        if (nearestQueue != null) {
                            Text(
                                text = "Antrian: ${nearestQueue.childName} (${nearestQueue.no})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Status: ${nearestQueue.status} - dr. ${nearestQueue.doctorName.replace("dr. ", "")}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        } else {
                            Text(
                                text = "Tidak ada jadwal terdekat",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Silakan booking di menu Booking",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // My Children Row List
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Anak Saya", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                    Text(
                        text = "+ Tambah Anak",
                        color = BrandBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { showAddChildDialog = true }
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(myChildren) { child ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Slate100),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.width(130.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(SoftBlueBg, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (child.gender == "Laki-laki") Icons.Default.Male else Icons.Default.Female,
                                        contentDescription = child.gender,
                                        tint = BrandBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = child.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Slate900,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Text(
                                    text = child.age,
                                    fontSize = 10.sp,
                                    color = Slate400,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    if (myChildren.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Slate100),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showAddChildDialog = true }
                            ) {
                                Text(
                                    text = "Belum ada data anak. Ketuk untuk menambah.",
                                    fontSize = 12.sp,
                                    color = Slate400,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Queue Monitor Tracker
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Pantau Antrian Live", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Slate100),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val activeQueues = viewModel.queue.filter { it.status != QueueStatus.Completed }
                        if (activeQueues.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Tidak ada antrian aktif saat ini", color = Slate400, fontSize = 12.sp)
                            }
                        } else {
                            activeQueues.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Slate50, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(
                                                    if (item.status == QueueStatus.Examining) SoftOrangeBg else Slate200,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = item.no,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp,
                                                color = if (item.status == QueueStatus.Examining) BrandOrange else Slate800
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = item.childName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Slate900
                                            )
                                            Text(
                                                text = "dr. ${item.doctorName.replace("dr. ", "")}",
                                                fontSize = 10.sp,
                                                color = Slate400
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (item.status == QueueStatus.Examining) SoftOrangeBg else SoftBlueBg
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (item.status == QueueStatus.Examining) "Diperiksa" else "Menunggu",
                                            color = if (item.status == QueueStatus.Examining) BrandOrange else BrandBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
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

    // Add Child Dialog
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
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Tambah Profil Anak", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate900)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nama Lengkap Anak", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Contoh: Arka Pratama") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Umur Anak (misal: 3 tahun)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        placeholder = { Text("Contoh: 3 tahun") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Jenis Kelamin", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { gender = "Laki-laki" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (gender == "Laki-laki") BrandBlue else Slate100,
                                contentColor = if (gender == "Laki-laki") Color.White else Slate800
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Laki-laki", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { gender = "Perempuan" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (gender == "Perempuan") BrandBlue else Slate100,
                                contentColor = if (gender == "Perempuan") Color.White else Slate800
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Perempuan", fontSize = 12.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Batal", color = Slate400)
                    }
                    Button(
                        onClick = { if (name.isNotBlank()) onAdd(name, age, gender) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text("Tambah")
                    }
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

    // Dropdown Expand States
    var childExpanded by remember { mutableStateOf(false) }
    var serviceExpanded by remember { mutableStateOf(false) }
    var doctorExpanded by remember { mutableStateOf(false) }

    // Init selection
    LaunchedEffect(children, viewModel.services, viewModel.doctors) {
        if (selectedChildName.isEmpty() && children.isNotEmpty()) selectedChildName = children.first().name
        if (selectedServiceName.isEmpty() && viewModel.services.isNotEmpty()) selectedServiceName = viewModel.services.first().name
        if (selectedDoctorName.isEmpty() && viewModel.doctors.isNotEmpty()) selectedDoctorName = viewModel.doctors.first().name
    }

    val myBookings = viewModel.bookings.filter { it.parentId == parentId }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Booking Form Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Slate100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Buat Booking Kunjungan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)

                    // Select Child
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Pilih Anak", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                        Box {
                            OutlinedCard(
                                shape = RoundedCornerShape(12.dp),
                                onClick = { childExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedChildName.ifEmpty { "Pilih Anak" }, fontSize = 13.sp, color = Slate800)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            }
                            DropdownMenu(expanded = childExpanded, onDismissRequest = { childExpanded = false }) {
                                children.forEach { child ->
                                    DropdownMenuItem(
                                        text = { Text(child.name) },
                                        onClick = {
                                            selectedChildName = child.name
                                            childExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Select Service
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Pilih Layanan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                        Box {
                            OutlinedCard(
                                shape = RoundedCornerShape(12.dp),
                                onClick = { serviceExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedServiceName.ifEmpty { "Pilih Layanan" }, fontSize = 13.sp, color = Slate800)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            }
                            DropdownMenu(expanded = serviceExpanded, onDismissRequest = { serviceExpanded = false }) {
                                viewModel.services.forEach { service ->
                                    DropdownMenuItem(
                                        text = { Text("${service.name} (${formatRupiah(service.price)})") },
                                        onClick = {
                                            selectedServiceName = service.name
                                            serviceExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Select Doctor
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Pilih Dokter", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                        Box {
                            OutlinedCard(
                                shape = RoundedCornerShape(12.dp),
                                onClick = { doctorExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedDoctorName.ifEmpty { "Pilih Dokter" }, fontSize = 13.sp, color = Slate800)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            }
                            DropdownMenu(expanded = doctorExpanded, onDismissRequest = { doctorExpanded = false }) {
                                viewModel.doctors.forEach { doc ->
                                    DropdownMenuItem(
                                        text = { Text("${doc.name} (${doc.specialty})") },
                                        onClick = {
                                            selectedDoctorName = doc.name
                                            doctorExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Date & Time Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Tanggal Kunjungan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                            OutlinedTextField(
                                value = bookingDate,
                                onValueChange = { bookingDate = it },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Column(modifier = Modifier.weight(0.8f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Jam", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                            OutlinedTextField(
                                value = bookingTime,
                                onValueChange = { bookingTime = it },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            if (selectedChildName.isNotEmpty() && selectedServiceName.isNotEmpty() && selectedDoctorName.isNotEmpty()) {
                                viewModel.createBooking(
                                    parentId = parentId,
                                    parentName = parentName,
                                    childName = selectedChildName,
                                    serviceName = selectedServiceName,
                                    doctorName = selectedDoctorName,
                                    date = bookingDate,
                                    time = bookingTime
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(24.dp),
                        enabled = children.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Buat Booking Baru", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (children.isEmpty()) {
                        Text(
                            text = "* Harap daftarkan anak di profil terlebih dahulu sebelum booking",
                            color = BrandOrange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Active Bookings List
        item {
            Text("Riwayat Kunjungan & Antrian", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
        }

        if (myBookings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada riwayat booking", color = Slate400, fontSize = 12.sp)
                }
            }
        } else {
            items(myBookings.reversed()) { book ->
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
                                text = "Kunjungan: ${book.childName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Slate900
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (book.status) {
                                            BookingStatus.PENDING -> Slate200
                                            BookingStatus.APPROVED -> SoftGreenBg
                                            BookingStatus.REJECTED -> SoftOrangeBg
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = book.status.name,
                                    color = when (book.status) {
                                        BookingStatus.PENDING -> Slate600
                                        BookingStatus.APPROVED -> BrandGreen
                                        BookingStatus.REJECTED -> BrandOrange
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        Divider(color = Slate100)

                        Text(
                            text = "${book.serviceName} • dr. ${book.doctorName.replace("dr. ", "")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Tanggal", tint = Slate400, modifier = Modifier.size(14.dp))
                                Text(book.date, fontSize = 11.sp, color = Slate400)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Waktu", tint = Slate400, modifier = Modifier.size(14.dp))
                                Text(book.time, fontSize = 11.sp, color = Slate400)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParentHistoryTab(viewModel: ClinicViewModel) {
    val parentId = viewModel.currentParentId
    val records = viewModel.medicalRecords.filter { it.parentId == parentId }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Riwayat Medis Anak", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
        }

        if (records.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada rekam medis yang tercatat", color = Slate400, fontSize = 12.sp)
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            text = "${record.serviceName} • dr. ${record.doctorName.replace("dr. ", "")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        )

                        if (!record.height.isNullOrEmpty() || !record.weight.isNullOrEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                record.height?.let {
                                    Text("Tinggi: $it", fontSize = 11.sp, color = Slate600, fontWeight = FontWeight.Bold)
                                }
                                record.weight?.let {
                                    Text("Berat: $it", fontSize = 11.sp, color = Slate600, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Text(
                            text = "Catatan Dokter:\n${record.notes}",
                            fontSize = 12.sp,
                            color = Slate600,
                            lineHeight = 16.sp
                        )

                        if (record.prescription.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Slate50, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "RESEP OBAT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandOrange,
                                        letterSpacing = 1.sp
                                    )
                                    record.prescription.forEach { item ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(item.name, fontSize = 11.sp, color = Slate800, fontWeight = FontWeight.Bold)
                                            Text(item.dosage, fontSize = 11.sp, color = Slate600)
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Tagihan & Pembayaran", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
        }

        val unpaidInvoices = myInvoices.filter { it.status != InvoiceStatus.PAID }
        val paidInvoices = myInvoices.filter { it.status == InvoiceStatus.PAID }

        item {
            Text("Tagihan Aktif", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate400)
        }

        if (unpaidInvoices.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Slate100),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak ada tagihan aktif", color = Slate400, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(unpaidInvoices) { inv ->
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
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = inv.item,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Slate900
                            )
                            Text(
                                text = "Anak: ${inv.childName} • Tagihan: ${inv.id}",
                                fontSize = 10.sp,
                                color = Slate400
                            )
                            Text(
                                text = formatRupiah(inv.price),
                                color = BrandOrange,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }

                        if (inv.status == InvoiceStatus.PENDING) {
                            Button(
                                onClick = { selectedInvoiceToPay = inv },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Bayar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate100)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Menunggu Verifikasi",
                                    color = Slate600,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Riwayat Pembayaran", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate400)
        }

        if (paidInvoices.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada riwayat pembayaran", color = Slate400, fontSize = 11.sp)
                }
            }
        } else {
            items(paidInvoices.reversed()) { inv ->
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
                        Column {
                            Text(
                                text = inv.item,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Slate900
                            )
                            Text(
                                text = "Tagihan: ${inv.id} • ${formatRupiah(inv.price)}",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftGreenBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Lunas", color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }

    // Payment Simulation Dialog
    if (selectedInvoiceToPay != null) {
        val inv = selectedInvoiceToPay!!
        Dialog(onDismissRequest = { selectedInvoiceToPay = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Simulasi Pembayaran", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate900)
                    Divider(color = Slate100)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("LAYANAN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)
                        Text(inv.item, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("TOTAL TRANSFER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)
                        Text(formatRupiah(inv.price), fontWeight = FontWeight.Black, fontSize = 20.sp, color = BrandOrange)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate50, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("BANK TRANSFER (MANUAL)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                        Text("Bank Mandiri: 123-000-456-7890", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                        Text("a/n CeriaCare Indonesia", fontSize = 11.sp, color = Slate600)
                    }

                    Text(
                        text = "Silakan klik konfirmasi di bawah untuk mensimulasikan upload bukti transfer bank manual.",
                        fontSize = 11.sp,
                        color = Slate400,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(onClick = { selectedInvoiceToPay = null }, modifier = Modifier.weight(1f)) {
                            Text("Batal", color = Slate400)
                        }
                        Button(
                            onClick = {
                                viewModel.payInvoice(inv.id)
                                selectedInvoiceToPay = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Upload Bukti", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
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
    val myChildren = viewModel.childrenDb[parentId] ?: emptyList()

    var showAddChildDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Avatar Card
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(SoftBlueBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Avatar", tint = BrandBlue, modifier = Modifier.size(54.dp))
        }

        // Profile Text Details
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(parent.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Slate900)
            Text(parent.email, fontSize = 12.sp, color = Slate400)
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SoftBlueBg)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(parent.memberType, color = BrandBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }

        Divider(color = Slate100)

        // Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Add Child Card Button
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate100),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAddChildDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ChildCare, contentDescription = "Add Child", tint = BrandBlue)
                        Text("Tambah Profil Anak", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate800)
                    }
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go", tint = Slate400)
                }
            }

            // Total Children Widget Indicator
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate100),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = "Anak", tint = Slate400)
                        Text("Jumlah Anak Terdaftar", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate800)
                    }
                    Text("${myChildren.size} Anak", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandBlue)
                }
            }

            // Log Out Button
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("LOGOUT DARI AKUN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
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
