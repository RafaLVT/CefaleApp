package com.gsti.cefaleapp.medico.model

data class Paciente(
    val id: String = "",
    val email: String = "",
    val medicoId: String? = null,
    val diagnostico: String = "",
    val medicacion: String = ""
)
