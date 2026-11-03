package com.example.picosmokedemo.p7c91e42a.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.design.Text

@Composable
fun HomePage() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("PICO Spatial Ready", color = PicoTheme.colorScheme.labelPrimary)
    }
}
