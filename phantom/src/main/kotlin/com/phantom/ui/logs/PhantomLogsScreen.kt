package com.phantom.ui.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
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

private enum class LogFilter(val label: String, val levels: Set<PhantomLogLevel>?) {
    ALL("All", null),
    INFO("INFO", setOf(PhantomLogLevel.INFO)),
    WARN("WARN", setOf(PhantomLogLevel.WARNING)),
    ERROR("ERROR", setOf(PhantomLogLevel.ERROR, PhantomLogLevel.CRITICAL));
}

@Composable
fun PhantomLogsScreen(onBack: () -> Unit) {
    val colors = LocalPhantomColors.current
    val logs by PhantomLogger.logs.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf(LogFilter.ALL) }
    val scope = rememberCoroutineScope()

    val filteredLogs = remember(logs, searchText, activeFilter) {
        logs.filter { item ->
            val matchesSearch = searchText.isBlank() ||
                    item.message.contains(searchText, ignoreCase = true) ||
                    (item.tag?.contains(searchText, ignoreCase = true) == true)
            val matchesFilter = activeFilter.levels == null || item.level in activeFilter.levels!!
            matchesSearch && matchesFilter
        }
    }

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
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Logs (${filteredLogs.size})",
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "Clear",
                color = colors.error,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceSecondary)
                    .clickable { scope.launch { PhantomLogger.clear() } }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        HorizontalDivider(color = colors.border, thickness = 0.5.dp)

        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            textStyle = TextStyle(color = colors.textPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(colors.accent),
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.surfaceSecondary)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = colors.textTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchText.isEmpty()) {
                            Text(
                                "Search by message or tag...",
                                color = colors.textTertiary,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(LogFilter.entries.toList()) { filter ->
                val isActive = activeFilter == filter
                Text(
                    text = filter.label,
                    color = if (isActive) colors.accent else colors.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .then(
                            if (isActive) Modifier.border(
                                1.dp,
                                colors.accent,
                                RoundedCornerShape(14.dp)
                            )
                            else Modifier.border(
                                1.dp,
                                colors.border,
                                RoundedCornerShape(14.dp)
                            )
                        )
                        .clickable { activeFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

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
                    .padding(top = 4.dp),
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

    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(0.5.dp, colors.border, RoundedCornerShape(10.dp))
            .background(colors.surface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(levelColor)
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.level.label,
                    color = levelColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .border(0.5.dp, levelColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                item.tag?.let {
                    Text(
                        text = it,
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Text(
                text = item.message,
                color = colors.textSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = timeFormat.format(Date(item.timestamp)),
                color = colors.textTertiary,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
