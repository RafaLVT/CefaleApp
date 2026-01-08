package com.gsti.cefaleapp.medico

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaDetallesPacienteMedico(
    onCalendarioClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Paciente: Juan Pérez",
            fontSize = 22.sp
        )

        Divider()

        Text("Diagnóstico actual")
        Text("Migraña crónica")

        Text("Medicamentos asignados")
        Text("• Ibuprofeno\n• Triptanes")

        Text("Antecedentes")
        Text("Historial de cefaleas desde 2021")

        Text("Informe de consulta")
        Text("Última revisión sin cambios significativos")

        Divider()

        Button(
            onClick = onCalendarioClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver calendario de dolor")
        }

        Button(
            onClick = onChatClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abrir chat con paciente")
        }
    }
}
