package com.gsti.cefaleapp.ui.paciente

import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CitaUi(
    val id: String,
    val pacienteId: String,
    val fecha: String,
    val motivo: String,
    val estado: String,
    val createdAt: Timestamp?
)

private fun DocumentSnapshot.toCitaUi(): CitaUi {
    return CitaUi(
        id = id,
        pacienteId = getString("pacienteId") ?: "",
        fecha = getString("fecha") ?: "",
        motivo = getString("motivo") ?: "",
        estado = getString("estado") ?: "pendiente",
        createdAt = getTimestamp("createdAt")
    )
}

private fun hoyYyyyMmDd(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasScreen(navController: NavController) {
    val context = LocalContext.current
    val db = Firebase.firestore

    val pacienteId = FirebaseAuth.getInstance().currentUser?.uid

    var fecha by remember { mutableStateOf(hoyYyyyMmDd()) }
    var motivo by remember { mutableStateOf("") }

    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var citas by remember { mutableStateOf<List<CitaUi>>(emptyList()) }

    DisposableEffect(Unit) {
        val reg = db.collection("citas")
            .whereEqualTo("pacienteId", pacienteId)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    error = e.message
                    cargando = false
                    return@addSnapshotListener
                }

                val lista = (snap?.documents ?: emptyList())
                    .map { it.toCitaUi() }
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }

                citas = lista
                cargando = false
            }

        onDispose { reg.remove() }
    }

    fun solicitarCita() {
        val m = motivo.trim()
        if (m.isBlank()) {
            Toast.makeText(context, "Pon un motivo", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "pacienteId" to pacienteId,
            "fecha" to fecha.trim(),
            "motivo" to m,
            "estado" to "pendiente",
            "createdAt" to Timestamp.now()
        )

        db.collection("citas")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(context, "✅ Cita solicitada", Toast.LENGTH_SHORT).show()
                motivo = ""
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun borrarCita(id: String) {
        db.collection("citas").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "🗑️ Cita borrada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "❌ Error borrando: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CITAS") },
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
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { solicitarCita() }
            ) {
                Text("Solicitar cita")
            }

            Spacer(Modifier.height(6.dp))
            Text("Mis citas")

            if (cargando) {
                Text("Cargando…")
                return@Column
            }

            if (error != null) {
                Text("Error: $error")
                return@Column
            }

            if (citas.isEmpty()) {
                Text("No hay citas todavía.")
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(citas) { c ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📅 ${c.fecha}   |   Estado: ${c.estado}")
                            Text("📝 Motivo: ${c.motivo}")

                            Spacer(Modifier.height(6.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { borrarCita(c.id) }
                                ) { Text("Borrar") }
                            }
                        }
                    }
                }
            }
        }
    }
}
