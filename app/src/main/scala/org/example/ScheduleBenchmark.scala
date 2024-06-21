package org.example

import datas._
import org.scalameter._


class ScheduleBenchmark {
  private val flightSchedules = new FlightSchedules()
  private val flightSchedulesPar = new FlightSchedulesPar()

  private val origen = "DFW"
  private val destination = "ATL"

  val airports: List[Aeropuerto] = aeropuertosCurso
  //val flights: List[Vuelo] = vuelosA1
  val flights: List[Vuelo] = vuelosB1
  //val flights: List[Vuelo] = vuelosC1
  //val flights: List[Vuelo] = vuelosD1

  def benchmarkSchedulesSeq(): Double = {
    val time = withWarmer(new Warmer.Default) measure {
      flightSchedules.schedules(flights, airports)(origen, destination)
    }
    println(s"Tiempo promedio secuencial: ${time.value}")
    time.value
  }

  def benchmarkSchedulesPar(): Double = {
    val time = withWarmer(new Warmer.Default) measure {
      flightSchedulesPar.schedules(flights, airports, 4)(origen, destination)
    }
    println(s"Tiempo promedio paralelo: ${time.value}")
    time.value
  }

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

  def benchmarkSchedulesScalesSeq(): Double = {
    val time = withWarmer(new Warmer.Default) measure {
      flightSchedules.schedulesScales(flights, airports)(origen, destination)
    }
    println(s"Tiempo promedio secuencial: ${time.value}")
    time.value
  }

  def benchmarkSchedulesScalesPar(): Double = {
    val time = withWarmer(new Warmer.Default) measure {
      flightSchedulesPar.schedulesScales(flights, airports)(origen, destination)
    }
    println(s"Tiempo promedio paralelo: ${time.value}")
    time.value
  }

  def benchmarkSchedulesTimeLandSeq(): Double = {
    val time = withWarmer(new Warmer.Default) measure {
      flightSchedules.schedulesTimeLand(flights, airports)(origen, destination)
    }
    println(s"Tiempo promedio secuencial: ${time.value}")
    time.value
  }

  def benchmarkSchedulesTimeLandPar(): Double = {
    val time = withWarmer(new Warmer.Default) measure {
      flightSchedulesPar.schedulesTimeLand(flights, airports)(origen, destination)
    }
    println(s"Tiempo promedio paralelo: ${time.value}")
    time.value
  }

  def benchmarkSchedulesOutputTimeSeq(): Double = {
    val time = withWarmer(new Warmer.Default) measure {
      flightSchedules.schedulesOutputTime(flights, airports)(origen, destination, 12, 0)
    }
    println(s"Tiempo promedio secuencial: ${time.value}")
    time.value
  }

  def benchmarkSchedulesOutputTimePar(): Double = {
    val time = withWarmer(new Warmer.Default) measure {
      flightSchedulesPar.schedulesOutputTime(flights, airports)(origen, destination, 12, 0)
    }
    println(s"Tiempo promedio paralelo: ${time.value}")
    time.value
  }

  def run(algorithmSeq: () => Double)(algorithmPar: () => Double): Unit = {
    algorithmSeq()
    algorithmPar()
    println("Fin de la ejecución")
  }
}
