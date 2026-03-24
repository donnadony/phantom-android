package com.phantom.ui.logs

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantom.core.PhantomLogger
import com.phantom.model.PhantomLogItem
import com.phantom.model.PhantomLogLevel
import com.phantom.theme.LocalPhantomColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PhantomLogsScreen(onBack: () -> Unit) {
    val colors = LocalPhantomColors.current
    val logs by PhantomLogger.logs.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val filteredLogs = remember(logs, searchText) {
        if (searchText.isBlank()) logs
        else logs.filter {
            it.message.contains(searchText, ignoreCase = true) ||
                    (it.tag?.contains(searchText, ignoreCase = true) == true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.accent,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )
                Text(text = "Logs", color = colors.textPrimary, fontSize = 20.sp)
            }
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Clear",
                tint = colors.textSecondary,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { scope.launch { PhantomLogger.clear() } }
            )
        }

        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            textStyle = TextStyle(color = colors.textPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(colors.accent),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.searchBackground)
                        .padding(12.dp)
                ) {
                    if (searchText.isEmpty()) {
                        Text("Search logs...", color = colors.textTertiary, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            }
        )

        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No logs", color = colors.textTertiary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredLogs, key = { it.id }) { logItem ->
                    LogItemRow(logItem)
                }
            }
        }
    }
}

@Composable
private fun LogItemRow(item: PhantomLogItem) {
    val colors = LocalPhantomColors.current
    val levelColor = when (item.level) {
        PhantomLogLevel.DEBUG -> colors.debug
        PhantomLogLevel.INFO -> colors.info
        PhantomLogLevel.WARNING -> colors.warning
        PhantomLogLevel.ERROR -> colors.error
        PhantomLogLevel.CRITICAL -> colors.critical
    }

    val timeFormat = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.US) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = item.level.emoji, fontSize = 12.sp)
                Text(text = item.level.label, color = levelColor, fontSize = 12.sp)
                item.tag?.let {
                    Text(text = "[$it]", color = colors.textTertiary, fontSize = 12.sp)
                }
            }
            Text(
                text = timeFormat.format(Date(item.timestamp)),
                color = colors.textTertiary,
                fontSize = 11.sp
            )
        }
        Text(
            text = item.message,
            color = colors.textPrimary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
