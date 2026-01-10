package com.gsti.cefaleapp.medico.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FormularioPacienteUiState(
    val diagnostico: String = "",
    val medicacion: String = "",
    val examenFisico: String = "",
    val pruebasRealizadas: String = "",
    val evolucion: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val guardadoCorrecto: Boolean = false
)

class FormularioPacienteViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(FormularioPacienteUiState())
    val uiState: StateFlow<FormularioPacienteUiState> = _uiState.asStateFlow()

    fun onDiagnosticoChange(value: String) {
        _uiState.value = _uiState.value.copy(diagnostico = value)
    }

    fun onMedicacionChange(value: String) {
        _uiState.value = _uiState.value.copy(medicacion = value)
    }

    fun onExamenFisicoChange(value: String) {
        _uiState.value = _uiState.value.copy(examenFisico = value)
    }

    fun onPruebasRealizadasChange(value: String) {
        _uiState.value = _uiState.value.copy(pruebasRealizadas = value)
    }

    fun onEvolucionChange(value: String) {
        _uiState.value = _uiState.value.copy(evolucion = value)
    }

    fun guardarFormulario(pacienteId: String) {
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)

        db.collection("users")
            .document(pacienteId)
            .update(
                mapOf(
                    "diagnostico" to _uiState.value.diagnostico,
                    "medicacion" to _uiState.value.medicacion,
                    "examenFisico" to _uiState.value.examenFisico,
                    "pruebasRealizadas" to _uiState.value.pruebasRealizadas,
                    "evolucion" to _uiState.value.evolucion
                )
            )
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    guardadoCorrecto = true
                )
            }
            .addOnFailureListener {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = it.message
                )
            }
    }
    fun cargarDatosPaciente(pacienteId: String) {
        db.collection("users")
            .document(pacienteId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _uiState.value = _uiState.value.copy(
                        diagnostico = doc.getString("diagnostico") ?: "",
                        medicacion = doc.getString("medicacion") ?: "",
                        examenFisico = doc.getString("examenFisico") ?: "",
                        pruebasRealizadas = doc.getString("pruebasRealizadas") ?: "",
                        evolucion = doc.getString("evolucion") ?: ""
                    )
                }
            }
            .addOnFailureListener {
                _uiState.value = _uiState.value.copy(error = it.message)
            }
    }

    fun resetGuardado() {
        _uiState.value = _uiState.value.copy(guardadoCorrecto = false)
    }






}
