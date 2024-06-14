package org.example.Sequential

import datas._
import org.example.{FlightSchedules, Itinerario, Vuelo}
import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class TestSchedulesOutputTimeSeq extends AnyFunSuite{
  val itoObj = new FlightSchedules()

  test("Test schedulesOutputTime 1") {
    val its1 = itoObj.schedulesOutputTime(vuelosCurso, aeropuertosCurso)("CTG", "PTY", 11, 40)
    assert(its1 === List(
      Itinerario( List(Vuelo("COPA", 1234, "CTG", 10, 0, "PTY", 11, 30, 0)), 90, 0, 90),
    ))
  }

  test("Test schedulesOutputTime 2") {
    val its2 = itoObj.schedulesOutputTime(vuelosCurso, aeropuertosCurso)("CTG", "PTY", 11, 55)
    assert(its2 === List(
      Itinerario(List(Vuelo("AVA", 4321, "CTG", 9, 30, "SMR", 10, 0, 0), Vuelo("COPA", 7631, "SMR", 10, 50, "PTY", 11, 50, 0)), 140, 1, 90)
    ))
  }

  test("Test schedulesOutputTime 3") {
    val its3 = itoObj.schedulesOutputTime(vuelosCurso, aeropuertosCurso)("CTG", "PTY", 12, 30)
    assert(its3 === List(
      Itinerario(List(Vuelo("AVA", 4321, "CTG", 9, 30, "SMR", 10, 0, 0), Vuelo("COPA", 7631, "SMR", 10, 50, "PTY", 11, 50, 0)), 140, 1, 90),
    ))
  }
}
