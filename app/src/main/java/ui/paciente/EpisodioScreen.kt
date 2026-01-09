package com.gsti.cefaleapp.ui.paciente

import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodioScreen(navController: NavController) {
    val context = LocalContext.current
    val pacienteId = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore

    // ✅ Fecha compatible con minSdk 24
    val hoy = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    val intensidad = remember { mutableStateOf(0) } // 0..3
    val duracionMin = remember { mutableStateOf("") }
    val nota = remember { mutableStateOf("") }

    val tomoMedicacion = remember { mutableStateOf(false) }
    val alivio = remember { mutableStateOf(false) }
    val redFlag = remember { mutableStateOf(false) }

    val sintomas = listOf("Náuseas", "Fotofobia", "Fonofobia", "Aura", "Mareo")
    val sintomasSeleccionados = remember { mutableStateOf(setOf<String>()) }

    val guardando = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar episodio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()              // ✅ evita que el teclado tape el botón
                .navigationBarsPadding(),  // ✅ evita que la barra de abajo lo tape
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Fecha: $hoy")

            // Intensidad
            Text("Intensidad (0-3)")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(0, 1, 2, 3).forEach { value ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (intensidad.value == value),
                            onClick = { intensidad.value = value }
                        )
                        Text("$value")
                    }
                }
            }

            // Duración
            OutlinedTextField(
                value = duracionMin.value,
                onValueChange = { duracionMin.value = it },
                label = { Text("Duración (minutos)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Síntomas
            Text("Síntomas")
            sintomas.forEach { s ->
                val checked = sintomasSeleccionados.value.contains(s)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { isChecked ->
                            sintomasSeleccionados.value =
                                if (isChecked) sintomasSeleccionados.value + s
                                else sintomasSeleccionados.value - s
                        }
                    )
                    Text(s)
                }
            }

            // Medicación
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("¿Tomaste medicación?")
                Switch(
                    checked = tomoMedicacion.value,
                    onCheckedChange = {
                        tomoMedicacion.value = it
                        if (!it) alivio.value = false
                    }
                )
            }

            if (tomoMedicacion.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("¿Te alivió?")
                    Switch(
                        checked = alivio.value,
                        onCheckedChange = { alivio.value = it }
                    )
                }
            }

            // Red flag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = redFlag.value,
                    onCheckedChange = { redFlag.value = it }
                )
                Text("Red flag (señal de alarma)")
            }

            // Nota
            OutlinedTextField(
                value = nota.value,
                onValueChange = { nota.value = it },
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !guardando.value,
                onClick = {
                    val durStr = duracionMin.value.trim()
                    val durInt = durStr.toIntOrNull()

                    // Validación rápida
                    if (durStr.isNotEmpty() && durInt == null) {
                        Toast.makeText(context, "Duración inválida (usa números)", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (pacienteId == null) {
                        Toast.makeText(context, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
                        return@Button
                    }


                    guardando.value = true

                    val data = hashMapOf(
                        "fecha" to hoy,
                        "createdAt" to Timestamp.now(),
                        "intensidad" to intensidad.value,
                        "duracionMin" to durInt, // puede ser null si lo dejan vacío
                        "sintomas" to sintomasSeleccionados.value.toList(),
                        "tomoMedicacion" to tomoMedicacion.value,
                        "alivio" to alivio.value,
                        "redFlag" to redFlag.value,
                        "nota" to nota.value.trim(),
                        "pacienteId" to pacienteId,
                        )

                    db.collection("episodios")
                        .add(data)
                        .addOnSuccessListener { doc ->
                            guardando.value = false
                            Toast.makeText(context, "✅ Guardado en Firestore: ${doc.id}", Toast.LENGTH_LONG).show()

                            // (Opcional) limpiar formulario
                            // intensidad.value = 0
                            // duracionMin.value = ""
                            // nota.value = ""
                            // tomoMedicacion.value = false
                            // alivio.value = false
                            // redFlag.value = false
                            // sintomasSeleccionados.value = emptySet()
                        }
                        .addOnFailureListener { e ->
                            guardando.value = false
                            Toast.makeText(context, "❌ Error guardando: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            ) {
                Text(if (guardando.value) "Guardando..." else "Guardar episodio")
            }
        }
    }
}
