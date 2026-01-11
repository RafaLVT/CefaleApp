package com.gsti.cefaleapp.medico.model

data class PacienteConGravedad(
    val paciente: Paciente,
    val gravedadMedia7d: Double
)
