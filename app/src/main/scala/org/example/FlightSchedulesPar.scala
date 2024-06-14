package org.example
import common._

class FlightSchedulesPar {
  private val maxDepthGlobal = 4

  private def schedules(flights: List[Vuelo], airports: List[Aeropuerto], maxDepth: Int)(origen: String, destination: String): List[Itinerario] = {
    def findSchedules(origen: String, destination: String, visited: Set[String], depth: Int): List[List[Vuelo]] = {
      if (origen == destination) List(List())
      else {
        val outboundFlights = flights.filter(f => f.AirportOrigin == origen && !visited.contains(f.AirportDestination))
        if (depth >= maxDepth) {
          outboundFlights.flatMap(flight => {
            findSchedules(flight.AirportDestination, destination, visited + origen, depth + 1).map(flight :: _)
          })
        } else {
          val scheduleTasks = outboundFlights.map { flight =>
            task {
              findSchedules(flight.AirportDestination, destination, visited + origen, depth + 1).map(flight :: _)
            }
          }
          scheduleTasks.flatMap(_.join())
        }
      }
    }

    findSchedules(origen, destination, Set(), 0).map(flights => {
      val flightTotal = getFlightTotal(airports)(flights)
      val scales = flights.map(flight => flight.Scales).sum + flights.size - 1
      val flightTime = getFlightTime(airports)(flights)

      Itinerario(flights, flightTotal, scales, flightTime)
    })
  }

  def schedulesTime(flights: List[Vuelo], airports: List[Aeropuerto])(origen: String, destination: String): List[Itinerario] = {
    val allSchedules = schedules(flights, airports, maxDepthGlobal)(origen, destination)
    allSchedules.sortBy(_.tiempoTotal).take(3)
  }

  def schedulesScales(flights: List[Vuelo], airports: List[Aeropuerto])(origen: String, destination: String): List[Itinerario] = {
    val allSchedules = schedules(flights, airports, maxDepthGlobal)(origen, destination)
    allSchedules.sortBy(_.escalas).take(3)
  }

  def schedulesTimeLand(flights: List[Vuelo], airports: List[Aeropuerto])(origen: String, destination: String): List[Itinerario] = {
    val allSchedules = schedules(flights, airports, maxDepthGlobal)(origen, destination)
    allSchedules.sortBy(_.tiempoVuelo).take(3)
  }

  def schedulesOutputTime(flights: List[Vuelo], airports: List[Aeropuerto])(origen: String, destination: String, hour: Int, minute: Int): List[Itinerario] = {
    val allSchedules = schedules(flights, airports, maxDepthGlobal)(origen, destination)
    val (_, _, appointmentTime) = convertToHourUTC(hour, minute, getGMT(airports)(destination))

    val possibleSchedules = allSchedules.filter(schedule => {
      val arrival = schedule.vuelos.last
      val arrivalTime = convertToHourUTC(arrival.HL, arrival.ML, getGMT(airports)(arrival.AirportDestination))._3
      arrivalTime <= appointmentTime
    })

    if (possibleSchedules.isEmpty) List()
    else possibleSchedules.sortBy { itinerary =>
      val lastFlight = itinerary.vuelos.last
      (lastFlight.HL, lastFlight.ML)
    }(Ordering[(Int, Int)].reverse).take(1)
  }

  private def getFlightTotal(airports: List[Aeropuerto])(flights: List[Vuelo]) = {
    flights.foldLeft((0, 0)) { case ((totalMinutes, previousArrival), flight) =>
      val (_, _, outputUTC) = convertToHourUTC(flight.HS, flight.MS, getGMT(airports)(flight.AirportOrigin))
      val (_, _, arrivalUTC) = convertToHourUTC(flight.HL, flight.ML, getGMT(airports)(flight.AirportDestination))
      val flightDuration = if (arrivalUTC < outputUTC) arrivalUTC + 1440 - outputUTC else arrivalUTC - outputUTC

      val layoverDuration = if (previousArrival == 0) 0 else {
        val layover = outputUTC - previousArrival
        if (layover < 0) layover + 1440 else layover
      }

      (totalMinutes + flightDuration + layoverDuration, arrivalUTC)
    }._1
  }

  private def getFlightTime(airports: List[Aeropuerto])(flights: List[Vuelo]) = flights.map { flight =>
    val (_, _, outputUTC) = convertToHourUTC(flight.HS, flight.MS, getGMT(airports)(flight.AirportOrigin))
    val (_, _, arrivalUTC) = convertToHourUTC(flight.HL, flight.ML, getGMT(airports)(flight.AirportDestination))
    if (arrivalUTC < outputUTC) arrivalUTC + 1440 - outputUTC
    else arrivalUTC - outputUTC
  }.sum

  private def getGMT(airports: List[Aeropuerto])(codeAirport: String): Double = {
    airports.find(_.Cod == codeAirport).map(_.GMT).getOrElse(0.0)
  }

  private def convertToHourUTC(hour: Int, minutes: Int, gmtOffset: Double): (Int, Int, Int) = {
    val gmtOffsetHours = gmtOffset / 100
    val gmtOffsetMinutes = (gmtOffset % 100)

    var utcHour = hour - gmtOffsetHours
    var utcMinutes = minutes - gmtOffsetMinutes

    if (utcMinutes < 0) {
      utcMinutes += 60
      utcHour -= 1
    } else if (utcMinutes >= 60) {
      utcMinutes -= 60
      utcHour += 1
    }

    if (utcHour < 0) utcHour += 24
    if (utcHour >= 24) utcHour -= 24

    val totalMinutes = utcHour * 60 + utcMinutes

    (utcHour.toInt, utcMinutes.toInt, totalMinutes.toInt)
  }
}
