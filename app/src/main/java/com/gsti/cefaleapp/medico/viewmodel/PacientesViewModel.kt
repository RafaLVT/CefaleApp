package com.gsti.cefaleapp.medico.viewmodel

import androidx.lifecycle.ViewModel
import com.gsti.cefaleapp.medico.model.Paciente
import com.google.firebase.firestore.FirebaseFirestore
import com.gsti.cefaleapp.medico.model.PacienteConGravedad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate


class PacientesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()


    private val _pacientesSinMedico = MutableStateFlow<List<Paciente>>(emptyList())
    val pacientesSinMedico: StateFlow<List<Paciente>> = _pacientesSinMedico


    private val _pacientesAsignados = MutableStateFlow<List<Paciente>>(emptyList())
    val pacientesAsignados: StateFlow<List<Paciente>> = _pacientesAsignados

    private val _pacientesOrdenados = MutableStateFlow<List<PacienteConGravedad>>(emptyList())
    val pacientesOrdenados: StateFlow<List<PacienteConGravedad>> = _pacientesOrdenados



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
                        medicoId = null,
                        diagnostico = it.getString("diagnostico") ?: "",
                        medicacion = it.getString("medicacion") ?: ""
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
                        medicoId = medicoId,
                        diagnostico = it.getString("diagnostico") ?: "",
                        medicacion = it.getString("medicacion") ?: ""
                    )
                }
            }
    }

    fun cargarPacientesPorGravedad(medicoId: String) {
        db.collection("users")
            .whereEqualTo("role", "paciente")
            .whereEqualTo("medicoId", medicoId)
            .get()
            .addOnSuccessListener { pacientesSnap ->

                val pacientes = pacientesSnap.documents.map {
                    Paciente(
                        id = it.id,
                        email = it.getString("email") ?: "",
                        medicoId = medicoId,
                        diagnostico = it.getString("diagnostico") ?: "",
                        medicacion = it.getString("medicacion") ?: ""
                    )
                }

                if (pacientes.isEmpty()) {
                    _pacientesOrdenados.value = emptyList()
                    return@addOnSuccessListener
                }

                val hace7Dias = LocalDate.now().minusDays(7)

                db.collection("episodios")
                    .whereIn("pacienteId", pacientes.map { it.id })
                    .get()
                    .addOnSuccessListener { episodiosSnap ->

                        val episodiosPorPaciente =
                            episodiosSnap.documents
                                .mapNotNull { doc ->
                                    val fecha = doc.getString("fecha") ?: return@mapNotNull null
                                    val intensidad = doc.getLong("intensidad")?.toInt() ?: return@mapNotNull null
                                    val pacienteId = doc.getString("pacienteId") ?: return@mapNotNull null

                                    val fechaLocal = LocalDate.parse(fecha)
                                    if (fechaLocal.isBefore(hace7Dias)) return@mapNotNull null

                                    pacienteId to intensidad
                                }
                                .groupBy({ it.first }, { it.second })

                        val resultado = pacientes.map { paciente ->
                            val intensidades = episodiosPorPaciente[paciente.id]
                            val media = intensidades?.average() ?: 0.0

                            PacienteConGravedad(
                                paciente = paciente,
                                gravedadMedia7d = media
                            )
                        }.sortedByDescending { it.gravedadMedia7d }

                        _pacientesOrdenados.value = resultado
                    }
            }
    }




    fun asignarPaciente(pacienteId: String, medicoId: String) {
        db.collection("users")
            .document(pacienteId)
            .update("medicoId", medicoId)
    }
}
