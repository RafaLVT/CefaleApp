package com.gsti.cefaleapp.medico.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import com.gsti.cefaleapp.medico.viewmodel.PacientesViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button



@Composable
fun PantallaAsignarPaciente(
    viewModel: PacientesViewModel,
    medicoId: String,
    onPacienteAsignado: () -> Unit
) {
    val pacientes by viewModel.pacientesSinMedico.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarPacientesSinMedico()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Asignar pacientes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (pacientes.isEmpty()) {
            Text("No hay pacientes pendientes de asignar")
        } else {
            LazyColumn {
                items(pacientes) { paciente ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(paciente.email)

                            Button(
                                onClick = {
                                    viewModel.asignarPaciente(
                                        paciente.id,
                                        medicoId
                                    )
                                    onPacienteAsignado()
                                }
                            ) {
                                Text("Asignar")
                            }
                        }
                    }
                }
            }
        }
    }
}
