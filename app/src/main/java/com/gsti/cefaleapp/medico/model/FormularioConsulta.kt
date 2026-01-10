package com.gsti.cefaleapp.medico.model

import com.google.firebase.Timestamp

data class FormularioConsulta(
    val id: String = "",
    val pacienteId: String = "",
    val medicoId: String = "",
    val diagnostico: String = "",
    val medicacion: String = "",
    val examenFisico: String = "",
    val pruebasRealizadas: String = "",
    val evolucion: String = "",
    val fecha: Timestamp = Timestamp.now()
)
