package com.phantom.ui.network

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantom.core.PhantomNetworkLogger
import com.phantom.model.PhantomNetworkItem
import com.phantom.theme.LocalPhantomColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PhantomNetworkScreen(onBack: () -> Unit) {
    val colors = LocalPhantomColors.current
    val requests by PhantomNetworkLogger.requests.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val expandedItems = remember { mutableStateMapOf<String, Boolean>() }
    val scope = rememberCoroutineScope()

    val filteredRequests = remember(requests, searchText) {
        if (searchText.isBlank()) requests
        else requests.filter { it.url.contains(searchText, ignoreCase = true) }
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
                Text(text = "Network", color = colors.textPrimary, fontSize = 20.sp)
            }
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Clear",
                tint = colors.textSecondary,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { scope.launch { PhantomNetworkLogger.clear() } }
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
                        Text("Search requests...", color = colors.textTertiary, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            }
        )

        if (filteredRequests.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No requests", color = colors.textTertiary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredRequests, key = { it.id }) { item ->
                    NetworkItemRow(
                        item = item,
                        isExpanded = expandedItems[item.id] == true,
                        onToggle = { expandedItems[item.id] = !(expandedItems[item.id] ?: false) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NetworkItemRow(
    item: PhantomNetworkItem,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val colors = LocalPhantomColors.current
    val clipboardManager = LocalClipboardManager.current
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.US) }

    val statusColor = when {
        item.statusCode == null -> colors.statusPending
        item.statusCode in 200..299 -> colors.statusSuccess
        item.statusCode in 400..499 -> colors.statusClientError
        else -> colors.statusServerError
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface)
            .clickable { onToggle() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = item.method, color = colors.accent, fontSize = 12.sp)
                Text(
                    text = item.statusCode?.toString() ?: "...",
                    color = statusColor,
                    fontSize = 12.sp
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item.duration?.let {
                    Text(text = "${it}ms", color = colors.textTertiary, fontSize = 11.sp)
                }
                Text(
                    text = timeFormat.format(Date(item.timestamp)),
                    color = colors.textTertiary,
                    fontSize = 11.sp
                )
            }
        }
        Text(
            text = item.url,
            color = colors.textPrimary,
            fontSize = 12.sp,
            maxLines = 2,
            modifier = Modifier.padding(top = 4.dp)
        )

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.requestHeaders.isNotEmpty()) {
                    SectionHeader("Request Headers")
                    item.requestHeaders.forEach { (key, value) ->
                        Text("$key: $value", color = colors.textSecondary, fontSize = 11.sp)
                    }
                }

                item.requestBody?.let { body ->
                    SectionHeader("Request Body")
                    PhantomJsonTreeView(body)
                }

                if (item.responseHeaders.isNotEmpty()) {
                    SectionHeader("Response Headers")
                    item.responseHeaders.forEach { (key, value) ->
                        Text("$key: $value", color = colors.textSecondary, fontSize = 11.sp)
                    }
                }

                item.responseBody?.let { body ->
                    SectionHeader("Response Body")
                    PhantomJsonTreeView(body)
                }

                item.curlCommand?.let { curl ->
                    Text(
                        text = "Copy cURL",
                        color = colors.accent,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surfaceSecondary)
                            .clickable { clipboardManager.setText(AnnotatedString(curl)) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    val colors = LocalPhantomColors.current
    Text(
        text = title,
        color = colors.textTertiary,
        fontSize = 11.sp,
        modifier = Modifier.padding(top = 4.dp)
    )
}
