package com.gsti.cefaleapp.ui.paciente

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EsperaAsignacionScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tu cuenta está pendiente de asignación")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tu médico te activará el acceso en breve")
        }
    }
}
