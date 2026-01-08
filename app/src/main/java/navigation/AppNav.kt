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
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.LOGIN
    ) {

        // 🔐 LOGIN
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { nav.navigate(Routes.DECIDE_HOME) },
                onGoRegister = { nav.navigate(Routes.REGISTER) }
            )
        }

        // 📝 REGISTER
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { nav.navigate(Routes.DECIDE_HOME) }
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
                            nav.navigate(Routes.HOME_MEDICO)
                        } else {
                            nav.navigate(Routes.HOME_PACIENTE)
                        }
                    }
            }
        }

        // 👤 HOME PACIENTE (LO QUE YA TENÍAS)
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

        // 👨‍⚕️ HOME MÉDICO (placeholder por ahora)
        composable(Routes.HOME_MEDICO) {
            // aquí luego pondremos PantallaPrincipalMedico()
        }
    }
}
