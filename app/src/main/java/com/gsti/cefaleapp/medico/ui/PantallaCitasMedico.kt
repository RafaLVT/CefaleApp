import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gsti.cefaleapp.medico.model.CitaMedico
import com.gsti.cefaleapp.medico.viewmodel.CitasMedicoViewModel
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.Button



@Composable
fun PantallaCitasMedico(
    medicoId: String,
    viewModel: CitasMedicoViewModel = viewModel()
) {
    val citas by viewModel.citas.collectAsState()
    val pacientes by viewModel.pacientes.collectAsState()


    var citaSeleccionada by remember { mutableStateOf<CitaMedico?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarCitasMedico(medicoId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(citas) { cita ->
            var fechaAsignada by remember(cita.id) { mutableStateOf("") }
            val nombrePaciente = pacientes[cita.pacienteId] ?: "Paciente"
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Text("👤 Paciente: $nombrePaciente")
                    Text("📅 Solicitud: ${cita.fechaSolicitada}")
                    Text("📝 Motivo: ${cita.motivo}")
                    Text("📌 Estado: ${cita.estado}")

                    if (cita.estado == "pendiente") {
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = fechaAsignada,
                            onValueChange = { fechaAsignada = it },
                            label = { Text("Fecha a asignar (YYYY-MM-DD)") }
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.concertarCita(cita.id, fechaAsignada)
                                fechaAsignada = ""
                            }
                        ) {
                            Text("Concertar cita")
                        }
                    }

                    if (cita.estado == "concertada") {
                        Spacer(Modifier.height(6.dp))
                        Text("✅ Fecha asignada: ${cita.fechaAsignada}")
                    }
                }
            }
        }
    }
}
