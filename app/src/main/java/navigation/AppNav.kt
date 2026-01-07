package com.gsti.cefaleapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gsti.cefaleapp.HomePacienteScreen
import com.gsti.cefaleapp.ui.paciente.*

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.HOME_PACIENTE) {
        composable(Routes.HOME_PACIENTE) {
            HomePacienteScreen(
                goEpisodio = { nav.navigate(Routes.EPISODIO) },
                goCalendario = { nav.navigate(Routes.CALENDARIO) },
                goMedicacion = { nav.navigate(Routes.MEDICACION) },
                goCitas = { nav.navigate(Routes.CITAS) },
                goChat = { nav.navigate(Routes.CHAT) },
            )
        }
        composable(Routes.EPISODIO) { EpisodioScreen() }
        composable(Routes.CALENDARIO) { CalendarioScreen() }
        composable(Routes.MEDICACION) { MedicacionScreen() }
        composable(Routes.CITAS) { CitasScreen() }
        composable(Routes.CHAT) { ChatScreen() }
    }
}
