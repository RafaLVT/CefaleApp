package com.gsti.cefaleapp.medico.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.gsti.cefaleapp.medico.model.EpisodioCalendario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CalendarioDolorMedicoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _episodios = MutableStateFlow<List<EpisodioCalendario>>(emptyList())
    val episodios: StateFlow<List<EpisodioCalendario>> = _episodios

    fun cargarEpisodiosPaciente(pacienteId: String) {
        db.collection("episodios")
            .whereEqualTo("pacienteId", pacienteId)
            .get()
            .addOnSuccessListener { result ->
                _episodios.value = result.documents.map {
                    EpisodioCalendario(
                        id = it.id,
                        fecha = it.getString("fecha") ?: "",
                        intensidad = (it.getLong("intensidad") ?: 0).toInt(),
                        duracionMin = (it.getLong("duracionMin") ?: 0).toInt(),
                        sintomas = it.get("sintomas") as? List<String> ?: emptyList(),
                        tomoMedicacion = it.getBoolean("tomoMedicacion") ?: false,
                        alivio = it.getBoolean("alivio") ?: false,
                        medicamento = it.getString("medicamento"),
                        dosis = it.getString("dosis"),
                        nota = it.getString("nota") ?: "",
                        comentarioMedico = it.getString("comentarioMedico")
                    )
                }
            }
    }

    fun guardarComentarioMedico(
        episodioId: String,
        comentario: String,
        pacienteId: String,
        onFinish: () -> Unit
    ) {
        db.collection("episodios")
            .document(episodioId)
            .update("comentarioMedico", comentario)
            .addOnSuccessListener {
                cargarEpisodiosPaciente(pacienteId)
                onFinish()
            }
    }

}
