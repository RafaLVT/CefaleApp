package com.gsti.cefaleapp.navigation

import ChatScreen
import PantallaCitasMedico
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gsti.cefaleapp.HomePacienteScreen
import com.gsti.cefaleapp.auth.LoginScreen
import com.gsti.cefaleapp.auth.RegisterScreen
import com.gsti.cefaleapp.ui.paciente.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gsti.cefaleapp.medico.ui.PantallaPacientesMedico
import com.gsti.cefaleapp.medico.ui.PantallaPrincipalMedico
import com.gsti.cefaleapp.medico.viewmodel.PacientesViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.gsti.cefaleapp.medico.ui.PantallaAsignarPaciente
import com.gsti.cefaleapp.medico.ui.PantallaDetallesPacienteMedico
import com.gsti.cefaleapp.medico.ui.PantallaFormularioPacienteMedico
import com.gsti.cefaleapp.medico.ui.PantallaCalendarioDolorMedico
import com.gsti.cefaleapp.medico.ui.PantallaChatMedico


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
                    .addOnSuccessListener { doc ->
                        val role = doc.getString("role")
                        val medicoId = doc.getString("medicoId")

                        when {
                            role == "medico" -> {
                                navController.navigate(Routes.HOME_MEDICO)
                            }

                            role == "paciente" && medicoId != null -> {
                                navController.navigate(Routes.HOME_PACIENTE)
                            }

                            role == "paciente" && medicoId == null -> {
                                navController.navigate(Routes.ESPERA_ASIGNACION)
                            }
                        }
                    }
            }
        }

        composable(Routes.ESPERA_ASIGNACION) {
            EsperaAsignacionScreen()
        }


        // 👤 HOME PACIENTE (LO QUE YA TENÍAS)
        composable(Routes.HOME_PACIENTE) {
            HomePacienteScreen(
                goEpisodio = { navController.navigate(Routes.EPISODIO) },
                goCalendario = { navController.navigate(Routes.CALENDARIO) },
                goCitas = { navController.navigate(Routes.CITAS) },
                goChat = { navController.navigate(Routes.CHAT) }
            )
        }

        composable(Routes.EPISODIO) { EpisodioScreen(navController) }
        composable(Routes.CALENDARIO) { CalendarioScreen(navController) }
        composable(Routes.CITAS) { CitasScreen(navController) }
        composable(Routes.CHAT) { ChatScreen(navController) }

        // 👨‍⚕️ HOME MÉDICO (placeholder por ahora)
        composable(Routes.HOME_MEDICO) {
            PantallaPrincipalMedico(
                onPacientesClick = {
                    navController.navigate(Routes.PACIENTES_MEDICO)
                },
                onCitasClick = {
                    navController.navigate(Routes.CITAS_MEDICO)
                }
            )
        }
        composable(Routes.PACIENTES_MEDICO) {
            val medicoId = FirebaseAuth.getInstance().currentUser!!.uid
            val viewModel = remember { PacientesViewModel() }
            val pacientes by viewModel.pacientesOrdenados.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.cargarPacientesPorGravedad(medicoId)
            }

            PantallaPacientesMedico(
                pacientes = pacientes,
                onAsignarPacienteClick = {
                    navController.navigate(Routes.ASIGNAR_PACIENTE)
                },
                onPacienteClick = { pacienteId ->
                    navController.navigate("${Routes.DETALLE_PACIENTE_MEDICO}/$pacienteId")
                }
            )
        }

        composable(
            route = "${Routes.DETALLE_PACIENTE_MEDICO}/{pacienteId}"
        ) { backStackEntry ->

            val pacienteId = backStackEntry.arguments?.getString("pacienteId")
                ?: return@composable

            PantallaDetallesPacienteMedico(
                pacienteId = pacienteId,
                onEditarFormularioClick = { id ->
                    navController.navigate("${Routes.FORMULARIO_PACIENTE_MEDICO}/$id")
                },
                onAntecedentesClick = { /* no nos corresponde, se debe updatear con datos de la clinica/hospital */ },
                onCalendarioClick = { id ->
                    navController.navigate("${Routes.CALENDARIO_DOLOR_MEDICO}/$id")
                },
                onChatClick = { id ->
                    navController.navigate("${Routes.CHAT_MEDICO}/$id")
                }
            )
        }

        composable(
            route = "${Routes.CALENDARIO_DOLOR_MEDICO}/{pacienteId}"
        ) { backStackEntry ->

            val pacienteId = backStackEntry.arguments?.getString("pacienteId")
                ?: return@composable

            PantallaCalendarioDolorMedico(
                pacienteId = pacienteId,
                onBack = { navController.popBackStack() }
            )
        }


        composable(
            route = "${Routes.FORMULARIO_PACIENTE_MEDICO}/{pacienteId}"
        ) { backStackEntry ->

            val pacienteId = backStackEntry.arguments?.getString("pacienteId")
                ?: return@composable

            val medicoId = FirebaseAuth.getInstance().currentUser!!.uid

            PantallaFormularioPacienteMedico(
                pacienteId = pacienteId,
                medicoId = medicoId,
                onFormularioGuardado = {
                    navController.popBackStack()
                }
            )
        }



        composable(Routes.ASIGNAR_PACIENTE) {
            val viewModel = remember { PacientesViewModel() }
            val medicoId = FirebaseAuth.getInstance().currentUser!!.uid

            PantallaAsignarPaciente(
                viewModel = viewModel,
                medicoId = medicoId,
                onPacienteAsignado = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CITAS_MEDICO) {
            val medicoId = FirebaseAuth.getInstance().currentUser!!.uid
            PantallaCitasMedico(medicoId)
        }

        composable("${Routes.CHAT_MEDICO}/{pacienteId}") { backStackEntry ->
            val pacienteId = backStackEntry.arguments?.getString("pacienteId")
                ?: return@composable

            val medicoId = FirebaseAuth.getInstance().currentUser!!.uid

            PantallaChatMedico(
                navController = navController,
                medicoId = medicoId,
                pacienteId = pacienteId
            )
        }








    }
}

