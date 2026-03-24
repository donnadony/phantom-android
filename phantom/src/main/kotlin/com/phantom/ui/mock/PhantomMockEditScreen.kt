package com.phantom.ui.mock

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantom.core.PhantomMockInterceptor
import com.phantom.model.PhantomMockResponse
import com.phantom.model.PhantomMockRule
import com.phantom.theme.LocalPhantomColors
import com.phantom.util.PhantomJson

private val HTTP_METHODS = listOf("ANY", "GET", "POST", "PUT", "DELETE")

@Composable
fun PhantomMockEditScreen(ruleId: String, onBack: () -> Unit) {
    val colors = LocalPhantomColors.current
    val rules by PhantomMockInterceptor.rules.collectAsState()
    val existingRule = rules.firstOrNull { it.id == ruleId }
    val isNew = ruleId == "new"

    var description by remember { mutableStateOf(existingRule?.description ?: "") }
    var url by remember { mutableStateOf(existingRule?.url ?: "") }
    var method by remember { mutableStateOf(existingRule?.method ?: "GET") }
    var responses by remember {
        mutableStateOf(existingRule?.responses ?: listOf(PhantomMockResponse()))
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
            Text(
                text = "Cancel",
                color = colors.textSecondary,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceSecondary)
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = if (isNew) "New Mock Rule" else "Edit Mock Rule",
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "Save",
                color = colors.accent,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceSecondary)
                    .clickable {
                        if (url.isNotBlank()) {
                            val rule = PhantomMockRule(
                                id = if (isNew) java.util.UUID.randomUUID()
                                    .toString() else ruleId,
                                description = description,
                                url = url,
                                method = method,
                                responses = responses
                            )
                            if (isNew) PhantomMockInterceptor.addRule(rule)
                            else PhantomMockInterceptor.updateRule(rule)
                            onBack()
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        HorizontalDivider(color = colors.border, thickness = 0.5.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            FormSection("Description") {
                FormTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Mock rule description"
                )
            }

            FormSection("URL Pattern (partial match)") {
                FormTextField(
                    value = url,
                    onValueChange = { url = it },
                    placeholder = "/api/endpoint"
                )
            }

            FormSection("HTTP Method") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HTTP_METHODS.forEach { m ->
                        val isSelected = method.equals(m, ignoreCase = true)
                        Text(
                            text = m,
                            color = if (isSelected) colors.accent else colors.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .then(
                                    if (isSelected) Modifier.border(
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
                                .clickable { method = m }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            responses.forEachIndexed { index, response ->
                ResponseSection(
                    index = index,
                    response = response,
                    onUpdate = { updated ->
                        responses = responses.toMutableList().apply { set(index, updated) }
                    },
                    onDelete = if (responses.size > 1) {
                        { responses = responses.toMutableList().apply { removeAt(index) } }
                    } else null
                )
            }

            Text(
                text = "+ Add another response",
                color = colors.accent,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    responses = responses + PhantomMockResponse()
                }
            )

            if (!isNew) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Delete Rule",
                    color = colors.error,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.error.copy(alpha = 0.12f))
                        .clickable {
                            PhantomMockInterceptor.deleteRule(ruleId)
                            onBack()
                        }
                        .padding(vertical = 14.dp)
                        .then(Modifier),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FormSection(title: String, content: @Composable () -> Unit) {
    val colors = LocalPhantomColors.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    val colors = LocalPhantomColors.current

    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = colors.textPrimary,
            fontSize = 14.sp
        ),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(colors.accent),
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(0.5.dp, colors.border, RoundedCornerShape(10.dp))
                    .background(colors.surfaceSecondary)
                    .padding(14.dp)
            ) {
                if (value.isEmpty()) {
                    Text(placeholder, color = colors.textTertiary, fontSize = 14.sp)
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun ResponseSection(
    index: Int,
    response: PhantomMockResponse,
    onUpdate: (PhantomMockResponse) -> Unit,
    onDelete: (() -> Unit)?
) {
    val colors = LocalPhantomColors.current
    val clipboardManager = LocalClipboardManager.current

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FormSection("Status Code") {
            FormTextField(
                value = response.statusCode.toString(),
                onValueChange = { input ->
                    input.toIntOrNull()?.let { code ->
                        onUpdate(response.copy(statusCode = code))
                    }
                },
                placeholder = "200"
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Response Body (JSON)",
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Paste",
                        color = colors.accent,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            clipboardManager.getText()?.text?.let { pasted ->
                                onUpdate(response.copy(body = pasted))
                            }
                        }
                    )
                    Text(
                        text = "Format",
                        color = colors.accent,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            val formatted = PhantomJson.prettyPrint(response.body)
                            onUpdate(response.copy(body = formatted))
                        }
                    )
                }
            }

            FormTextField(
                value = response.body,
                onValueChange = { onUpdate(response.copy(body = it)) },
                placeholder = "{}",
                singleLine = false,
                minLines = 6,
                maxLines = 20
            )
        }
    }
}
