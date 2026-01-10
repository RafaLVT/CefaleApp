package com.gsti.cefaleapp.medico.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PacienteDetalleUiState(
    val isLoading: Boolean = true,
    val email: String = "",
    val diagnostico: String = "",
    val medicacion: String = "",
    val error: String? = null
)

class PacienteDetalleViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(PacienteDetalleUiState())
    val uiState: StateFlow<PacienteDetalleUiState> = _uiState.asStateFlow()

    fun cargarPaciente(pacienteId: String) {
        _uiState.value = PacienteDetalleUiState(isLoading = true)

        db.collection("users")
            .document(pacienteId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _uiState.value = PacienteDetalleUiState(
                        isLoading = false,
                        email = doc.getString("email") ?: "",
                        diagnostico = doc.getString("diagnostico") ?: "",
                        medicacion = doc.getString("medicacion") ?: ""
                    )
                } else {
                    _uiState.value = PacienteDetalleUiState(
                        isLoading = false,
                        error = "Paciente no encontrado"
                    )
                }
            }
            .addOnFailureListener {
                _uiState.value = PacienteDetalleUiState(
                    isLoading = false,
                    error = it.message
                )
            }
    }
}
