package com.phantom.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Phantom",
                color = colors.textPrimary,
                fontSize = 24.sp
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = colors.textSecondary,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onClose() }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuItem(
                icon = Icons.AutoMirrored.Filled.List,
                title = "Logs",
                subtitle = "Application logs",
                onClick = { onNavigate("logs") }
            )
            MenuItem(
                icon = Icons.Default.Share,
                title = "Network",
                subtitle = "HTTP request inspector",
                onClick = { onNavigate("network") }
            )
            MenuItem(
                icon = Icons.Default.Build,
                title = "Mock Services",
                subtitle = "API mock responses",
                onClick = { onNavigate("mock") }
            )
            MenuItem(
                icon = Icons.Default.Settings,
                title = "Configuration",
                subtitle = "Feature flags & settings",
                onClick = { onNavigate("config") }
            )
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = LocalPhantomColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surfaceSecondary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(20.dp)
            )
        }
        Column {
            Text(text = title, color = colors.textPrimary, fontSize = 16.sp)
            Text(text = subtitle, color = colors.textSecondary, fontSize = 13.sp)
        }
    }
}
