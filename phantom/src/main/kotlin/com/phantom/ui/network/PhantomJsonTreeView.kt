package com.phantom.ui.network

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phantom.theme.LocalPhantomColors
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun PhantomJsonTreeView(jsonString: String, modifier: Modifier = Modifier) {
    val expandedNodes = remember { mutableStateMapOf<String, Boolean>() }
    val colors = LocalPhantomColors.current

    val parsed = remember(jsonString) {
        runCatching {
            val trimmed = jsonString.trim()
            when {
                trimmed.startsWith("{") -> JSONObject(trimmed)
                trimmed.startsWith("[") -> JSONArray(trimmed)
                else -> null
            }
        }.getOrNull()
    }

    Column(modifier = modifier) {
        when (parsed) {
            is JSONObject -> JsonObjectNode(parsed, "", 0, expandedNodes, colors)
            is JSONArray -> JsonArrayNode(parsed, "root", 0, expandedNodes, colors)
            else -> Text(jsonString, color = colors.textPrimary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun JsonObjectNode(
    obj: JSONObject,
    path: String,
    depth: Int,
    expandedNodes: MutableMap<String, Boolean>,
    colors: com.phantom.theme.PhantomTheme
) {
    val keys = obj.keys().asSequence().toList()
    keys.forEach { key ->
        val nodePath = "$path/$key"
        val value = obj.get(key)
        JsonValueNode(key, value, nodePath, depth, expandedNodes, colors)
    }
}

@Composable
private fun JsonArrayNode(
    arr: JSONArray,
    path: String,
    depth: Int,
    expandedNodes: MutableMap<String, Boolean>,
    colors: com.phantom.theme.PhantomTheme
) {
    for (i in 0 until arr.length()) {
        val nodePath = "$path[$i]"
        val value = arr.get(i)
        JsonValueNode("[$i]", value, nodePath, depth, expandedNodes, colors)
    }
}

@Composable
private fun JsonValueNode(
    key: String,
    value: Any?,
    path: String,
    depth: Int,
    expandedNodes: MutableMap<String, Boolean>,
    colors: com.phantom.theme.PhantomTheme
) {
    val indent = depth * 16
    val isExpanded = expandedNodes[path] ?: false

    when (value) {
        is JSONObject -> {
            val arrow = if (isExpanded) "▼" else "▶"
            Row(
                modifier = Modifier
                    .padding(start = indent.dp, top = 2.dp, bottom = 2.dp)
                    .clickable { expandedNodes[path] = !isExpanded }
            ) {
                Text("$arrow ", color = colors.textTertiary, fontSize = 12.sp)
                Text("$key: ", color = colors.accent, fontSize = 12.sp)
                Text("{${value.length()}}", color = colors.textTertiary, fontSize = 12.sp)
            }
            if (isExpanded) {
                JsonObjectNode(value, path, depth + 1, expandedNodes, colors)
            }
        }
        is JSONArray -> {
            val arrow = if (isExpanded) "▼" else "▶"
            Row(
                modifier = Modifier
                    .padding(start = indent.dp, top = 2.dp, bottom = 2.dp)
                    .clickable { expandedNodes[path] = !isExpanded }
            ) {
                Text("$arrow ", color = colors.textTertiary, fontSize = 12.sp)
                Text("$key: ", color = colors.accent, fontSize = 12.sp)
                Text("[${value.length()}]", color = colors.textTertiary, fontSize = 12.sp)
            }
            if (isExpanded) {
                JsonArrayNode(value, path, depth + 1, expandedNodes, colors)
            }
        }
        is String -> {
            Row(modifier = Modifier.padding(start = indent.dp, top = 2.dp, bottom = 2.dp)) {
                Text("$key: ", color = colors.accent, fontSize = 12.sp)
                Text("\"$value\"", color = colors.success, fontSize = 12.sp)
            }
        }
        is Number -> {
            Row(modifier = Modifier.padding(start = indent.dp, top = 2.dp, bottom = 2.dp)) {
                Text("$key: ", color = colors.accent, fontSize = 12.sp)
                Text("$value", color = colors.warning, fontSize = 12.sp)
            }
        }
        is Boolean -> {
            Row(modifier = Modifier.padding(start = indent.dp, top = 2.dp, bottom = 2.dp)) {
                Text("$key: ", color = colors.accent, fontSize = 12.sp)
                Text("$value", color = colors.info, fontSize = 12.sp)
            }
        }
        else -> {
            Row(modifier = Modifier.padding(start = indent.dp, top = 2.dp, bottom = 2.dp)) {
                Text("$key: ", color = colors.accent, fontSize = 12.sp)
                Text("null", color = colors.textTertiary, fontSize = 12.sp)
            }
        }
    }
}
