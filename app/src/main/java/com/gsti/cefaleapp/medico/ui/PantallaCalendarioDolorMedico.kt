package com.gsti.cefaleapp.medico.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gsti.cefaleapp.medico.model.EpisodioCalendario
import com.gsti.cefaleapp.medico.viewmodel.CalendarioDolorMedicoViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun PantallaCalendarioDolorMedico(
    pacienteId: String,
    onBack: () -> Unit,
    viewModel: CalendarioDolorMedicoViewModel = viewModel()
) {
    val episodios by viewModel.episodios.collectAsState()
    var mesActual by remember { mutableStateOf(YearMonth.now()) }
    var episodioSeleccionado by remember { mutableStateOf<EpisodioCalendario?>(null) }

    LaunchedEffect(pacienteId) {
        viewModel.cargarEpisodiosPaciente(pacienteId)
    }

    val episodiosPorFecha = episodios.associateBy {
        LocalDate.parse(it.fecha)
    }

    val semanas = generarCalendarioMes(mesActual)

    Column(modifier = Modifier.fillMaxSize()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 12.dp)) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹",
                modifier = Modifier
                    .weight(1f)
                    .clickable { mesActual = mesActual.minusMonths(1) },
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Start
            )

            Text(
                text = "${mesActual.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() }} ${mesActual.year}",
                modifier = Modifier.weight(3f),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Text(
                text = "›",
                modifier = Modifier
                    .weight(1f)
                    .clickable { mesActual = mesActual.plusMonths(1) },
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.End
            )
        }

        Divider(modifier = Modifier.padding(bottom = 8.dp))

        Spacer(Modifier.height(16.dp))


        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }

        Spacer(Modifier.height(8.dp))

        //calen
        semanas.forEach { semana ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                semana.forEach { fecha ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (fecha != null) {
                            val episodio = episodiosPorFecha[fecha]

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = episodio?.let {
                                            colorPorIntensidad(it.intensidad)
                                        } ?: Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable(enabled = episodio != null) {
                                        episodioSeleccionado = episodio
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(fecha.dayOfMonth.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    episodioSeleccionado?.let {
        DialogoEpisodioMedico(
            episodio = it,
            onGuardarComentario = { comentario ->
                viewModel.guardarComentarioMedico(
                    episodioId = it.id,
                    comentario = comentario,
                    pacienteId = pacienteId
                ) {
                    episodioSeleccionado = null
                }
            },

            onCerrar = { episodioSeleccionado = null }
        )
    }
}
