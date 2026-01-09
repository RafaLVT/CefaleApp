package com.gsti.cefaleapp.medico.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaPrincipalMedico(
    onPacientesClick: () -> Unit,
    onCitasClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Panel del médico",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onPacientesClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pacientes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCitasClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Citas")
        }
    }
}