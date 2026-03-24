package com.phantom.ui.config

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantom.core.PhantomConfig
import com.phantom.model.PhantomConfigEntry
import com.phantom.model.PhantomConfigType
import com.phantom.theme.LocalPhantomColors

@Composable
fun PhantomConfigScreen(onBack: () -> Unit) {
    val colors = LocalPhantomColors.current
    val entries by PhantomConfig.entries.collectAsState()

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
                text = "Configuration",
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        HorizontalDivider(color = colors.border, thickness = 0.5.dp)

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No configs registered", color = colors.textTertiary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(entries, key = { it.key }) { entry ->
                    ConfigEntryRow(entry)
                }
            }
        }
    }
}

@Composable
private fun ConfigEntryRow(entry: PhantomConfigEntry) {
    val colors = LocalPhantomColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(0.5.dp, colors.border, RoundedCornerShape(10.dp))
            .background(colors.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.label,
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (entry.isModified) {
                    Text(
                        text = "Modified",
                        color = colors.configModified,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .border(0.5.dp, colors.configModified, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            if (entry.isModified) {
                Text(
                    text = "Reset",
                    color = colors.configModified,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { PhantomConfig.resetValue(entry.key) }
                )
            }
        }

        when (entry.type) {
            PhantomConfigType.TEXT -> TextConfigEditor(entry)
            PhantomConfigType.TOGGLE -> ToggleConfigEditor(entry)
            PhantomConfigType.PICKER -> PickerConfigEditor(entry)
        }
    }
}

@Composable
private fun TextConfigEditor(entry: PhantomConfigEntry) {
    val colors = LocalPhantomColors.current

    OutlinedTextField(
        value = entry.resolvedValue,
        onValueChange = { PhantomConfig.setValue(entry.key, it) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedBorderColor = colors.accent,
            unfocusedBorderColor = colors.border,
            cursorColor = colors.accent
        ),
        singleLine = true
    )
}

@Composable
private fun ToggleConfigEditor(entry: PhantomConfigEntry) {
    val colors = LocalPhantomColors.current
    val isOn = entry.resolvedValue.toBooleanStrictOrNull() ?: false

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isOn) "Enabled" else "Disabled",
            color = colors.textSecondary,
            fontSize = 13.sp
        )
        Switch(
            checked = isOn,
            onCheckedChange = { PhantomConfig.setValue(entry.key, it.toString()) },
            colors = SwitchDefaults.colors(
                checkedTrackColor = colors.accent,
                uncheckedTrackColor = colors.border
            )
        )
    }
}

@Composable
private fun PickerConfigEditor(entry: PhantomConfigEntry) {
    val colors = LocalPhantomColors.current
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(
            text = entry.resolvedValue,
            color = colors.textPrimary,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(0.5.dp, colors.border, RoundedCornerShape(8.dp))
                .background(colors.surfaceSecondary)
                .clickable { expanded = true }
                .padding(14.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.surface)
        ) {
            entry.options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = colors.textPrimary) },
                    onClick = {
                        PhantomConfig.setValue(entry.key, option)
                        expanded = false
                    }
                )
            }
        }
    }
}
