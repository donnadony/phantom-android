package com.phantom.ui.mock

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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
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
                text = "Mock Services (${rules.size})",
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "+ New",
                color = colors.accent,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceSecondary)
                    .clickable { onNewRule() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        HorizontalDivider(color = colors.border, thickness = 0.5.dp)

        if (rules.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No mock rules", color = colors.textTertiary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(rules, key = { it.id }) { rule ->
                    MockRuleRow(
                        rule = rule,
                        onToggle = { PhantomMockInterceptor.toggleRule(rule.id) },
                        onEdit = { onEditRule(rule.id) }
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
    onEdit: () -> Unit
) {
    val colors = LocalPhantomColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(0.5.dp, colors.border, RoundedCornerShape(10.dp))
            .background(colors.surface)
            .clickable { onEdit() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (rule.description.isNotBlank()) {
                Text(
                    text = rule.description,
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = if (rule.description.isNotBlank()) Modifier.padding(top = 2.dp) else Modifier
            ) {
                Text(
                    text = rule.method,
                    color = colors.accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${rule.responses.size} response(s)",
                    color = colors.textTertiary,
                    fontSize = 12.sp
                )
            }
            Text(
                text = rule.url,
                color = colors.textSecondary,
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
