package com.gsti.cefaleapp.medico.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.gsti.cefaleapp.medico.model.ChatResumenMedico
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatsMedicoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _chats = MutableStateFlow<List<ChatResumenMedico>>(emptyList())
    val chats: StateFlow<List<ChatResumenMedico>> = _chats

    fun cargarChatsMedico(medicoId: String) {
        db.collection("chats")
            .whereEqualTo("medicoId", medicoId)
            .addSnapshotListener { snap, _ ->
                _chats.value = snap?.documents?.map {
                    ChatResumenMedico(
                        chatId = it.id,
                        pacienteId = it.getString("pacienteId") ?: "",
                        updatedAt = it.getTimestamp("updatedAt")
                    )
                } ?: emptyList()
            }
    }
}
