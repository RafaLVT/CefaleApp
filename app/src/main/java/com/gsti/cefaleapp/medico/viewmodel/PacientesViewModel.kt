package com.gsti.cefaleapp.medico.viewmodel

import androidx.lifecycle.ViewModel
import com.gsti.cefaleapp.medico.model.Paciente
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PacientesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // 🔹 Pacientes SIN médico (para asignar)
    private val _pacientesSinMedico = MutableStateFlow<List<Paciente>>(emptyList())
    val pacientesSinMedico: StateFlow<List<Paciente>> = _pacientesSinMedico

    // 🔹 Pacientes ASIGNADOS al médico
    private val _pacientesAsignados = MutableStateFlow<List<Paciente>>(emptyList())
    val pacientesAsignados: StateFlow<List<Paciente>> = _pacientesAsignados

    // ===============================
    // 📥 CARGAS
    // ===============================

    fun cargarPacientesSinMedico() {
        db.collection("users")
            .whereEqualTo("role", "paciente")
            .whereEqualTo("medicoId", null)
            .get()
            .addOnSuccessListener { result ->
                _pacientesSinMedico.value = result.documents.map {
                    Paciente(
                        id = it.id,
                        email = it.getString("email") ?: "",
                        medicoId = null
                    )
                }
            }
    }

    fun cargarPacientesAsignados(medicoId: String) {
        db.collection("users")
            .whereEqualTo("role", "paciente")
            .whereEqualTo("medicoId", medicoId)
            .get()
            .addOnSuccessListener { result ->
                _pacientesAsignados.value = result.documents.map {
                    Paciente(
                        id = it.id,
                        email = it.getString("email") ?: "",
                        medicoId = medicoId
                    )
                }
            }
    }

    // ===============================
    // 🔗 ASIGNACIÓN
    // ===============================

    fun asignarPaciente(pacienteId: String, medicoId: String) {
        db.collection("users")
            .document(pacienteId)
            .update("medicoId", medicoId)
    }
}
