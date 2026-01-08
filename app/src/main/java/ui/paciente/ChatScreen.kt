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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class MsgUi(
    val id: String,
    val from: String,
    val text: String,
    val createdAt: Timestamp?
)

private fun DocumentSnapshot.toMsgUi(): MsgUi {
    return MsgUi(
        id = id,
        from = getString("from") ?: "",
        text = getString("text") ?: "",
        createdAt = getTimestamp("createdAt")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController) {
    val context = LocalContext.current
    val db = Firebase.firestore

    // ✅ DEMO: luego lo cambiaréis por uid real + medico asignado
    val pacienteId = "demo_paciente"
    val medicoId = "demo_medico"
    val chatId = "${medicoId}_$pacienteId"

    var msgText by remember { mutableStateOf("") }
    var mensajes by remember { mutableStateOf<List<MsgUi>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Listener tiempo real
    DisposableEffect(Unit) {
        val reg = db.collection("chats")
            .document(chatId)
            .collection("mensajes")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    error = e.message
                    cargando = false
                    return@addSnapshotListener
                }
                mensajes = (snap?.documents ?: emptyList())
                    .map { it.toMsgUi() }
                cargando = false
            }

        onDispose { reg.remove() }
    }

    fun enviar() {
        val text = msgText.trim()
        if (text.isBlank()) return

        // (opcional) crea/actualiza el doc del chat
        val chatInfo = hashMapOf(
            "pacienteId" to pacienteId,
            "medicoId" to medicoId,
            "updatedAt" to Timestamp.now()
        )
        db.collection("chats").document(chatId).set(chatInfo)

        val data = hashMapOf(
            "from" to "paciente",
            "text" to text,
            "createdAt" to Timestamp.now()
        )

        db.collection("chats")
            .document(chatId)
            .collection("mensajes")
            .add(data)
            .addOnSuccessListener {
                msgText = ""
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CHAT") },
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
                .fillMaxSize()
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (cargando) Text("Cargando chat…")
            if (error != null) Text("Error: $error")

            // Lista mensajes
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mensajes) { m ->
                    val esPaciente = m.from == "paciente"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (esPaciente) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(if (esPaciente) "Tú" else "Médico")
                                Spacer(Modifier.padding(2.dp))
                                Text(m.text)
                            }
                        }
                    }
                }
            }

            // Input + enviar
            OutlinedTextField(
                value = msgText,
                onValueChange = { msgText = it },
                label = { Text("Escribe un mensaje…") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { enviar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar")
            }
        }
    }
}
