package org.example

class FlightSchedules {
  def schedules(flights: List[Vuelo], airports: List[Aeropuerto])(origen: String, destination: String): List[Itinerario] = {
    def findSchedules(origen: String, destination: String, visited: Set[String]): List[List[Vuelo]] = {
      if (origen == destination) List(List())
      else {
        val outboundFlights = flights.filter(f => f.AirportOrigin == origen && !visited.contains(f.AirportDestination)) // filtra los vuelos que salen del aeropuerto origen y que no han sido visitados para evitar ciclos
        outboundFlights.flatMap(flight => { // mapea los vuelos que salen del aeropuerto origen
          findSchedules(flight.AirportDestination, destination, visited + origen).map(flight :: _) // mapea los vuelos que llegan al aeropuerto destino
        })
      }
    }

    findSchedules(origen, destination, Set()).map(flights => { // Buscar los vuelos que salen del aeropuerto origen y llegan al aeropuerto destino
      val flightTotal = getFlightTotal(airports)(flights) // obtener el tiempo total de vuelo
      val scales = flights.map(flight => flight.Scales).sum + flights.size - 1 // obtener la cantidad de escalas eliminando la escala inicial (origen9
      val flightTime = getFlightTime(airports)(flights) // obtener el tiempo de vuelo

      Itinerario(flights, flightTotal, scales, flightTime)
    })
  }

  def schedulesTime(flights: List[Vuelo], airports: List[Aeropuerto])(origen: String, destination: String): List[List[Vuelo]] = {
    val allSchedules = schedules(flights, airports)(origen, destination)
    allSchedules.sortBy(_.tiempoTotal).take(3).map(_.vuelos)
  }

  def schedulesScales(flights: List[Vuelo], airports: List[Aeropuerto])(origen: String, destination: String): List[Itinerario] = {
    val allSchedules = schedules(flights, airports)(origen, destination)
    allSchedules.sortBy(_.escalas).take(3) // obtener los 3 primeros itinerarios con menor cantidad de escalas
  }

  def schedulesTimeLand(flights: List[Vuelo], airports: List[Aeropuerto])(origen: String, destination: String): List[Itinerario] = {
    val allSchedules = schedules(flights, airports)(origen, destination)
    allSchedules.sortBy(_.tiempoVuelo).take(3) // obtener los 3 primeros itinerarios con menor tiempo de vuelo
  }

  // Obtiene los itinerarios que llegan al aeropuerto destino antes de la hora de la cita
  def schedulesOutputTime(flights: List[Vuelo], airports: List[Aeropuerto])(origen: String, destination: String, hour: Int, minute: Int): List[Itinerario] = {
    val allSchedules = schedules(flights, airports)(origen, destination)
    val (_, _, appointmentTime) = convertToHourUTC(hour, minute, getGMT(airports)(destination))

    // Obtenemos los itinerarios que llegan al aeropuerto destino antes de la hora de la cita
    val possibleSchedules = allSchedules.filter(schedule => {
      val arrival = schedule.vuelos.last // obtener el último vuelo del itinerario
      val arrivalTime = convertToHourUTC(arrival.HL, arrival.ML, getGMT(airports)(arrival.AirportDestination))._3
      arrivalTime <= appointmentTime
    })

    // Si no hay itinerarios que lleguen antes de la hora de la cita, retornamos una lista vacía
    if(possibleSchedules.isEmpty) List()
    else possibleSchedules.sortBy { itinerary => // ordenamos los itinerarios por tiempo de llegada
      val lastFlight = itinerary.vuelos.last
      (lastFlight.HL, lastFlight.ML)
    }(Ordering[(Int, Int)].reverse).take(1) // obtenemos el itinerario con la hora de llegada más cercana a la hora de la cita
  }

  // Obtiene el tiempo total de vuelo de una lista de vuelos
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

  // Obtiene el tiempo de vuelo de una lista de vuelos
  private def getFlightTime(airports: List[Aeropuerto])(flights: List[Vuelo]) = flights.map { flight =>
      val (_, _, outputUTC) = convertToHourUTC(flight.HS, flight.MS, getGMT(airports)(flight.AirportOrigin))
      val (_, _, arrivalUTC) = convertToHourUTC(flight.HL, flight.ML, getGMT(airports)(flight.AirportDestination))
      if (arrivalUTC < outputUTC) arrivalUTC + 1440 - outputUTC
      else arrivalUTC - outputUTC
    }.sum

  // Obtiene el GMT de un aeropuerto (GMT es la diferencia de tiempo con respecto a UTC)
  private def getGMT(airports: List[Aeropuerto])(codeAirport: String): Double = {
    airports.find(_.Cod == codeAirport).map(_.GMT).getOrElse(0.0)
  }

  // Convierte la hora local a UTC
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
