package com.example.klinikanakbarulagi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klinikanakbarulagi.model.Role
import com.example.klinikanakbarulagi.ui.components.CeriaButton
import com.example.klinikanakbarulagi.ui.components.CeriaCard
import com.example.klinikanakbarulagi.ui.components.CeriaTextField
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
        AnimatedContent(
            targetState = isRegistering,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.9f) togetherWith
                fadeOut(animationSpec = tween(500)) + scaleOut(targetScale = 1.1f)
            },
            label = "LoginRegisterTransition"
        ) { state ->
            if (state) {
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
}

@Composable
fun LoginView(
    viewModel: ClinicViewModel,
    onGoToRegister: () -> Unit
) {
    var loginMode by remember { mutableStateOf(Role.PARENT) }

    var parentDropdownExpanded by remember { mutableStateOf(false) }
    var doctorDropdownExpanded by remember { mutableStateOf(false) }
    var adminDropdownExpanded by remember { mutableStateOf(false) }

    val activeParent = viewModel.parents.find { it.id == viewModel.currentParentId } ?: viewModel.parents.firstOrNull()
    val activeDoctor = viewModel.doctors.find { it.id == viewModel.currentDoctorId } ?: viewModel.doctors.firstOrNull()
    val activeAdmin = viewModel.admins.find { it.id == viewModel.currentAdminId } ?: viewModel.admins.firstOrNull()

    val themeColor = when (loginMode) {
        Role.PARENT -> BrandBlue
        Role.DOCTOR -> BrandOrange
        Role.ADMIN -> BrandGreen
    }

    val themeLabel = when (loginMode) {
        Role.PARENT -> activeParent?.name ?: "Bunda"
        Role.DOCTOR -> activeDoctor?.name ?: "Dokter"
        Role.ADMIN -> activeAdmin?.name ?: "Admin"
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Advanced Logo with Shadows and Gradients
        Box(
            modifier = Modifier
                .size(140.dp)
                .shadow(32.dp, RoundedCornerShape(48.dp), spotColor = themeColor)
                .clip(RoundedCornerShape(48.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(themeColor, themeColor.copy(alpha = 0.7f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VolunteerActivism,
                contentDescription = "CeriaCare Logo",
                tint = Color.White,
                modifier = Modifier.size(72.dp)
            )
        }

        // Mixed Typography Title
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "CeriaCare",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily.Cursive,
                    fontSize = 54.sp
                ),
                color = Slate900
            )
            Text(
                text = "PEDIATRIC CARE REDEFINED",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 4.sp
                ),
                color = Slate400
            )
        }

            CeriaCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 16
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                Text(
                    text = "IDENTITAS PENGGUNA",
                    style = MaterialTheme.typography.labelLarge,
                    color = Slate400
                )

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Slate50,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (loginMode) {
                                Role.PARENT -> parentDropdownExpanded = true
                                Role.DOCTOR -> doctorDropdownExpanded = true
                                Role.ADMIN -> adminDropdownExpanded = true
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (loginMode) {
                                    Role.PARENT -> activeParent?.name ?: ""
                                    Role.DOCTOR -> activeDoctor?.name ?: ""
                                    Role.ADMIN -> activeAdmin?.name ?: ""
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = Slate900,
                                maxLines = 1
                            )
                            Text(
                                text = when (loginMode) {
                                    Role.PARENT -> activeParent?.email ?: ""
                                    Role.DOCTOR -> activeDoctor?.specialty ?: ""
                                    Role.ADMIN -> "Akses Penuh Sistem"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate400
                            )
                        }
                        Icon(Icons.Default.ExpandCircleDown, contentDescription = null, tint = themeColor)
                    }

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
                            .fillMaxWidth(0.8f)
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
        }

        // Unified Role Selector (Object-based)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RoleIconToggle(
                isSelected = loginMode == Role.PARENT,
                icon = Icons.Default.ChildFriendly,
                color = BrandBlue,
                onClick = { loginMode = Role.PARENT },
                modifier = Modifier.weight(1f)
            )
            RoleIconToggle(
                isSelected = loginMode == Role.DOCTOR,
                icon = Icons.Default.HealthAndSafety,
                color = BrandOrange,
                onClick = { loginMode = Role.DOCTOR },
                modifier = Modifier.weight(1f)
            )
            RoleIconToggle(
                isSelected = loginMode == Role.ADMIN,
                icon = Icons.Default.AdminPanelSettings,
                color = BrandGreen,
                onClick = { loginMode = Role.ADMIN },
                modifier = Modifier.weight(1f)
            )
        }

        // Login Button
        CeriaButton(
            text = "MASUK SEBAGAI $themeLabel",
            onClick = {
                when (loginMode) {
                    Role.PARENT -> viewModel.loginAsParent(viewModel.currentParentId)
                    Role.DOCTOR -> viewModel.loginAsDoctor(viewModel.currentDoctorId)
                    Role.ADMIN -> viewModel.loginAsAdmin(viewModel.currentAdminId)
                }
            },
            containerColor = themeColor,
            icon = Icons.AutoMirrored.Filled.KeyboardArrowRight
        )

        if (loginMode == Role.PARENT) {
            TextButton(onClick = onGoToRegister) {
                Text(
                    text = "BELUM PUNYA AKUN? DAFTAR DI SINI",
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandBlue
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun RoleIconToggle(
    isSelected: Boolean,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(32.dp),
        color = if (isSelected) color else Slate50,
        modifier = modifier.height(84.dp).shadow(if (isSelected) 16.dp else 0.dp, RoundedCornerShape(32.dp), spotColor = color)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else color.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
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
            .padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .background(Slate50, CircleShape)
                .size(56.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Slate800)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Mari Bergabung",
                style = MaterialTheme.typography.headlineLarge,
                color = Slate900
            )
            Text(
                text = "Buat akun CeriaCare untuk monitor tumbuh kembang si kecil dengan mudah.",
                style = MaterialTheme.typography.bodyLarge,
                color = Slate400
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            CeriaTextField(
                label = "Nama Lengkap Bunda",
                value = name,
                onValueChange = { name = it },
                placeholder = "Bunda Sarah Wijaya",
                leadingIcon = Icons.Default.Person
            )

            CeriaTextField(
                label = "Alamat Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "sarah@ceria.com",
                leadingIcon = Icons.Default.AlternateEmail,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            CeriaTextField(
                label = "Kata Sandi",
                value = password,
                onValueChange = { password = it },
                placeholder = "Min. 8 Karakter",
                leadingIcon = Icons.Default.LockOpen,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CeriaButton(
                text = "BUAT AKUN SEKARANG",
                onClick = { if (name.isNotBlank() && email.isNotBlank()) onRegister(name, email) },
                containerColor = BrandBlue,
                icon = Icons.Default.AddCircleOutline
            )

            Text(
                text = "Dengan mendaftar, Anda menyetujui seluruh Syarat & Ketentuan CeriaCare Indonesia.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate400,
                textAlign = TextAlign.Center
            )
        }
    }
}
