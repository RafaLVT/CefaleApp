package com.gsti.cefaleapp.medico.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.gsti.cefaleapp.medico.model.CitaMedico
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.map

class CitasMedicoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _citas = MutableStateFlow<List<CitaMedico>>(emptyList())
    val citas: StateFlow<List<CitaMedico>> = _citas

    private val _pacientes = MutableStateFlow<Map<String, String>>(emptyMap())
    val pacientes: StateFlow<Map<String, String>> = _pacientes


    fun cargarCitasMedico(medicoId: String) {
        db.collection("citas")
            .whereEqualTo("medicoId", medicoId)
            .addSnapshotListener { snap, _ ->

                val lista = snap?.documents?.map {
                    CitaMedico(
                        id = it.id,
                        pacienteId = it.getString("pacienteId") ?: "",
                        medicoId = it.getString("medicoId") ?: "",
                        fechaSolicitada = it.getString("fecha") ?: "",
                        fechaAsignada = it.getString("fechaAsignada"),
                        motivo = it.getString("motivo") ?: "",
                        estado = it.getString("estado") ?: "pendiente",
                        createdAt = it.getTimestamp("createdAt")
                    )
                } ?: emptyList()

                _citas.value = lista
                cargarPacientesDeCitas(lista)
            }
    }


    private fun cargarPacientesDeCitas(citas: List<CitaMedico>) {
        val ids = citas.map { it.pacienteId }.distinct()

        if (ids.isEmpty()) return

        db.collection("users")
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { result ->
                _pacientes.value = result.documents.associate {
                    it.id to (it.getString("email") ?: "Paciente")
                }
            }
    }


    fun concertarCita(citaId: String, fechaAsignada: String) {
        db.collection("citas")
            .document(citaId)
            .update(
                mapOf(
                    "fechaAsignada" to fechaAsignada,
                    "estado" to "concertada"
                )
            )
    }
}
