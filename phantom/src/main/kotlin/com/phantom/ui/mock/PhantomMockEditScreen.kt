package com.phantom.ui.mock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantom.core.PhantomMockInterceptor
import com.phantom.model.PhantomMockResponse
import com.phantom.model.PhantomMockRule
import com.phantom.theme.LocalPhantomColors

@Composable
fun PhantomMockEditScreen(ruleId: String, onBack: () -> Unit) {
    val colors = LocalPhantomColors.current
    val rules by PhantomMockInterceptor.rules.collectAsState()
    val existingRule = rules.firstOrNull { it.id == ruleId }
    val isNew = ruleId == "new"

    var url by remember { mutableStateOf(existingRule?.url ?: "") }
    var method by remember { mutableStateOf(existingRule?.method ?: "GET") }
    var responses by remember {
        mutableStateOf(existingRule?.responses ?: listOf(PhantomMockResponse()))
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
        focusedBorderColor = colors.accent,
        unfocusedBorderColor = colors.border,
        cursorColor = colors.accent,
        focusedLabelColor = colors.accent,
        unfocusedLabelColor = colors.textTertiary
    )

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
                Text(
                    text = if (isNew) "New Mock Rule" else "Edit Mock Rule",
                    color = colors.textPrimary,
                    fontSize = 20.sp
                )
            }
            Text(
                text = "Save",
                color = colors.accent,
                fontSize = 16.sp,
                modifier = Modifier.clickable {
                    if (url.isNotBlank()) {
                        val rule = PhantomMockRule(
                            id = if (isNew) java.util.UUID.randomUUID().toString() else ruleId,
                            url = url,
                            method = method,
                            responses = responses
                        )
                        if (isNew) PhantomMockInterceptor.addRule(rule)
                        else PhantomMockInterceptor.updateRule(rule)
                        onBack()
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL Pattern") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                singleLine = true
            )

            OutlinedTextField(
                value = method,
                onValueChange = { method = it.uppercase() },
                label = { Text("HTTP Method") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Responses", color = colors.textPrimary, fontSize = 16.sp)
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Response",
                    tint = colors.accent,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            responses = responses + PhantomMockResponse()
                        }
                )
            }

            responses.forEachIndexed { index, response ->
                ResponseEditor(
                    index = index,
                    response = response,
                    fieldColors = fieldColors,
                    onUpdate = { updated ->
                        responses = responses.toMutableList().apply { set(index, updated) }
                    },
                    onDelete = if (responses.size > 1) {
                        { responses = responses.toMutableList().apply { removeAt(index) } }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun ResponseEditor(
    index: Int,
    response: PhantomMockResponse,
    fieldColors: androidx.compose.material3.TextFieldColors,
    onUpdate: (PhantomMockResponse) -> Unit,
    onDelete: (() -> Unit)?
) {
    val colors = LocalPhantomColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Response #${index + 1}", color = colors.textSecondary, fontSize = 13.sp)
            onDelete?.let {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = colors.error,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { it() }
                )
            }
        }

        OutlinedTextField(
            value = response.statusCode.toString(),
            onValueChange = { input ->
                input.toIntOrNull()?.let { code ->
                    onUpdate(response.copy(statusCode = code))
                }
            },
            label = { Text("Status Code") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors,
            singleLine = true
        )

        OutlinedTextField(
            value = response.body,
            onValueChange = { onUpdate(response.copy(body = it)) },
            label = { Text("Response Body (JSON)") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors,
            minLines = 3,
            maxLines = 10
        )
    }
}
