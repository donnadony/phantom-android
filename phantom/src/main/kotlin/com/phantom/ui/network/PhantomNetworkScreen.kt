package com.phantom.ui.network

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantom.core.PhantomNetworkLogger
import com.phantom.model.PhantomNetworkItem
import com.phantom.theme.LocalPhantomColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class NetworkFilter(val label: String) {
    ALL("All"),
    ERRORS("Errors"),
    SLOW("Slow >1s");
}

private enum class DetailTab(val label: String) {
    REQUEST("Request"),
    RESPONSE("Response"),
    HEADERS("Headers");
}

@Composable
fun PhantomNetworkScreen(onBack: () -> Unit) {
    val colors = LocalPhantomColors.current
    val requests by PhantomNetworkLogger.requests.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf(NetworkFilter.ALL) }
    var selectedItemId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val filteredRequests = remember(requests, searchText, activeFilter) {
        requests.filter { item ->
            val matchesSearch = searchText.isBlank() ||
                    item.url.contains(searchText, ignoreCase = true) ||
                    item.responseBody?.contains(searchText, ignoreCase = true) == true ||
                    item.requestHeaders.any { (k, v) ->
                        k.contains(searchText, ignoreCase = true) || v.contains(searchText, ignoreCase = true)
                    }
            val matchesFilter = when (activeFilter) {
                NetworkFilter.ALL -> true
                NetworkFilter.ERRORS -> item.isError
                NetworkFilter.SLOW -> item.isSlow
            }
            matchesSearch && matchesFilter
        }
    }

    val selectedItem = selectedItemId?.let { id -> requests.firstOrNull { it.id == id } }

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
                    .clickable {
                        if (selectedItem != null) selectedItemId = null
                        else onBack()
                    },
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
                text = "Network (${filteredRequests.size})",
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
                    .clickable { scope.launch { PhantomNetworkLogger.clear() } }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        HorizontalDivider(color = colors.border, thickness = 0.5.dp)

        if (selectedItem != null) {
            NetworkDetailView(item = selectedItem)
        } else {
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
                                    "Filter by endpoint, body or headers",
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
                items(NetworkFilter.entries.toList()) { filter ->
                    val isActive = activeFilter == filter
                    Text(
                        text = filter.label,
                        color = if (isActive) colors.accent else colors.textSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                1.dp,
                                if (isActive) colors.accent else colors.border,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { activeFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

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
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredRequests, key = { it.id }) { item ->
                        NetworkItemRow(
                            item = item,
                            onClick = { selectedItemId = item.id }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NetworkItemRow(
    item: PhantomNetworkItem,
    onClick: () -> Unit
) {
    val colors = LocalPhantomColors.current
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }

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
            .clip(RoundedCornerShape(10.dp))
            .border(0.5.dp, colors.border, RoundedCornerShape(10.dp))
            .background(colors.surface)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Text(
                text = item.method,
                color = colors.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            item.statusCode?.let { code ->
                Text(
                    text = code.toString(),
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .border(0.5.dp, statusColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
            if (item.isMocked) {
                Text(
                    text = "MOCK",
                    color = colors.warning,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .border(0.5.dp, colors.warning, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }

        Text(
            text = item.url,
            color = colors.textSecondary,
            fontSize = 12.sp,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            modifier = Modifier.padding(top = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = timeFormat.format(Date(item.timestamp)),
                color = colors.textTertiary,
                fontSize = 11.sp
            )
            Text(
                text = "${item.duration ?: 0}ms",
                color = colors.textTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.responseSize,
                color = colors.textTertiary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun NetworkDetailView(item: PhantomNetworkItem) {
    val colors = LocalPhantomColors.current
    var activeTab by remember { mutableStateOf(DetailTab.RESPONSE) }
    val clipboardManager = LocalClipboardManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surfaceSecondary)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DetailTab.entries.forEach { tab ->
                val isActive = activeTab == tab
                Text(
                    text = tab.label,
                    color = if (isActive) colors.textPrimary else colors.textSecondary,
                    fontSize = 14.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (isActive) Modifier.background(colors.surface) else Modifier
                        )
                        .clickable { activeTab = tab }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = item.url,
                color = colors.textSecondary,
                fontSize = 12.sp
            )

            when (activeTab) {
                DetailTab.REQUEST -> {
                    item.requestBody?.let { body ->
                        SectionHeader("Request Body")
                        PhantomJsonTreeView(body)
                    } ?: Text("No request body", color = colors.textTertiary, fontSize = 13.sp)

                    item.curlCommand?.let { curl ->
                        Text(
                            text = "Copy cURL",
                            color = colors.accent,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.surfaceSecondary)
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(curl))
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                DetailTab.RESPONSE -> {
                    item.responseBody?.let { body ->
                        SectionHeader("Response Body")
                        PhantomJsonTreeView(body)
                    } ?: Text("No response body", color = colors.textTertiary, fontSize = 13.sp)

                    if (item.isMocked) {
                        Text(
                            text = "Edit Mock",
                            color = colors.warning,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .align(Alignment.End)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, colors.warning, RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                DetailTab.HEADERS -> {
                    if (item.requestHeaders.isNotEmpty()) {
                        SectionHeader("Request Headers")
                        item.requestHeaders.forEach { (key, value) ->
                            HeaderRow(key, value)
                        }
                    }
                    if (item.responseHeaders.isNotEmpty()) {
                        SectionHeader("Response Headers")
                        item.responseHeaders.forEach { (key, value) ->
                            HeaderRow(key, value)
                        }
                    }
                    if (item.requestHeaders.isEmpty() && item.responseHeaders.isEmpty()) {
                        Text("No headers", color = colors.textTertiary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderRow(key: String, value: String) {
    val colors = LocalPhantomColors.current
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("$key: ", color = colors.accent, fontSize = 12.sp)
        Text(value, color = colors.textSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun SectionHeader(title: String) {
    val colors = LocalPhantomColors.current
    Text(
        text = title,
        color = colors.textTertiary,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 4.dp)
    )
}
