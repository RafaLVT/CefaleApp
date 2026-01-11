package com.gsti.cefaleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gsti.cefaleapp.navigation.AppNav
import com.gsti.cefaleapp.ui.theme.CefaleAppTheme
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CefaleAppTheme {
                AppNav()

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePacienteScreen(
    goEpisodio: () -> Unit,
    goCalendario: () -> Unit,
    goCitas: () -> Unit,
    goChat: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("CefaleApp - Paciente") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(modifier = Modifier.fillMaxWidth(), onClick = goEpisodio) { Text("Registrar episodio") }
            Button(modifier = Modifier.fillMaxWidth(), onClick = goCalendario) { Text("Calendario") }
            Button(modifier = Modifier.fillMaxWidth(), onClick = goCitas) { Text("Citas") }
            Button(modifier = Modifier.fillMaxWidth(), onClick = goChat) { Text("Chat") }
        }
    }
}

