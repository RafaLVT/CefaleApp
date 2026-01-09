package com.gsti.cefaleapp.ui.paciente

import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class EpisodioUi(
    val id: String,
    val fecha: String,
    val intensidad: Int,
    val duracionMin: Int?,
    val sintomas: List<String>,
    val tomoMedicacion: Boolean,
    val alivio: Boolean,
    val redFlag: Boolean,
    val nota: String,
    val createdAt: Timestamp?
)

private fun DocumentSnapshot.toEpisodioUi(): EpisodioUi {
    val fecha = getString("fecha") ?: ""
    val intensidad = (getLong("intensidad") ?: 0L).toInt()
    val duracionMin = (getLong("duracionMin") ?: getLong("duracion"))?.toInt()

    @Suppress("UNCHECKED_CAST")
    val sintomas = (get("sintomas") as? List<String>) ?: emptyList()

    val tomoMedicacion = getBoolean("tomoMedicacion") ?: false
    val alivio = getBoolean("alivio") ?: false
    val redFlag = getBoolean("redFlag") ?: false
    val nota = getString("nota") ?: ""
    val createdAt = getTimestamp("createdAt")

    return EpisodioUi(
        id = id,
        fecha = fecha,
        intensidad = intensidad,
        duracionMin = duracionMin,
        sintomas = sintomas,
        tomoMedicacion = tomoMedicacion,
        alivio = alivio,
        redFlag = redFlag,
        nota = nota,
        createdAt = createdAt
    )
}

private fun hoyYyyyMmDd(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

private fun desplazarFecha(fecha: String, dias: Int): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val base = sdf.parse(fecha) ?: Date()
    val cal = Calendar.getInstance()
    cal.time = base
    cal.add(Calendar.DAY_OF_MONTH, dias)
    return sdf.format(cal.time)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(navController: NavController) {
    val db = Firebase.firestore
    val pacienteId = FirebaseAuth.getInstance().currentUser?.uid

    var fechaSeleccionada by remember { mutableStateOf(hoyYyyyMmDd()) }
    var verTodos by remember { mutableStateOf(false) }

    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var episodios by remember { mutableStateOf<List<EpisodioUi>>(emptyList()) }

    // 🔥 Listener en tiempo real (cada vez que cambie Firestore o cambie la fecha)
    DisposableEffect(fechaSeleccionada, verTodos) {
        cargando = true
        error = null

        val query = if (verTodos) {
            db.collection("episodios")
                .whereEqualTo("pacienteId", pacienteId)
                .limit(50)
        } else {
            db.collection("episodios")
                .whereEqualTo("pacienteId", pacienteId)
                .whereEqualTo("fecha", fechaSeleccionada)
        }

        val reg = query.addSnapshotListener { snap, e ->
            if (e != null) {
                error = e.message
                cargando = false
                return@addSnapshotListener
            }
            val docs = snap?.documents ?: emptyList()
            episodios = docs.map { it.toEpisodioUi() }
                .sortedByDescending { it.createdAt?.seconds ?: 0L }
            cargando = false
        }

        onDispose { reg.remove() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CALENDARIO") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Selector simple de fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { fechaSeleccionada = desplazarFecha(fechaSeleccionada, -1) }) {
                    Text("◀")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Fecha")
                    Text(fechaSeleccionada)
                }

                Button(onClick = { fechaSeleccionada = desplazarFecha(fechaSeleccionada, +1) }) {
                    Text("▶")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { fechaSeleccionada = hoyYyyyMmDd() }) {
                    Text("Hoy")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ver todos")
                    Spacer(Modifier.padding(horizontal = 6.dp))
                    Switch(checked = verTodos, onCheckedChange = { verTodos = it })
                }
            }

            if (cargando) {
                Text("Cargando episodios…")
                return@Column
            }

            if (error != null) {
                Text("Error: $error")
                return@Column
            }

            if (episodios.isEmpty()) {
                Text("No hay episodios para esta fecha.")
                return@Column
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(episodios) { ep ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📅 ${ep.fecha}   |   Intensidad: ${ep.intensidad}")
                            Text("⏱ Duración: ${ep.duracionMin ?: 0} min")
                            Text("🤒 Síntomas: ${if (ep.sintomas.isEmpty()) "—" else ep.sintomas.joinToString()}")
                            Text("💊 Medicación: ${if (ep.tomoMedicacion) "Sí" else "No"}  |  Alivio: ${if (ep.alivio) "Sí" else "No"}")
                            Text("🚩 Red flag: ${if (ep.redFlag) "Sí" else "No"}")

                            if (ep.nota.isNotBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text("📝 Nota: ${ep.nota}")
                            }
                        }
                    }
                }
            }
        }
    }
}
