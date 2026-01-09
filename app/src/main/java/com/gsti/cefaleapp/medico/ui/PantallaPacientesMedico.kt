package com.gsti.cefaleapp.medico.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gsti.cefaleapp.medico.model.Paciente

@Composable
fun PantallaPacientesMedico(
    pacientes: List<Paciente>,
    onAsignarPacienteClick: () -> Unit,
    onPacienteClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Pacientes",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onAsignarPacienteClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Asignar paciente")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (pacientes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes pacientes asignados todavía")
            }
        } else {
            Column {
                pacientes.forEach { paciente ->
                    Text(
                        text = paciente.email,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onPacienteClick(paciente.id)
                            }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}
