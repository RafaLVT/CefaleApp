package com.gsti.cefaleapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gsti.cefaleapp.HomePacienteScreen
import com.gsti.cefaleapp.ui.paciente.*

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME_PACIENTE) {

        composable(Routes.HOME_PACIENTE) {
            HomePacienteScreen(
                goEpisodio = { navController.navigate(Routes.EPISODIO) },
                goCalendario = { navController.navigate(Routes.CALENDARIO) },
                goMedicacion = { navController.navigate(Routes.MEDICACION) },
                goCitas = { navController.navigate(Routes.CITAS) },
                goChat = { navController.navigate(Routes.CHAT) }
            )
        }

        composable(Routes.EPISODIO) { EpisodioScreen(navController) }
        composable(Routes.CALENDARIO) { CalendarioScreen(navController) }
        composable(Routes.MEDICACION) { MedicacionScreen(navController) }
        composable(Routes.CITAS) { CitasScreen(navController) }
        composable(Routes.CHAT) { ChatScreen(navController) }

    }
}

