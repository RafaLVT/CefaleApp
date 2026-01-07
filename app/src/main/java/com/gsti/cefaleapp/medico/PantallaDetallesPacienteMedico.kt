package com.gsti.cefaleapp.medico

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaDetallesPacienteMedico(
    onChatMedicoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text("Detalle del paciente", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Diagnóstico actual")
        Text("Medicamentos")
        Text("Antecedentes")
        Text("Informe de consulta")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { }) {
            Text("Calendario de dolor")
        }

        Button(
            onClick = onChatMedicoClick
        ) {
            Text("Chat con paciente")
        }
    }
}
