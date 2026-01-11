package com.gsti.cefaleapp.medico.model

import com.google.firebase.Timestamp

data class ChatResumenMedico(
    val chatId: String,
    val pacienteId: String,
    val updatedAt: Timestamp?
)
