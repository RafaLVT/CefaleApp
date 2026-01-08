package com.gsti.cefaleapp.ui.paciente

import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TomaUi(
    val id: String,
    val pacienteId: String,
    val fecha: String,
    val medicamento: String,
    val dosis: String,
    val createdAt: Timestamp?
)

private fun DocumentSnapshot.toTomaUi(): TomaUi {
    return TomaUi(
        id = id,
        pacienteId = getString("pacienteId") ?: "",
        fecha = getString("fecha") ?: "",
        medicamento = getString("medicamento") ?: "",
        dosis = getString("dosis") ?: "",
        createdAt = getTimestamp("createdAt")
    )
}

private fun hoyYyyyMmDd(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicacionScreen() {
    val context = LocalContext.current
    val db = Firebase.firestore

    // ✅ DEMO (luego se cambia por uid del login)
    val pacienteId = "demo_paciente"

    var medicamento by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    val fecha = remember { hoyYyyyMmDd() }

    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var tomas by remember { mutableStateOf<List<TomaUi>>(emptyList()) }

    // Listener en tiempo real (sin orderBy para evitar índices; ordenamos en Kotlin)
    DisposableEffect(Unit) {
        val reg = db.collection("tomas_medicacion")
            .whereEqualTo("pacienteId", pacienteId)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    error = e.message
                    cargando = false
                    return@addSnapshotListener
                }
                val lista = (snap?.documents ?: emptyList())
                    .map { it.toTomaUi() }
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }

                tomas = lista
                cargando = false
            }

        onDispose { reg.remove() }
    }

    fun guardarToma() {
        val med = medicamento.trim()
        val dos = dosis.trim()

        if (med.isBlank()) {
            Toast.makeText(context, "Pon el nombre del medicamento", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "pacienteId" to pacienteId,
            "fecha" to fecha,
            "medicamento" to med,
            "dosis" to dos,
            "createdAt" to Timestamp.now()
        )

        db.collection("tomas_medicacion")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(context, "✅ Toma guardada", Toast.LENGTH_SHORT).show()
                medicamento = ""
                dosis = ""
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun borrarToma(id: String) {
        db.collection("tomas_medicacion").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "🗑️ Toma borrada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "❌ Error borrando: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Medicación") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Fecha: $fecha")

            OutlinedTextField(
                value = medicamento,
                onValueChange = { medicamento = it },
                label = { Text("Medicamento (ej: Ibuprofeno)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dosis,
                onValueChange = { dosis = it },
                label = { Text("Dosis (opcional) (ej: 400mg)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { guardarToma() }
            ) {
                Text("Registrar toma")
            }

            Spacer(Modifier.height(6.dp))
            Text("Historial de tomas")

            if (cargando) {
                Text("Cargando…")
                return@Column
            }

            if (error != null) {
                Text("Error: $error")
                return@Column
            }

            if (tomas.isEmpty()) {
                Text("No hay tomas todavía.")
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(tomas) { t ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("💊 ${t.medicamento}  ${if (t.dosis.isBlank()) "" else "(${t.dosis})"}")
                            Text("📅 ${t.fecha}")

                            Spacer(Modifier.height(6.dp))

                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { borrarToma(t.id) }
                            ) {
                                Text("Borrar")
                            }
                        }
                    }
                }
            }
        }
    }
}
