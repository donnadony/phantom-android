package com.phantom.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.phantom.Phantom
import com.phantom.theme.LocalPhantomColors

class PhantomActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalPhantomColors provides Phantom.currentTheme) {
                PhantomScreen(onClose = { finish() })
            }
        }
    }
}
