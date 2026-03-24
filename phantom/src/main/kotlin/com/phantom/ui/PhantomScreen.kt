package com.phantom.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.phantom.theme.LocalPhantomColors
import com.phantom.ui.config.PhantomConfigScreen
import com.phantom.ui.logs.PhantomLogsScreen
import com.phantom.ui.mock.PhantomMockEditScreen
import com.phantom.ui.mock.PhantomMockListScreen
import com.phantom.ui.network.PhantomNetworkScreen

@Composable
fun PhantomScreen(onClose: () -> Unit) {
    val navController = rememberNavController()
    val colors = LocalPhantomColors.current

    NavHost(
        navController = navController,
        startDestination = "menu",
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        composable("menu") {
            PhantomMenuScreen(
                onNavigate = { navController.navigate(it) },
                onClose = onClose
            )
        }
        composable("logs") {
            PhantomLogsScreen(onBack = { navController.popBackStack() })
        }
        composable("network") {
            PhantomNetworkScreen(onBack = { navController.popBackStack() })
        }
        composable("mock") {
            PhantomMockListScreen(
                onBack = { navController.popBackStack() },
                onEditRule = { ruleId -> navController.navigate("mock_edit/$ruleId") },
                onNewRule = { navController.navigate("mock_edit/new") }
            )
        }
        composable("mock_edit/{ruleId}") { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getString("ruleId") ?: "new"
            PhantomMockEditScreen(
                ruleId = ruleId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("config") {
            PhantomConfigScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun PhantomMenuScreen(onNavigate: (String) -> Unit, onClose: () -> Unit) {
    val colors = LocalPhantomColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceSecondary)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Phantom",
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        HorizontalDivider(color = colors.border, thickness = 0.5.dp)

        MenuItem(
            icon = Icons.AutoMirrored.Filled.List,
            title = "Logs",
            onClick = { onNavigate("logs") }
        )
        HorizontalDivider(
            color = colors.border,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 56.dp)
        )
        MenuItem(
            icon = Icons.Default.Share,
            title = "Network",
            onClick = { onNavigate("network") }
        )
        HorizontalDivider(
            color = colors.border,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 56.dp)
        )
        MenuItem(
            icon = Icons.Default.Share,
            title = "Mock Services",
            onClick = { onNavigate("mock") }
        )
        HorizontalDivider(
            color = colors.border,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 56.dp)
        )
        MenuItem(
            icon = Icons.Default.Settings,
            title = "Configuration",
            onClick = { onNavigate("config") }
        )
        HorizontalDivider(color = colors.border, thickness = 0.5.dp)
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    val colors = LocalPhantomColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            color = colors.textPrimary,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colors.textTertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}
