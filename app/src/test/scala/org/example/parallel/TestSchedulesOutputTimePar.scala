package org.example.parallel

import datas.{aeropuertos, aeropuertosCurso, vuelosA1, vuelosB1, vuelosCurso}
import org.example.{FlightSchedules, FlightSchedulesPar, Itinerario, Vuelo}
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TestSchedulesOutputTimePar extends AnyFunSuite{

  val itoObj = new FlightSchedulesPar()
  val itsCurso: (String, String, Int, Int) => List[List[Vuelo]] = itoObj.schedulesOutputTime(vuelosCurso , aeropuertosCurso)
  val itsCurso15: (String, String, Int, Int) => List[List[Vuelo]] = itoObj.schedulesOutputTime(vuelosA1, aeropuertos)
  val itsCurso40: (String, String, Int, Int) => List[List[Vuelo]] = itoObj.schedulesOutputTime(vuelosB1, aeropuertos)

  test("test1") {
    val its1 = itsCurso("CTG", "PTY", 11, 40)
    assert(its1 === List(List(Vuelo("COPA", 1234, "CTG", 10, 0, "PTY", 11, 30, 0))))
  }

  test ("test2") {
    val its2 = itsCurso("CTG", "PTY", 11, 55)
    assert(its2 === List(List(Vuelo("AVA", 4321, "CTG", 9, 30, "SMR", 10, 0, 0), Vuelo("COPA", 7631, "SMR", 10, 50, "PTY", 11, 50, 0))))
  }

  test ("test3") {
    val its3 = itsCurso("CTG", "PTY", 12, 30)
    assert(its3 === List(List(Vuelo("AVA", 4321, "CTG", 9, 30, "SMR", 10, 0, 0), Vuelo("COPA", 7631, "SMR", 10, 50, "PTY", 11, 50, 0))))
  }

  test("test4") {
    val its4 = itsCurso15("HOU", "BNA", 19,20)
    assert(its4 === List(List(Vuelo("4X", 373, "HOU", 13, 15, "MSY", 15, 10, 1), Vuelo("AA", 828, "MSY", 17, 10, "BNA", 18, 37, 0))))
  }

  test("test5") {
    val its5 = itsCurso40("DFW", "ATL",16, 5)
    assert(its5 === List(List(Vuelo("AA", 864, "DFW", 6, 56, "ATL", 15, 3, 0))))
  }

  test("test6") {
    val its5 = itsCurso40("DEN", "MIA",18, 53)
    assert(its5 === List(List(Vuelo("AA", 50, "DEN", 13, 20, "DFW", 16, 24, 0), Vuelo("AA", 498, "DFW", 14, 25, "MIA", 18, 4, 0))))
  }

  test("test7") {
    val its5 = itsCurso40("DFW", "HOU",16, 5)
    assert(its5 === List(List(Vuelo("AA", 926, "DFW", 23, 16, "HOU", 12, 15, 0))))
  }
}
