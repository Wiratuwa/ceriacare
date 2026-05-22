package com.example.klinikanakbarulagi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.klinikanakbarulagi.model.Role
import com.example.klinikanakbarulagi.ui.screens.AdminDashboard
import com.example.klinikanakbarulagi.ui.screens.DoctorDashboard
import com.example.klinikanakbarulagi.ui.screens.LoginScreen
import com.example.klinikanakbarulagi.ui.screens.ParentDashboard
import com.example.klinikanakbarulagi.ui.theme.KlinikanakbarulagiTheme
import com.example.klinikanakbarulagi.viewmodel.ClinicViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlinikanakbarulagiTheme {
                val viewModel = remember { ClinicViewModel() }
                AppScreen(viewModel)
            }
        }
    }
}

@Composable
fun AppScreen(viewModel: ClinicViewModel) {
    AnimatedContent(
        targetState = viewModel.role,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "RoleTransition"
    ) { role ->
        when (role) {
            Role.PARENT -> ParentDashboard(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Role.DOCTOR -> DoctorDashboard(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Role.ADMIN -> AdminDashboard(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            null -> LoginScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
        }
    }
}