package com.gsti.cefaleapp.medico.model

data class EpisodioCalendario(
    val id: String,
    val fecha: String,            // yyyy-MM-dd
    val intensidad: Int,
    val duracionMin: Int,
    val sintomas: List<String>,
    val tomoMedicacion: Boolean,
    val alivio: Boolean,
    val medicamento: String?,
    val dosis: String?,
    val nota: String,
    val comentarioMedico: String?
)
