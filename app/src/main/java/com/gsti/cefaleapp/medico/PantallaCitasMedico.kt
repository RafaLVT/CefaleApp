package com.gsti.cefaleapp.medico

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaCitasMedico() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Citas", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Text("Aquí aparecerán las citas del médico")
    }
}
