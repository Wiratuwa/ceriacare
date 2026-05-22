package com.example.klinikanakbarulagi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klinikanakbarulagi.model.Role
import com.example.klinikanakbarulagi.ui.theme.*
import com.example.klinikanakbarulagi.viewmodel.ClinicViewModel

@Composable
fun LoginScreen(
    viewModel: ClinicViewModel,
    modifier: Modifier = Modifier
) {
    var isRegistering by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (isRegistering) {
            RegisterView(
                onRegister = { name, email ->
                    viewModel.registerParent(name, email)
                },
                onBack = { isRegistering = false }
            )
        } else {
            LoginView(
                viewModel = viewModel,
                onGoToRegister = { isRegistering = true }
            )
        }
    }
}

@Composable
fun LoginView(
    viewModel: ClinicViewModel,
    onGoToRegister: () -> Unit
) {
    var loginMode by remember { mutableStateOf(Role.PARENT) }

    // Dropdown States
    var parentDropdownExpanded by remember { mutableStateOf(false) }
    var doctorDropdownExpanded by remember { mutableStateOf(false) }
    var adminDropdownExpanded by remember { mutableStateOf(false) }

    val activeParent = viewModel.parents.find { it.id == viewModel.currentParentId } ?: viewModel.parents.firstOrNull()
    val activeDoctor = viewModel.doctors.find { it.id == viewModel.currentDoctorId } ?: viewModel.doctors.firstOrNull()
    val activeAdmin = viewModel.admins.find { it.id == viewModel.currentAdminId } ?: viewModel.admins.firstOrNull()

    // Determine current theme parameters based on role
    val themeColor = when (loginMode) {
        Role.PARENT -> BrandBlue
        Role.DOCTOR -> BrandOrange
        Role.ADMIN -> BrandGreen
    }

    val themeSoftBg = when (loginMode) {
        Role.PARENT -> SoftBlueBg
        Role.DOCTOR -> SoftOrangeBg
        Role.ADMIN -> SoftGreenBg
    }

    val themeLabel = when (loginMode) {
        Role.PARENT -> activeParent?.name ?: "Bunda"
        Role.DOCTOR -> activeDoctor?.name ?: "Dokter"
        Role.ADMIN -> activeAdmin?.name ?: "Admin"
    }

    // Scroll state
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Animated Logo Card
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(themeColor)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(30.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = "CeriaCare Logo",
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        // Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "CeriaCare",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Slate900,
                letterSpacing = (-1).sp
            )
            Text(
                text = "PEDIATRIC CARE REDEFINED",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Account Selector Selector
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = when (loginMode) {
                    Role.PARENT -> "PILIH AKUN BUNDA/WALI"
                    Role.DOCTOR -> "PILIH AKUN DOKTER"
                    Role.ADMIN -> "PILIH AKUN ADMIN"
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                modifier = Modifier.padding(start = 4.dp)
            )

            // Selector Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Slate50)
                    .border(1.dp, themeColor, RoundedCornerShape(16.dp))
                    .clickable {
                        when (loginMode) {
                            Role.PARENT -> parentDropdownExpanded = true
                            Role.DOCTOR -> doctorDropdownExpanded = true
                            Role.ADMIN -> adminDropdownExpanded = true
                        }
                    }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = when (loginMode) {
                            Role.PARENT -> "${activeParent?.name} (${activeParent?.email})"
                            Role.DOCTOR -> "${activeDoctor?.name} (${activeDoctor?.specialty})"
                            Role.ADMIN -> "${activeAdmin?.name}"
                        },
                        color = Slate800,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        tint = Slate400
                    )
                }

                // Dropdown Menus
                DropdownMenu(
                    expanded = when (loginMode) {
                        Role.PARENT -> parentDropdownExpanded
                        Role.DOCTOR -> doctorDropdownExpanded
                        Role.ADMIN -> adminDropdownExpanded
                    },
                    onDismissRequest = {
                        parentDropdownExpanded = false
                        doctorDropdownExpanded = false
                        adminDropdownExpanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color.White)
                ) {
                    when (loginMode) {
                        Role.PARENT -> {
                            viewModel.parents.forEach { parent ->
                                DropdownMenuItem(
                                    text = { Text("${parent.name} (${parent.email})") },
                                    onClick = {
                                        viewModel.loginAsParent(parent.id)
                                        parentDropdownExpanded = false
                                    }
                                )
                            }
                        }
                        Role.DOCTOR -> {
                            viewModel.doctors.forEach { doctor ->
                                DropdownMenuItem(
                                    text = { Text("${doctor.name} (${doctor.specialty})") },
                                    onClick = {
                                        viewModel.loginAsDoctor(doctor.id)
                                        doctorDropdownExpanded = false
                                    }
                                )
                            }
                        }
                        Role.ADMIN -> {
                            viewModel.admins.forEach { admin ->
                                DropdownMenuItem(
                                    text = { Text(admin.name) },
                                    onClick = {
                                        viewModel.loginAsAdmin(admin.id)
                                        adminDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Login Button
        Button(
            onClick = {
                when (loginMode) {
                    Role.PARENT -> viewModel.loginAsParent(viewModel.currentParentId)
                    Role.DOCTOR -> viewModel.loginAsDoctor(viewModel.currentDoctorId)
                    Role.ADMIN -> viewModel.loginAsAdmin(viewModel.currentAdminId)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = themeColor),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "MASUK SEBAGAI $themeLabel".uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Arrow Right",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Role Toggles Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Toggle 1: Parent / Doctor
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(76.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (loginMode == Role.DOCTOR) SoftBlueBg else Slate50)
                    .border(
                        1.dp,
                        if (loginMode == Role.DOCTOR) BrandBlue else Slate200,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        loginMode = if (loginMode == Role.DOCTOR) Role.PARENT else Role.DOCTOR
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (loginMode == Role.DOCTOR) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Wali",
                            tint = BrandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Bunda / Wali",
                            color = BrandBlue,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Dokter",
                            tint = BrandOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Dokter Anak",
                            color = Slate600,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Toggle 2: Parent / Admin
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(76.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (loginMode == Role.ADMIN) SoftBlueBg else Slate50)
                    .border(
                        1.dp,
                        if (loginMode == Role.ADMIN) BrandBlue else Slate200,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        loginMode = if (loginMode == Role.ADMIN) Role.PARENT else Role.ADMIN
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (loginMode == Role.ADMIN) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Wali",
                            tint = BrandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Bunda / Wali",
                            color = BrandBlue,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Admin",
                            tint = BrandGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Admin Klinik",
                            color = Slate600,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        Text(
            text = "OFFICIAL CLINIC MANAGEMENT SYSTEM",
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            color = Slate200,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Register Link for Parent Mode
        if (loginMode == Role.PARENT) {
            Text(
                text = "REGISTRASI AKUN BARU",
                color = BrandBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .clickable { onGoToRegister() }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun RegisterView(
    onRegister: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(28.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Back Button
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .background(Slate100, RoundedCornerShape(50))
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Slate800,
                modifier = Modifier.size(18.dp)
            )
        }

        // Header Title
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Daftar Akun Baru",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Slate900
            )
            Text(
                text = "Buat akun CeriaCare untuk monitor kesehatan anak Anda.",
                fontSize = 12.sp,
                color = Slate400
            )
        }

        // Form Fields
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Full Name
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "NAMA LENGKAP ORTU/WALI",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    modifier = Modifier.padding(start = 4.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Contoh: Bunda Sarah", color = Slate400) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlue,
                        unfocusedBorderColor = Slate200,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Slate50
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Email
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "EMAIL",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    modifier = Modifier.padding(start = 4.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("sarah@example.com", color = Slate400) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlue,
                        unfocusedBorderColor = Slate200,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Slate50
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Password
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "PASSWORD",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    modifier = Modifier.padding(start = 4.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("••••••••", color = Slate400) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlue,
                        unfocusedBorderColor = Slate200,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Slate50
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Register Action
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onRegister(name, email)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "DAFTAR SEKARANG",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Register",
                        tint = Color.White
                    )
                }
            }

            Text(
                text = "Dengan mendaftar, Anda menyetujui Syarat & Ketentuan kami.",
                fontSize = 9.sp,
                color = Slate400,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
