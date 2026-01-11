package com.gsti.cefaleapp.medico.model

import com.google.firebase.Timestamp

data class CitaMedico(
    val id: String,
    val pacienteId: String,
    val medicoId: String,
    val fechaSolicitada: String,
    val fechaAsignada: String?,
    val motivo: String,
    val estado: String,
    val createdAt: Timestamp?
)
