package com.phantom.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantom.Phantom
import com.phantom.model.PhantomConfigType
import com.phantom.model.PhantomLogLevel

class PhantomDemoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Phantom.init(this)
        seedDemoData()

        setContent {
            DemoScreen()
        }
    }

    private fun seedDemoData() {
        seedConfigs()
        seedLogs()
        seedNetworkRequests()
    }

    private fun seedConfigs() {
        Phantom.registerConfig(
            label = "API Base URL",
            key = "api_base_url",
            defaultValue = "https://api.example.com",
            type = PhantomConfigType.TEXT
        )
        Phantom.registerConfig(
            label = "Environment",
            key = "environment",
            defaultValue = "development",
            type = PhantomConfigType.PICKER,
            options = listOf("development", "staging", "production")
        )
        Phantom.registerConfig(
            label = "Dark Mode",
            key = "dark_mode",
            defaultValue = "true",
            type = PhantomConfigType.TOGGLE
        )
    }

    private fun seedLogs() {
        Phantom.log(PhantomLogLevel.INFO, "Application started successfully", "App")
        Phantom.log(PhantomLogLevel.DEBUG, "Loading user preferences from cache", "Cache")
        Phantom.log(PhantomLogLevel.WARNING, "API response took 2.3s - exceeds threshold", "Network")
        Phantom.log(PhantomLogLevel.ERROR, "Failed to parse JSON: unexpected token at position 42", "Parser")
        Phantom.log(PhantomLogLevel.CRITICAL, "Database connection pool exhausted", "Database")
    }

    private fun seedNetworkRequests() {
        Phantom.logRequest(
            url = "https://api.example.com/users/me",
            method = "GET",
            headers = mapOf(
                "Authorization" to "Bearer eyJhbGciOiJIUzI1NiIs...",
                "Accept" to "application/json"
            )
        )
        Phantom.logResponse(
            url = "https://api.example.com/users/me",
            method = "GET",
            headers = mapOf("Content-Type" to "application/json"),
            body = """{"id": 1, "name": "John Doe", "email": "john@example.com", "roles": ["admin", "user"]}""",
            statusCode = 200
        )

        Phantom.logRequest(
            url = "https://api.example.com/posts",
            method = "POST",
            headers = mapOf(
                "Authorization" to "Bearer eyJhbGciOiJIUzI1NiIs...",
                "Content-Type" to "application/json"
            ),
            body = """{"title": "Hello World", "content": "This is a test post", "tags": ["demo", "test"]}"""
        )
        Phantom.logResponse(
            url = "https://api.example.com/posts",
            method = "POST",
            headers = mapOf("Content-Type" to "application/json"),
            body = """{"id": 42, "title": "Hello World", "created_at": "2024-01-15T10:30:00Z"}""",
            statusCode = 201
        )

        Phantom.logRequest(
            url = "https://api.example.com/admin/settings",
            method = "GET",
            headers = mapOf("Authorization" to "Bearer expired_token")
        )
        Phantom.logResponse(
            url = "https://api.example.com/admin/settings",
            method = "GET",
            headers = mapOf("Content-Type" to "application/json"),
            body = """{"error": "Unauthorized", "message": "Token has expired"}""",
            statusCode = 401
        )
    }
}

private val DarkBackground = Color(0xFF0D1117)
private val DarkSurface = Color(0xFF161B22)
private val TextPrimary = Color(0xFFF0F6FC)
private val TextSecondary = Color(0xFF8B949E)
private val Accent = Color(0xFF58A6FF)
private val AccentDark = Color(0xFF1F6FEB)
private val Green = Color(0xFF3FB950)
private val Yellow = Color(0xFFD29922)
private val Red = Color(0xFFF85149)

@Composable
private fun DemoScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Text(
            text = "Phantom",
            color = TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Debug Toolkit for Android",
            color = TextSecondary,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { Phantom.show(context) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Open Debug Panel",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Seeded Data",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        FeatureCard(
            icon = Icons.AutoMirrored.Filled.List,
            title = "5 Log Entries",
            subtitle = "DEBUG, INFO, WARNING, ERROR, CRITICAL",
            badgeColor = Green
        )
        FeatureCard(
            icon = Icons.Default.Share,
            title = "3 Network Requests",
            subtitle = "GET 200, POST 201, GET 401",
            badgeColor = Accent
        )
        FeatureCard(
            icon = Icons.Default.Build,
            title = "Mock Services",
            subtitle = "Create mock rules in the debug panel",
            badgeColor = Yellow
        )
        FeatureCard(
            icon = Icons.Default.Settings,
            title = "3 Config Entries",
            subtitle = "Text, Picker, Toggle",
            badgeColor = Red
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Add More Data",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                text = "Add Log",
                modifier = Modifier.weight(1f),
                onClick = {
                    val levels = PhantomLogLevel.entries
                    val level = levels.random()
                    Phantom.log(level, "Dynamic log at ${System.currentTimeMillis()}", "Demo")
                }
            )
            ActionButton(
                text = "Add Request",
                modifier = Modifier.weight(1f),
                onClick = {
                    val id = (1..100).random()
                    Phantom.logRequest(
                        url = "https://api.example.com/items/$id",
                        method = "GET",
                        headers = mapOf("Accept" to "application/json")
                    )
                    Phantom.logResponse(
                        url = "https://api.example.com/items/$id",
                        method = "GET",
                        headers = mapOf("Content-Type" to "application/json"),
                        body = """{"id": $id, "name": "Item $id", "price": ${(10..999).random()}.99}""",
                        statusCode = listOf(200, 201, 400, 404, 500).random()
                    )
                }
            )
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = badgeColor,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(text = title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = TextSecondary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent)
    ) {
        Text(text = text, fontSize = 14.sp)
    }
}
