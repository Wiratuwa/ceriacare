package com.example.klinikanakbarulagi.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klinikanakbarulagi.ui.theme.*

@Composable
fun CeriaCard(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    borderColor: Color = Slate100,
    elevation: Int = 8,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.shadow(elevation.dp, RoundedCornerShape(32.dp), ambientColor = Slate400, spotColor = Slate200),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)),
        content = content
    )
}

@Composable
fun CeriaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = BrandBlue,
    contentColor: Color = Color.White,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(containerColor, containerColor.copy(alpha = 0.8f))
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
            .shadow(12.dp, CircleShape, spotColor = containerColor),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, // Using Box for gradient
            contentColor = contentColor,
            disabledContainerColor = Slate200,
            disabledContentColor = Slate400
        ),
        enabled = enabled,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (enabled) gradient else Brush.linearGradient(listOf(Slate200, Slate200))),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun CeriaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    singleLine: Boolean = true
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = Slate600,
            modifier = Modifier.padding(start = 16.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = Slate400) },
            leadingIcon = leadingIcon?.let { { Icon(imageVector = it, contentDescription = null, tint = Slate400) } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandBlue,
                unfocusedBorderColor = Slate100,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Slate50
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CeriaTopBar(
    title: String,
    subtitle: String,
    containerColor: Color,
    icon: ImageVector? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    // We use a Surface here with 0 elevation to act as a solid background for the rounded corners
    Surface(
        color = Slate50, // Matches exactly with dashboard background
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = containerColor,
            shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp),
            shadowElevation = 0.dp, // Removing shadow to prevent edge artifacts
            modifier = Modifier.fillMaxWidth()
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    if (icon != null) {
                        Box(
                            modifier = Modifier
                                .padding(start = 20.dp)
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    }
}

@Composable
fun CeriaBadge(
    text: String,
    containerColor: Color,
    contentColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = CircleShape,
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun AnimatedTabContent(
    targetState: String,
    content: @Composable (String) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            fadeIn(animationSpec = tween(400)) + slideInHorizontally(animationSpec = tween(400), initialOffsetX = { 100 }) togetherWith
                    fadeOut(animationSpec = tween(400)) + slideOutHorizontally(animationSpec = tween(400), targetOffsetX = { -100 })
        },
        label = "TabTransition"
    ) { state ->
        content(state)
    }
}
