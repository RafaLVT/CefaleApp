package com.gsti.cefaleapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gsti.cefaleapp.HomePacienteScreen
import com.gsti.cefaleapp.auth.LoginScreen
import com.gsti.cefaleapp.auth.RegisterScreen
import com.gsti.cefaleapp.ui.paciente.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // 🔐 LOGIN
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.DECIDE_HOME) },
                onGoRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        // 📝 REGISTER
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Routes.DECIDE_HOME) }
            )
        }

        // 🔀 DECIDE SEGÚN ROL
        composable(Routes.DECIDE_HOME) {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val db = FirebaseFirestore.getInstance()

            LaunchedEffect(uid) {
                db.collection("users").document(uid).get()
                    .addOnSuccessListener {
                        val role = it.getString("role")
                        if (role == "medico") {
                            navController.navigate(Routes.HOME_MEDICO)
                        } else if (role == "paciente") {
                            navController.navigate(Routes.HOME_PACIENTE)
                        }
                    }
            }
        }

        // 👤 HOME PACIENTE (LO QUE YA TENÍAS)
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

        // 👨‍⚕️ HOME MÉDICO (placeholder por ahora)
        composable(Routes.HOME_MEDICO) {
            // aquí luego pondremos PantallaPrincipalMedico()
        }
    }
}

