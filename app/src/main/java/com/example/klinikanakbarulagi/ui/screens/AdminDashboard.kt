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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klinikanakbarulagi.model.*
import com.example.klinikanakbarulagi.ui.theme.*
import com.example.klinikanakbarulagi.viewmodel.ClinicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    viewModel: ClinicViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("monitor") }
    val activeAdminName = viewModel.admins.find { it.id == viewModel.currentAdminId }?.name ?: "Admin CeriaCare"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PORTAL ADMINISTRATOR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = activeAdminName,
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
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Admin",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandGreen),
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
                    selected = activeTab == "monitor",
                    onClick = { activeTab = "monitor" },
                    icon = { Icon(Icons.Default.Monitor, contentDescription = "Monitor") },
                    label = { Text("Monitor", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandGreen,
                        selectedTextColor = BrandGreen,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftGreenBg
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "payment",
                    onClick = { activeTab = "payment" },
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = "Keuangan") },
                    label = { Text("Keuangan", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandGreen,
                        selectedTextColor = BrandGreen,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftGreenBg
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "settings",
                    onClick = { activeTab = "settings" },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Master") },
                    label = { Text("Master", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandGreen,
                        selectedTextColor = BrandGreen,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = SoftGreenBg
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
                "monitor" -> AdminMonitorTab(viewModel)
                "payment" -> AdminPaymentTab(viewModel)
                "settings" -> AdminMasterTab(viewModel)
            }
        }
    }
}

@Composable
fun AdminMonitorTab(viewModel: ClinicViewModel) {
    val pendingBookings = viewModel.bookings.filter { it.status == BookingStatus.PENDING }
    val activeQueues = viewModel.queue.filter { it.status != QueueStatus.Completed }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pending Bookings Header
        item {
            Text("Pengajuan Booking Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
        }

        if (pendingBookings.isEmpty()) {
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
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak ada pengajuan booking baru", color = Slate400, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(pendingBookings) { booking ->
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
                            Column {
                                Text(
                                    text = "Pasien: ${booking.childName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Slate900
                                )
                                Text(
                                    text = "Ortu: ${booking.parentName}",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate100)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("PENDING", color = Slate600, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }

                        Divider(color = Slate100)

                        Text(
                            text = "${booking.serviceName} • dr. ${booking.doctorName.replace("dr. ", "")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )

                        Text(
                            text = "Jadwal: ${booking.date} pada ${booking.time}",
                            fontSize = 11.sp,
                            color = Slate600
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.rejectBooking(booking.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftOrangeBg, contentColor = BrandOrange),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Tolak", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.approveBooking(booking.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                modifier = Modifier.weight(1.5f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Setujui", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Active Queues Monitor
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Antrian Berjalan Hari Ini", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
        }

        if (activeQueues.isEmpty()) {
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
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak ada antrian berjalan", color = Slate400, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(activeQueues) { queueItem ->
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (queueItem.status == QueueStatus.Examining) SoftOrangeBg else SoftGreenBg,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = queueItem.no,
                                    fontWeight = FontWeight.Black,
                                    color = if (queueItem.status == QueueStatus.Examining) BrandOrange else BrandGreen,
                                    fontSize = 14.sp
                                )
                            }
                            Column {
                                Text(
                                    text = queueItem.childName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Slate900
                                )
                                Text(
                                    text = "Dokter: ${queueItem.doctorName.replace("dr. ", "")}",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                        }

                        // Admin status controls
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (queueItem.status == QueueStatus.Examining) SoftOrangeBg else SoftBlueBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable {
                                    // Let admin manually toggle examining/waiting for simulation
                                    val nextStat = if (queueItem.status == QueueStatus.Examining) QueueStatus.Waiting else QueueStatus.Examining
                                    viewModel.updateQueueStatus(queueItem.id, nextStat)
                                }
                        ) {
                            Text(
                                text = if (queueItem.status == QueueStatus.Examining) "Pemeriksaan" else "Menunggu",
                                color = if (queueItem.status == QueueStatus.Examining) BrandOrange else BrandBlue,
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

@Composable
fun AdminPaymentTab(viewModel: ClinicViewModel) {
    val pendingInvoices = viewModel.invoices.filter { it.status == InvoiceStatus.PENDING_CONFIRMATION }
    val paidInvoices = viewModel.invoices.filter { it.status == InvoiceStatus.PAID }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Verifikasi Keuangan & Transfer", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
        }

        item {
            Text("Menunggu Konfirmasi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate400)
        }

        if (pendingInvoices.isEmpty()) {
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
                        Text("Tidak ada pembayaran baru untuk diverifikasi", color = Slate400, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(pendingInvoices) { inv ->
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
                            Column {
                                Text(
                                    text = inv.item,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Slate900
                                )
                                Text(
                                    text = "Tagihan: ${inv.id} • Pasien: ${inv.childName}",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                            Text(
                                text = formatRupiah(inv.price),
                                fontWeight = FontWeight.Black,
                                color = BrandOrange,
                                fontSize = 15.sp
                            )
                        }

                        Divider(color = Slate100)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Transfer", tint = BrandBlue, modifier = Modifier.size(16.dp))
                                Text("Bukti Transfer Manual Diupload", fontSize = 11.sp, color = Slate600, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.verifyPayment(inv.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Verifikasi Lunas", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Pembayaran Lunas Hari Ini", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate400)
        }

        if (paidInvoices.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada riwayat lunas hari ini", color = Slate400, fontSize = 11.sp)
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
                            Text("Verifikasi Lunas", color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMasterTab(viewModel: ClinicViewModel) {
    // State variables for adding doctor/service
    var showAddDocRow by remember { mutableStateOf(false) }
    var docName by remember { mutableStateOf("") }
    var docSpec by remember { mutableStateOf("") }

    var showAddSrvRow by remember { mutableStateOf(false) }
    var srvName by remember { mutableStateOf("") }
    var srvPrice by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Services Master List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Master Layanan Klinik", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                Text(
                    text = if (showAddSrvRow) "Tutup" else "+ Tambah",
                    color = BrandGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { showAddSrvRow = !showAddSrvRow }
                )
            }
        }

        if (showAddSrvRow) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandGreen.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Tambah Layanan Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        OutlinedTextField(
                            value = srvName,
                            onValueChange = { srvName = it },
                            placeholder = { Text("Nama Layanan") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = srvPrice,
                            onValueChange = { srvPrice = it },
                            placeholder = { Text("Harga (Rupiah)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                val priceVal = srvPrice.toDoubleOrNull()
                                if (srvName.isNotBlank() && priceVal != null) {
                                    viewModel.addService(srvName, priceVal)
                                    srvName = ""
                                    srvPrice = ""
                                    showAddSrvRow = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simpan Layanan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(viewModel.services) { service ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate100),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(service.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                        Text(formatRupiah(service.price), fontSize = 11.sp, color = BrandOrange, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { viewModel.deleteService(service.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandOrange, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Doctors Master List
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Master Profil Dokter", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                Text(
                    text = if (showAddDocRow) "Tutup" else "+ Tambah",
                    color = BrandGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { showAddDocRow = !showAddDocRow }
                )
            }
        }

        if (showAddDocRow) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandGreen.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Tambah Dokter Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        OutlinedTextField(
                            value = docName,
                            onValueChange = { docName = it },
                            placeholder = { Text("Nama Lengkap Dokter (misal: dr. Andi, Sp.A)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = docSpec,
                            onValueChange = { docSpec = it },
                            placeholder = { Text("Poliklinik / Spesialisasi") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (docName.isNotBlank() && docSpec.isNotBlank()) {
                                    viewModel.addDoctor(docName, docSpec)
                                    docName = ""
                                    docSpec = ""
                                    showAddDocRow = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simpan Dokter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(viewModel.doctors) { doctor ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate100),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                        Text(doctor.specialty, fontSize = 11.sp, color = Slate600)
                    }
                    IconButton(onClick = { viewModel.deleteDoctor(doctor.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandOrange, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Action controls
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("LOGOUT DARI PORTAL ADMIN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
