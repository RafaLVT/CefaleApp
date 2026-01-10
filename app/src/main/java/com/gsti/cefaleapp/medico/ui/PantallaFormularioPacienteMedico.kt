package com.gsti.cefaleapp.medico.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gsti.cefaleapp.medico.viewmodel.FormularioPacienteViewModel

@Composable
fun PantallaFormularioPacienteMedico(
    pacienteId: String,
    medicoId: String,
    onFormularioGuardado: () -> Unit,
    viewModel: FormularioPacienteViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(pacienteId) {
        viewModel.cargarDatosPaciente(pacienteId)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Formulario de consulta", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = state.diagnostico,
            onValueChange = viewModel::onDiagnosticoChange,
            label = { Text("Diagnóstico") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.medicacion,
            onValueChange = viewModel::onMedicacionChange,
            label = { Text("Medicación") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.examenFisico,
            onValueChange = viewModel::onExamenFisicoChange,
            label = { Text("Examen físico") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = state.pruebasRealizadas,
            onValueChange = viewModel::onPruebasRealizadasChange,
            label = { Text("Pruebas realizadas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = state.evolucion,
            onValueChange = viewModel::onEvolucionChange,
            label = { Text("Evolucion") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Button(
            onClick = {
                viewModel.guardarFormulario(
                    pacienteId = pacienteId
                )
            },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar formulario")
        }

        if (state.guardadoCorrecto) {
            LaunchedEffect(Unit) {
                onFormularioGuardado()
            }
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
