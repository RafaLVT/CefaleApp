package com.gsti.cefaleapp.medico.ui

import androidx.compose.ui.graphics.Color
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

fun colorPorIntensidad(intensidad: Int): Color {
    return when (intensidad) {
        1 -> Color(0xFF4CAF50) // verde
        2 -> Color(0xFFFFC107) // amarillo
        3 -> Color(0xFFF44336) // rojo
        else -> Color(0xFFBDBDBD) // gris
    }
}

/**
 * Devuelve una lista de semanas, cada semana es una lista de 7 LocalDate?
 */
fun generarCalendarioMes(mes: YearMonth): List<List<LocalDate?>> {
    val primerDiaMes = mes.atDay(1)
    val ultimoDiaMes = mes.atEndOfMonth()

    val inicioCalendario =
        primerDiaMes.minusDays(((primerDiaMes.dayOfWeek.value + 6) % 7).toLong())

    val semanas = mutableListOf<List<LocalDate?>>()
    var diaActual = inicioCalendario

    while (diaActual <= ultimoDiaMes || diaActual.dayOfWeek != DayOfWeek.MONDAY) {
        val semana = (0..6).map {
            val d = diaActual
            diaActual = diaActual.plusDays(1)
            if (d.month == mes.month) d else null
        }
        semanas.add(semana)
    }

    return semanas
}
