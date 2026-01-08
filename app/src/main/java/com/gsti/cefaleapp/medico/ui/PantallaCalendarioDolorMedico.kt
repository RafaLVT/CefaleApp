package com.gsti.cefaleapp.medico.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaCalendarioDolorMedico() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Calendario de dolor",
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Vista mensual por colores (verde / amarillo / rojo)")

        Spacer(modifier = Modifier.height(32.dp))

        // Placeholder visual
        repeat(5) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Día ${it + 1} - Dolor moderado",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
