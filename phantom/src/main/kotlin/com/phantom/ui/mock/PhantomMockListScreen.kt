package com.phantom.ui.mock

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantom.core.PhantomMockInterceptor
import com.phantom.model.PhantomMockRule
import com.phantom.theme.LocalPhantomColors

@Composable
fun PhantomMockListScreen(
    onBack: () -> Unit,
    onEditRule: (String) -> Unit,
    onNewRule: () -> Unit
) {
    val colors = LocalPhantomColors.current
    val rules by PhantomMockInterceptor.rules.collectAsState()

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
                Text(text = "Mock Services", color = colors.textPrimary, fontSize = 20.sp)
            }
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = colors.accent,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceSecondary)
                    .clickable { onNewRule() }
                    .padding(4.dp)
            )
        }

        if (rules.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No mock rules", color = colors.textTertiary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(rules, key = { it.id }) { rule ->
                    MockRuleRow(
                        rule = rule,
                        onToggle = { PhantomMockInterceptor.toggleRule(rule.id) },
                        onEdit = { onEditRule(rule.id) },
                        onDelete = { PhantomMockInterceptor.deleteRule(rule.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MockRuleRow(
    rule: PhantomMockRule,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalPhantomColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface)
            .clickable { onEdit() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = rule.method, color = colors.accent, fontSize = 12.sp)
                Text(
                    text = "${rule.responses.size} response(s)",
                    color = colors.textTertiary,
                    fontSize = 12.sp
                )
            }
            Text(
                text = rule.url,
                color = colors.textPrimary,
                fontSize = 13.sp,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Switch(
            checked = rule.isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedTrackColor = colors.mockEnabled,
                uncheckedTrackColor = colors.mockDisabled
            )
        )
    }
}
