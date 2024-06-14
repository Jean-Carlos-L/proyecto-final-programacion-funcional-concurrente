package org.example.parallel

import datas.{aeropuertosCurso, vuelosCurso}
import org.example.{FlightSchedulesPar, Itinerario, Vuelo}
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TestSchedulesTimeLandPar extends AnyFunSuite{
  val flightSchedulesPar = new FlightSchedulesPar()

  test("Test schedulesTimeLand 1") {
    val its1 = flightSchedulesPar.schedulesTimeLand(vuelosCurso, aeropuertosCurso)("MID", "SVCS")
    assert(its1 === List(
      Itinerario(List(Vuelo("AIRVZLA", 601, "MID", 5, 0, "SVCS", 6, 0, 0)), 60, 0, 60))
    )
  }

  test("Test schedulesTimeLand 2") {
    val its2 = flightSchedulesPar.schedulesTimeLand(vuelosCurso, aeropuertosCurso)("CLO", "SVCS")
    assert(its2 === List())
  }

  test("Test schedulesTimeLand 3") {
    val its3 = flightSchedulesPar.schedulesTimeLand(vuelosCurso, aeropuertosCurso)("CLO", "SVO")
    assert(its3 === List(
      Itinerario(List(Vuelo("AVA", 9432, "CLO", 7, 0, "SVO", 2, 20, 4)), 680, 4, 680),
      Itinerario(List(Vuelo("AVA", 9432, "CLO", 7, 0, "BOG", 8, 0, 0),
        Vuelo("IBERIA", 505, "BOG", 18, 0, "MAD", 12, 0, 0),
        Vuelo("IBERIA", 506, "MAD", 14, 0, "SVO", 23, 20, 0)), 1940, 2, 1220),
      Itinerario(List(Vuelo("AVA", 9432, "CLO", 7, 0, "BOG", 8, 0, 0),
        Vuelo("IBERIA", 505, "BOG", 18, 0, "MAD", 12, 0, 0),
        Vuelo("IBERIA", 507, "MAD", 16, 0, "SVO", 1, 20, 0)), 2060, 2, 1220)
    ))
  }

  test("Test schedulesTimeLand 4") {
    val its4 = flightSchedulesPar.schedulesTimeLand(vuelosCurso, aeropuertosCurso)("CLO", "MEX")
    assert(its4 === List(
      Itinerario(List(Vuelo("AVA", 9432, "CLO", 7, 0, "BOG", 8, 0, 0),
        Vuelo("LATAM", 787, "BOG", 17, 0, "MEX", 19, 0, 0)), 780, 1, 240),
      Itinerario(List(Vuelo("AVA", 9432, "CLO", 7, 0, "BOG", 8, 0, 0),
        Vuelo("VIVA", 756, "BOG", 9, 0, "MDE", 10, 0, 0),
        Vuelo("VIVA", 769, "MDE", 11, 0, "BAQ", 12, 0, 0),
        Vuelo("AVA", 5643, "BAQ", 14, 0, "MEX", 16, 0, 0)), 600, 3, 360)
    ))
  }

  test("Test schedulesTimeLand 5") {
    val its5 = flightSchedulesPar.schedulesTimeLand(vuelosCurso, aeropuertosCurso)("CTG", "PTY")
    assert(its5 === List(
      Itinerario(List(Vuelo("COPA", 1234, "CTG", 10, 0, "PTY", 11, 30, 0)), 90, 0, 90),
      Itinerario(List(Vuelo("AVA", 4321, "CTG", 9, 30, "SMR", 10, 0, 0), Vuelo("COPA", 7631, "SMR", 10, 50, "PTY", 11, 50, 0)), 140, 1, 90))
    )
  }
}
