package org.example

import datas._
import org.scalameter._


class ScheduleBenchmark {
  private val flightSchedules = new FlightSchedules()
  private val flightSchedulesPar = new FlightSchedulesPar()

  private val origen = "ABQ"
  private val destination = "HOU"

  val airports: List[Aeropuerto] = aeropuertos
  val flights: List[Vuelo] = vuelosC1

  def benchmarkSchedulesTimeSeq(): Double = {
    val time = withWarmer(new Warmer.Default) measure {
      flightSchedules.schedulesTime(flights, airports)(origen, destination)
    }
    println(s"Tiempo promedio secuencial: ${time.value}")
    time.value
  }

  def benchmarkSchedulesTimePar(): Double = {
    val time = withWarmer(new Warmer.Default) measure {
      flightSchedulesPar.schedulesTime(flights, airports)(origen, destination)
    }
    println(s"Tiempo promedio paralelo: ${time.value}")
    time.value
  }

  def run(): Unit = {
    benchmarkSchedulesTimeSeq()
    benchmarkSchedulesTimePar()
    println("Fin de la ejecución")
  }
}
