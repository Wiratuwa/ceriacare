package com.example.klinikanakbarulagi.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klinikanakbarulagi.model.*
import com.example.klinikanakbarulagi.ui.components.*
import com.example.klinikanakbarulagi.ui.theme.*
import com.example.klinikanakbarulagi.viewmodel.ClinicViewModel

@Composable
fun AdminDashboard(
    viewModel: ClinicViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("monitor") }
    val activeAdminName = viewModel.admins.find { it.id == viewModel.currentAdminId }?.name ?: "Admin CeriaCare"

    Scaffold(
        topBar = {
            CeriaTopBar(
                title = "PUSAT KONTROL ADMIN,",
                subtitle = activeAdminName,
                containerColor = BrandGreen,
                icon = Icons.Default.AdminPanelSettings,
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
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
                        Triple("monitor", Icons.Default.MonitorHeart, "Monitor"),
                        Triple("payment", Icons.Default.AccountBalanceWallet, "Keuangan"),
                        Triple("settings", Icons.Default.Dns, "Data")
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
                                selectedIconColor = BrandGreen,
                                selectedTextColor = BrandGreen,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400,
                                indicatorColor = SoftGreenBg
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
                    "monitor" -> AdminMonitorTab(viewModel)
                    "payment" -> AdminPaymentTab(viewModel)
                    "settings" -> AdminMasterTab(viewModel)
                }
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
        contentPadding = PaddingValues(bottom = 140.dp, start = 24.dp, end = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item { Text("Persetujuan Booking", style = MaterialTheme.typography.titleMedium, color = Slate900) }

        if (pendingBookings.isEmpty()) {
            item {
                CeriaCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Semua pengajuan booking telah diproses.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate400,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        } else {
            items(pendingBookings) { booking ->
                CeriaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column {
                                Text(booking.childName, style = MaterialTheme.typography.titleLarge, color = Slate900)
                                Text("Orang Tua: ${booking.parentName}", style = MaterialTheme.typography.bodySmall, color = Slate400)
                            }
                            CeriaBadge(text = "PENDING", containerColor = Slate100, contentColor = Slate600)
                        }
                        HorizontalDivider(color = Slate50)
                        Text("${booking.serviceName} • dr. ${booking.doctorName.replace("dr. ", "")}", style = MaterialTheme.typography.labelLarge, color = Slate800)
                        Text("Jadwal: ${booking.date} @ ${booking.time}", style = MaterialTheme.typography.bodyMedium, color = Slate600)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            CeriaButton(
                                text = "TOLAK",
                                onClick = { viewModel.rejectBooking(booking.id) },
                                containerColor = SoftOrangeBg,
                                contentColor = BrandOrange,
                                modifier = Modifier.weight(1f)
                            )
                            CeriaButton(
                                text = "SETUJUI",
                                onClick = { viewModel.approveBooking(booking.id) },
                                containerColor = BrandGreen,
                                modifier = Modifier.weight(1.5f)
                            )
                        }
                    }
                }
            }
        }

        item { Text("Dashboard Antrian", style = MaterialTheme.typography.titleMedium, color = Slate900) }

        if (activeQueues.isEmpty()) {
            item { CeriaCard(modifier = Modifier.fillMaxWidth()) { Text("Klinik sedang sepi antrian.", style = MaterialTheme.typography.bodyMedium, color = Slate400, modifier = Modifier.padding(32.dp), textAlign = TextAlign.Center) } }
        } else {
            items(activeQueues) { item ->
                CeriaCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = if (item.status == QueueStatus.Examining) BrandOrange else BrandGreen,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item.no, style = MaterialTheme.typography.labelLarge, color = Color.White)
                            }
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.childName, style = MaterialTheme.typography.titleMedium, color = Slate900)
                            Text("Dokter: ${item.doctorName.replace("dr. ", "")}", style = MaterialTheme.typography.bodySmall, color = Slate400)
                        }

                        CeriaButton(
                            text = if (item.status == QueueStatus.Examining) "PERIKSA" else "ANTRI",
                            onClick = { 
                                val nextStat = if (item.status == QueueStatus.Examining) QueueStatus.Waiting else QueueStatus.Examining
                                viewModel.updateQueueStatus(item.id, nextStat)
                            },
                            containerColor = if (item.status == QueueStatus.Examining) BrandOrange else BrandBlue,
                            modifier = Modifier.width(100.dp)
                        )
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
        contentPadding = PaddingValues(bottom = 140.dp, start = 24.dp, end = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item { Text("Verifikasi Transaksi", style = MaterialTheme.typography.titleMedium, color = Slate900) }

        if (pendingInvoices.isEmpty()) {
            item { CeriaCard(modifier = Modifier.fillMaxWidth()) { Text("Tidak ada bukti transfer baru.", style = MaterialTheme.typography.bodyMedium, color = Slate400, modifier = Modifier.padding(32.dp), textAlign = TextAlign.Center) } }
        } else {
            items(pendingInvoices) { inv ->
                CeriaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column {
                                Text(inv.item, style = MaterialTheme.typography.titleMedium, color = Slate900)
                                Text("Pasien: ${inv.childName}", style = MaterialTheme.typography.bodySmall, color = Slate400)
                            }
                            Text(formatRupiah(inv.price), style = MaterialTheme.typography.displayMedium, fontSize = 24.sp, color = BrandOrange)
                        }
                        HorizontalDivider(color = Slate50)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = BrandBlue)
                                Text("BUKTI DITERIMA", style = MaterialTheme.typography.labelLarge, color = BrandBlue)
                            }
                            CeriaButton(text = "VERIFIKASI LUNAS", onClick = { viewModel.verifyPayment(inv.id) }, containerColor = BrandGreen, modifier = Modifier.width(180.dp))
                        }
                    }
                }
            }
        }

        item { Text("Riwayat Pelunasan", style = MaterialTheme.typography.titleMedium, color = Slate900) }
        items(paidInvoices.reversed()) { inv ->
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
}

@Composable
fun AdminMasterTab(viewModel: ClinicViewModel) {
    var showAddDoc by remember { mutableStateOf(false) }
    var docName by remember { mutableStateOf("") }
    var docSpec by remember { mutableStateOf("") }

    var showAddSrv by remember { mutableStateOf(false) }
    var srvName by remember { mutableStateOf("") }
    var srvPrice by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 140.dp, start = 24.dp, end = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Services
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Layanan Klinik", style = MaterialTheme.typography.titleMedium, color = Slate900)
                CeriaButton(
                    text = if (showAddSrv) "TUTUP" else "+ LAYANAN", 
                    onClick = { showAddSrv = !showAddSrv },
                    containerColor = if (showAddSrv) SoftOrangeBg else SoftGreenBg,
                    contentColor = if (showAddSrv) BrandOrange else BrandGreen,
                    modifier = Modifier.width(140.dp)
                )
            }
        }

        if (showAddSrv) {
            item {
                CeriaCard(borderColor = BrandGreen) {
                    Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        CeriaTextField(label = "Nama Layanan Baru", value = srvName, onValueChange = { srvName = it })
                        CeriaTextField(label = "Tarif Layanan (Rp)", value = srvPrice, onValueChange = { srvPrice = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        CeriaButton(text = "SIMPAN LAYANAN", onClick = { srvPrice.toDoubleOrNull()?.let { viewModel.addService(srvName, it); srvName = ""; srvPrice = ""; showAddSrv = false } }, containerColor = BrandGreen)
                    }
                }
            }
        }

        items(viewModel.services) { s ->
            CeriaCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(s.name, style = MaterialTheme.typography.titleMedium, color = Slate900)
                        Text(formatRupiah(s.price), style = MaterialTheme.typography.labelLarge, color = BrandOrange)
                    }
                    IconButton(
                        onClick = { viewModel.deleteService(s.id) },
                        modifier = Modifier.padding(start = 16.dp)
                    ) { 
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = BrandOrange) 
                    }
                }
            }
        }

        // Doctors
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Database Dokter", style = MaterialTheme.typography.titleMedium, color = Slate900)
                CeriaButton(
                    text = if (showAddDoc) "TUTUP" else "+ DOKTER", 
                    onClick = { showAddDoc = !showAddDoc },
                    containerColor = if (showAddDoc) SoftOrangeBg else SoftGreenBg,
                    contentColor = if (showAddDoc) BrandOrange else BrandGreen,
                    modifier = Modifier.width(140.dp)
                )
            }
        }

        if (showAddDoc) {
            item {
                CeriaCard(borderColor = BrandGreen) {
                    Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        CeriaTextField(label = "Nama Lengkap Dokter", value = docName, onValueChange = { docName = it }, placeholder = "dr. Nama, Sp.A")
                        CeriaTextField(label = "Spesialisasi / Poli", value = docSpec, onValueChange = { docSpec = it }, placeholder = "Poli Anak Umum")
                        CeriaButton(text = "SIMPAN DOKTER", onClick = { if (docName.isNotBlank()) { viewModel.addDoctor(docName, docSpec); docName = ""; docSpec = ""; showAddDoc = false } }, containerColor = BrandGreen)
                    }
                }
            }
        }

        items(viewModel.doctors) { d ->
            CeriaCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(d.name, style = MaterialTheme.typography.titleMedium, color = Slate900)
                        Text(d.specialty, style = MaterialTheme.typography.bodySmall, color = Slate400)
                    }
                    IconButton(
                        onClick = { viewModel.deleteDoctor(d.id) },
                        modifier = Modifier.padding(start = 16.dp)
                    ) { 
                        Icon(Icons.Default.PersonRemove, contentDescription = null, tint = BrandOrange) 
                    }
                }
            }
        }

        item {
            CeriaButton(
                text = "KELUAR SISTEM",
                onClick = { viewModel.logout() },
                containerColor = BrandOrange,
                icon = Icons.AutoMirrored.Filled.Logout
            )
        }
    }
}
