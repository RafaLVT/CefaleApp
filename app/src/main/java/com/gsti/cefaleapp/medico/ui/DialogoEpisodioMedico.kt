package com.gsti.cefaleapp.medico.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gsti.cefaleapp.medico.model.EpisodioCalendario

@Composable
fun DialogoEpisodioMedico(
    episodio: EpisodioCalendario,
    onGuardarComentario: (String) -> Unit,
    onCerrar: () -> Unit
) {
    var comentario by remember {
        mutableStateOf(episodio.comentarioMedico ?: "")
    }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text("Episodio ${episodio.fecha}")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text("Datos registrados por el paciente")

                Text("Intensidad: ${episodio.intensidad}/3")
                Text("Duración: ${episodio.duracionMin} minutos")

                if (episodio.sintomas.isNotEmpty()) {
                    Text("Síntomas: ${episodio.sintomas.joinToString(", ")}")
                } else {
                    Text("Síntomas: no especificados")
                }

                Text(
                    text = "Tomó medicación: ${
                        if (episodio.tomoMedicacion) "Sí" else "No"
                    }"
                )

                Text(
                    text = "Hubo alivio: ${
                        if (episodio.alivio) "Sí" else "No"
                    }"
                )

                if (episodio.nota.isNotBlank()) {
                    Text("Nota del paciente:")
                    Text(episodio.nota)
                } else {
                    Text("Nota del paciente: no añadió comentarios")
                }

                Divider()

                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentario médico") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onGuardarComentario(comentario)
                onCerrar()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) {
                Text("Cerrar")
            }
        }
    )
}

