package com.gsti.cefaleapp.medico.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


data class MsgUi(
    val id: String,
    val from: String,
    val text: String,
    val createdAt: Timestamp?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaChatMedico(
    navController: NavController,
    medicoId: String,
    pacienteId: String
) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val chatId = "${medicoId}_${pacienteId}"

    var mensajes by remember { mutableStateOf<List<MsgUi>>(emptyList()) }
    var msgText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }


    DisposableEffect(chatId) {
        val reg = db.collection("chats")
            .document(chatId)
            .collection("mensajes")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    error = e.message
                    return@addSnapshotListener
                }

                mensajes = snap?.documents?.map {
                    MsgUi(
                        id = it.id,
                        from = it.getString("from") ?: "",
                        text = it.getString("text") ?: "",
                        createdAt = it.getTimestamp("createdAt")
                    )
                } ?: emptyList()
            }

        onDispose { reg.remove() }
    }

    fun enviar() {
        val text = msgText.trim()
        if (text.isBlank()) return

        // actualiza info del chat
        db.collection("chats")
            .document(chatId)
            .set(
                mapOf(
                    "medicoId" to medicoId,
                    "pacienteId" to pacienteId,
                    "updatedAt" to Timestamp.now()
                )
            )

        db.collection("chats")
            .document(chatId)
            .collection("mensajes")
            .add(
                mapOf(
                    "from" to "medico",
                    "text" to text,
                    "createdAt" to Timestamp.now()
                )
            )
            .addOnSuccessListener { msgText = "" }
            .addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat con paciente") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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

            if (error != null) {
                Text("Error: $error")
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mensajes) { m ->
                    val esMedico = m.from == "medico"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            if (esMedico) Arrangement.End else Arrangement.Start
                    ) {
                        Card(modifier = Modifier.fillMaxWidth(0.8f)) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment =
                                    if (esMedico) Alignment.End else Alignment.Start
                            ) {
                                Text(if (esMedico) "Tú" else "Paciente")
                                Spacer(Modifier.height(2.dp))
                                Text(m.text)
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = msgText,
                onValueChange = { msgText = it },
                label = { Text("Escribe un mensaje…") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { enviar() }
            ) {
                Text("Enviar")
            }
        }
    }
}
