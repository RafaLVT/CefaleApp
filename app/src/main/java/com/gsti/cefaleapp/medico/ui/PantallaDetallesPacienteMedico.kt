package com.gsti.cefaleapp.medico.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gsti.cefaleapp.medico.viewmodel.PacienteDetalleViewModel

@Composable
fun PantallaDetallesPacienteMedico(
    pacienteId: String,
    onEditarFormularioClick: (String) -> Unit,
    onAntecedentesClick: (String) -> Unit,
    onCalendarioClick: (String) -> Unit,
    onChatClick: (String) -> Unit,
    viewModel: PacienteDetalleViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(pacienteId) {
        viewModel.cargarPaciente(pacienteId)
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Text(
                text = state.error!!,
                modifier = Modifier.padding(24.dp)
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Paciente: ${state.email}",
                    fontSize = 22.sp
                )

                Divider()

                Text("Diagnóstico actual", style = MaterialTheme.typography.titleMedium)
                Text(state.diagnostico.ifBlank { "No definido" })

                Text("Medicación actual", style = MaterialTheme.typography.titleMedium)
                Text(state.medicacion.ifBlank { "No definida" })

                Divider()

                Button(
                    onClick = { onEditarFormularioClick(pacienteId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar formulario")
                }

                Divider()

                Button(
                    onClick = { onAntecedentesClick(pacienteId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Antecedentes del paciente")
                }


                Button(
                    onClick = { onCalendarioClick(pacienteId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Calendario de dolor")
                }

                Button(
                    onClick = { onChatClick(pacienteId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Chat con el paciente")
                }
            }
        }
    }
}
