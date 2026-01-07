package com.gsti.cefaleapp.medico

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaChatMedico() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Chat médico – paciente")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Mensajes...")

        Spacer(modifier = Modifier.weight(1f))

        TextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Escribir mensaje") }
        )

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }
    }
}
